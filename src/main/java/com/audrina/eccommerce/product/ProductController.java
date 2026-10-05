package com.audrina.eccommerce.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>
                (productService.createProduct(productRequest)
                        , HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@Valid @PathVariable Long id) {
        return new ResponseEntity<>
                (productService.getProductById(id),
                        HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProduct( @RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseEntity<>
                (productService.getProducts(pageNumber, pageSize),
                        HttpStatus.OK);
    }

    public ResponseEntity<Page<ProductResponse>> getProductByPage(String category, BigDecimal maxPrice, int pageNumber, int pageSize) {
        return new ResponseEntity<>
                (productService
                        .getAllProducts(category, maxPrice, pageNumber, pageSize)
                        , HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService
                .updateProduct(id, productRequest)
                , HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@Valid @PathVariable Long id) {
        productService.deleteProduct(id);
    }

}
