package com.audrina.eccommerce.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findProductByCategory(String category);
    Page<ProductResponse> findByCategoryAndPriceLessThan(String category, BigDecimal priceIsLessThan,Pageable pageable);
    Optional<Product> findByNameAndCategory(String name, String category);

//    Product findByUserId(Long userId);
}
