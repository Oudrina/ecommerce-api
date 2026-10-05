package com.audrina.eccommerce.order;

import com.audrina.eccommerce.cart.Cart;
import com.audrina.eccommerce.cart.CartItem;
import com.audrina.eccommerce.cart.CartRepository;
import com.audrina.eccommerce.exception.*;
import com.audrina.eccommerce.product.Product;
import com.audrina.eccommerce.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse saveOrder(Long userId) {

        Cart cart = cartRepository.findByUserId(userId).orElseThrow(
                () -> new CartNotFoundException("Cart  not found with userId: " + userId)
        );

        if (cart.getItems().isEmpty()) {
            throw new CartItemNotFoundException("Cannot checkout an empty cart");
        }

        Order order = new Order();
        order.setUserId(cart.getUserId());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal calulateTotal = BigDecimal.ZERO;


        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(
                            () -> new ProductNotFoundException("Product not found with id: " + cartItem.getProductId())
                    );

            if (cartItem.getQuantity() > product.getStock()) {
                throw new NotEnoughStockException("Not enough stock for check out");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItem.setOrder(order);
            order.getOrderItemsList().add(orderItem);

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            calulateTotal = calulateTotal.add(subtotal);

            product.setStock(product.getStock() - cartItem.getQuantity());

        }
        order.setTotalAmount(calulateTotal);


        orderRepository.save(order);
        cart.getItems().clear();
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse getOderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found with id" + orderId)
        );
        return orderMapper.toOrderResponse(order);
    }

    public Page<OrderResponse> getAllOrders(int PageNumber, int PageSize) {

        Pageable pageable = PageRequest.of(PageNumber, PageSize);

        return orderRepository
                .findAll(pageable)
                .map(orderMapper::toOrderResponse);

    }

    public Page<OrderResponse> getOrdersByUser(Long userId, int PageNumber, int PageSize) {
        Pageable pageable = PageRequest.of(PageNumber, PageSize);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);

        return orders.map(orderMapper::toOrderResponse);

    }


}
