package com.audrina.eccommerce.product;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "Product name cannot be empty")
    private String name;
    @NotBlank(message = "Product description cannot be empty")
    private String description;

    @NotNull(message = "Price must be greater than 0")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")

    private BigDecimal price;

    @NotNull(message = "Stock must not be null")
    @Min(value = 1, message = "Stock must be greater than 1")
    private int stock;

    @NotBlank(message = "Product category cannot be empty")
    private String category;
}
