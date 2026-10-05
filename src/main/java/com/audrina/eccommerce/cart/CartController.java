package com.audrina.eccommerce.cart;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addProductToCart(@PathVariable Long userId, @Valid @RequestBody CartItemRequest request) {
        return new ResponseEntity<>(cartService.addProductToCart(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> GetProductFromCart(@PathVariable Long userId) {
        return new ResponseEntity<>(cartService.getCart(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public  CartResponse removeProductFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        return  cartService.removeCart(userId, productId);
    }


}
