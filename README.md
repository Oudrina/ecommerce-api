# E-Commerce Backend — Project 2 Design Document

## Scope Statement (read this before writing any code)

This project exists to practice **Stage 2 concepts**: layered architecture, optimistic/pessimistic locking, pagination/sorting/filtering, N+1 avoidance, and API contracts.

**In scope:**
- Product catalog (CRUD + search/filter/sort/paginate)
- Cart (add/remove/view items)
- Checkout → Order creation with **concurrency-safe stock deduction**
- Order history (paginated)

**Explicitly OUT of scope for this project** (deferred to later stages per the roadmap):
- Authentication/authorization (Stage 3 — Security). Use a plain `userId` field (Long), no login system, no JWT.
- Payment gateway integration (Stage 4 — Fintech Domain Core)
- Async processing / message queues (Stage 5)
- Microservices split (Stage 6)

If you find yourself building any of the "out of scope" items while working on this project, stop — that's scope creep. Note it down and move on.

---

## Entity Relationship Design

```
User (simplified — no auth)
  └── id, name, email

Product
  ├── id, name, description, price, stock, category
  └── version (for optimistic locking)

Cart
  ├── id, userId
  └── 1 ── * CartItem

CartItem
  ├── id, cartId, productId, quantity

Order
  ├── id, userId, status, totalAmount, createdAt
  └── 1 ── * OrderItem

OrderItem
  ├── id, orderId, productId, quantity, priceAtPurchase
```

**Relationship notes:**
- `Cart` → `CartItem`: one-to-many. A cart holds many line items.
- `Order` → `OrderItem`: one-to-many. Mirrors CartItem, but **snapshots** `priceAtPurchase` — prices can change later; an order must preserve what was actually charged.
- `Product` is referenced by ID from `CartItem`/`OrderItem`, not embedded — avoids duplicating product data and keeps the N+1 discussion relevant (you'll need `JOIN FETCH` when loading an order with its items and their product details).

---

## Entities (Java sketch)

### Product
```java
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private String category;

    @Version
    private Integer version;   // optimistic locking
}
```

### Cart / CartItem
```java
@Entity
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}

@Entity
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;
}
```

### Order / OrderItem
```java
@Entity
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;   // PENDING, CONFIRMED, CANCELLED

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();
}

@Entity
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal priceAtPurchase;
}
```

---

## API Contract

Every endpoint below is a **promise**: exact request shape in, exact response shape + status code out. Keep this updated as the source of truth while building — if behavior and this doc disagree, one of them is wrong.

### Products

| Method | Path | Request Body | Success | Failure |
|---|---|---|---|---|
| `POST` | `/products` | `ProductRequest` | `201` + `ProductResponse` | `400` invalid input |
| `GET` | `/products/{id}` | — | `200` + `ProductResponse` | `404` not found |
| `GET` | `/products?category=&maxPrice=&page=&size=&sort=` | — | `200` + `Page<ProductResponse>` | `400` invalid query params |
| `PUT` | `/products/{id}` | `ProductRequest` | `200` + `ProductResponse` | `404` / `400` |
| `DELETE` | `/products/{id}` | — | `204` | `404` |

### Cart

| Method | Path | Request Body | Success | Failure |
|---|---|---|---|---|
| `POST` | `/cart/{userId}/items` | `AddCartItemRequest {productId, quantity}` | `200` + `CartResponse` | `400` invalid qty, `404` product not found |
| `DELETE` | `/cart/{userId}/items/{productId}` | — | `200` + `CartResponse` | `404` |
| `GET` | `/cart/{userId}` | — | `200` + `CartResponse` | `404` empty/no cart |

### Checkout / Orders

| Method | Path | Request Body | Success | Failure |
|---|---|---|---|---|
| `POST` | `/orders/checkout/{userId}` | — (reads from cart) | `201` + `OrderResponse` | `409` insufficient stock / conflict, `400` empty cart |
| `GET` | `/orders/{id}` | — | `200` + `OrderResponse` | `404` |
| `GET` | `/orders/user/{userId}?page=&size=` | — | `200` + `Page<OrderResponse>` | `400` |

**Note the `409 Conflict` on checkout** — this is the correct status code for "your request conflicted with the current state of the resource" (e.g., stock ran out between viewing the product and checking out, or an `OptimisticLockException`/deadlock was caught). Not `400` (that implies the request itself was malformed) and not `500` (that implies a server bug).

### Structured error response (applies to every endpoint)

```json
{
  "timestamp": "2026-09-25T10:00:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "quantity must be greater than 0",
  "path": "/cart/1/items"
}
```

This fixes review issue #3 (no structured error model) from Project 1 — build this once, reuse it everywhere via `@ControllerAdvice`.

---

## Where each Stage 2 concept gets applied

| Concept | Where it's used |
|---|---|
| Layered architecture | Every feature: Controller → Service → Repository |
| Optimistic locking (`@Version`) | `Product.stock` deduction during checkout |
| Pessimistic locking (`@Lock`) | Alternative implementation to compare against optimistic, on the same checkout flow — build both, benchmark the difference |
| Pagination | `GET /products`, `GET /orders/user/{userId}` |
| Sorting | `GET /products?sort=price,asc` |
| Filtering | `GET /products?category=&maxPrice=` |
| N+1 avoidance | Loading an `Order` with its `OrderItem`s (and their `Product` details) — use `JOIN FETCH` |
| Structured error handling | Every endpoint, via one shared `@ControllerAdvice` |
| Deadlock awareness | Checkout locking two+ products in one transaction — always lock in a **consistent order** (e.g., by ascending `productId`) to prevent circular waits |
| Connection pool discipline | Keep `@Transactional` checkout methods short — no external calls inside the transaction |

---

## Build order (suggested)

1. `Product` entity + repository + CRUD endpoints (no locking yet — just re-establish the CRUD pattern)
2. Add pagination/sorting/filtering to `GET /products`
3. `Cart`/`CartItem` — add/remove/view
4. `Order`/`OrderItem` entities (no checkout logic yet)
5. Checkout with **optimistic locking** — write it, then write a test that proves a stale save gets rejected
6. Checkout with **pessimistic locking** — same flow, alternate implementation, compare
7. Structured error handling (`@ControllerAdvice` + `ErrorResponse`) — retrofit across everything built so far
8. Full unit + integration test suite (same discipline as Project 1)

---

