package com.audrina.eccommerce.product;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component

public class ProductMapper {

    public Product toEntity(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setCategory(productRequest.getCategory());
        product.setStock(productRequest.getStock());
        return product;
    }

    public  ProductResponse toResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setDescription(product.getDescription());
        productResponse.setStock(product.getStock());
        productResponse.setCategory(product.getCategory());
        return productResponse;
    }

    public void updateEntity( ProductRequest productRequest , Product existingProduct) {
        existingProduct.setName(productRequest.getName());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setCategory(productRequest.getCategory());
        existingProduct.setStock(productRequest.getStock());



    };
}
