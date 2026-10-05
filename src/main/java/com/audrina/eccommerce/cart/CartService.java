package com.audrina.eccommerce.cart;

import com.audrina.eccommerce.exception.CartItemNotFoundException;
import com.audrina.eccommerce.exception.CartNotFoundException;
import com.audrina.eccommerce.exception.ProductNotFoundException;
import com.audrina.eccommerce.exception.UserNotFoundException;
import com.audrina.eccommerce.product.Product;
import com.audrina.eccommerce.product.ProductRepository;
import com.audrina.eccommerce.user.User;
import com.audrina.eccommerce.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    @Transactional
    public CartResponse addProductToCart(Long userId, CartItemRequest request) {

//        1. Check user exists

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException("User Not Found with the id " + userId)
                );

        //        2. Check product exists

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ProductNotFoundException("Product Not Found with the id " + request.getProductId()));

//        3. Find/create cart
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(
                        () -> {
                            Cart newCart = new Cart();
                            newCart.setUserId(userId);
                            newCart.setItems(new ArrayList<>());
                            return newCart;
                        }
                );

        CartItem item = cart.getItems().stream().filter(
                cartItem -> cartItem.getProductId().equals(product.getId())
        ).findFirst().orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + request.getQuantity());

        } else {
            CartItem cartItem = cartMapper.toEntity(request);
            cartItem.setCart(cart);

            //        5. Attach CartItem to Cart
            cart.getItems().add(cartItem);
        }

        //        6. Save Cart
        Cart responseCart = cartRepository.save(cart);

        return cartMapper.toResponse(responseCart);

    }

    @Transactional
    public CartResponse getCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new UserNotFoundException("User Not Found with the id " + userId)
                );
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(
                () -> new CartNotFoundException("Cart not not found with the user id " + userId)
        );

        return cartMapper.toResponse(cart);

    }

    @Transactional
    public CartResponse removeCart(Long userId, Long productId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new CartNotFoundException("Cart not found with the user id " + userId)
                );
        CartItem existingCartItem = cart.getItems().stream().filter(
                        item -> item.getProductId().equals(productId))
                .findFirst().orElseThrow(() -> new CartItemNotFoundException("Product not found with the product id " + productId + " in the cart"));

        cart.getItems().remove(existingCartItem);

        cartRepository.save(cart);
        return cartMapper.toResponse(cart);

    }


}
