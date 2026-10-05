package com.audrina.eccommerce.cart;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private  Long userId;
    private List<CartItemResponse>  responses = new ArrayList<>();
}
