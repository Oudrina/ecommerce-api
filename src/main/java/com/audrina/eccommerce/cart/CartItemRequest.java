package com.audrina.eccommerce.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemRequest {
    @NotNull
    @Min( value = 1, message = "ProductId must be provided")
    private  Long productId;
    @NotNull
    @Min( value = 1, message = "Quantity must be specified")
    private  Integer quantity;
}
