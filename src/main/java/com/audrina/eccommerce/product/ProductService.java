package com.audrina.eccommerce.product;

import com.audrina.eccommerce.exception.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    //    TODO: CREATE PRODUCT
    public ProductResponse createProduct(ProductRequest request) {

        Product existingProduct = productRepository
                .findByNameAndCategory(request.getName(), request.getCategory())
                .orElse(null);


        if (existingProduct != null) {
            existingProduct.setStock(existingProduct.getStock() + request.getStock());
            existingProduct.setPrice(request.getPrice());
            productRepository.save(existingProduct);

            return productMapper.toResponse(existingProduct);
        }

        Product createdProduct = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(createdProduct);
        return productMapper.toResponse(savedProduct);


    }

    //    TODO GET 1 PRODUCT
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found")
        );
        return productMapper.toResponse(product);

    }

    //    TODO GET PRODUCT
    public Page<ProductResponse> getProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        return productRepository
                .findAll(pageable)
                .map(productMapper::toResponse);
    }

    //    TODO GET PRODUCTS  BY CATEGORY
    public List<ProductResponse> getProductByCategory(String category) {
        return productRepository.findProductByCategory(category)
                .stream().map(productMapper::toResponse)
                .toList();

    }

    public Page<ProductResponse> getAllProducts(String category, BigDecimal maxPrice, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of
                (pageNumber,
                        pageSize,
                        Sort.by("price").ascending());

        return productRepository.findByCategoryAndPriceLessThan(category, maxPrice, pageable);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new ProductNotFoundException("Product not found" + "with id" + id)
                );

        productMapper.updateEntity(request, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);

    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found" + "with id" + id)
        );
        productRepository.delete(product);
    }

}
