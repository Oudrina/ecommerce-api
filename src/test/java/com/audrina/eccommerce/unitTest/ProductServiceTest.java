package com.audrina.eccommerce.unitTest;

import com.audrina.eccommerce.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductMapper productMapper;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void  setUp(){
        product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setDescription("Product 1 Description");
        product.setPrice(BigDecimal.valueOf(100.00));
        product.setCategory("Category 1");
        product.setStock(3);

        productRequest = new ProductRequest();
        productRequest.setName("Product 1");
        productRequest.setDescription("Product 1 Description");
        productRequest.setPrice(BigDecimal.valueOf(100.00));
        productRequest.setCategory("Category 1");
        productRequest.setStock(3);

        productResponse = new ProductResponse();
        productResponse.setName("Product 1");
        productResponse.setDescription("Product 1 Description");
        productResponse.setPrice(BigDecimal.valueOf(100.00));
        productResponse.setCategory("Category 1");
        productResponse.setStock(3);

    }

    @Test
    void  createProduct_whenProductDoesNotExist() {
//Find product my   name and category
        when(productRepository.findByNameAndCategory(
                        product.getName()
                        ,product.getCategory()))
                .thenReturn(Optional.empty());


//        Arrange
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
//Act

    ProductResponse result =   productService.createProduct(productRequest);

//      Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(productResponse);
      verify(productRepository).save(product);
    }

    
}
