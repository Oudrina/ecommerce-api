package com.audrina.eccommerce.cart;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    public  CartItem toEntity(CartItemRequest request){
        CartItem cartItem = new CartItem();
        cartItem.setProductId(request.getProductId());
        cartItem.setQuantity(request.getQuantity());
        return cartItem;
    }


    public CartItemResponse toCartItemResponse(CartItem cartItem){
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setProductId(cartItem.getProductId());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        return cartItemResponse;
    }

    public  CartResponse toResponse(Cart cart){
        CartResponse cartResponse = new CartResponse();
        cartResponse.setUserId(cart.getUserId());

        List<CartItemResponse> cartItemResponses = cart.getItems()
                .stream().map(
                this::toCartItemResponse
        ).toList();

        cartResponse.setResponses(cartItemResponses);
        return cartResponse;

    }


}
