package com.sergej.web_chocholate_shop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.sergej.web_chocholate_shop.model.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    List<Product> findByNameContaining(String name);


    @Query("""
SELECT p
FROM Product p
WHERE
(:code IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%', :code, '%')))
AND
(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
AND
(:factoryId IS NULL OR p.factory.id = :factoryId)
AND
(:minStock IS NULL OR p.stockQuantity >= :minStock)
AND
(:maxStock IS NULL OR p.stockQuantity <= :maxStock)
AND
(:minPrice IS NULL OR p.price >= :minPrice)
AND
(:maxPrice IS NULL OR p.price <= :maxPrice)
""")
    List<Product> searchProducts(
            @Param("code") String code,
            @Param("name") String name,
            @Param("factoryId") Long factoryId,
            @Param("minStock") Integer minStock,
            @Param("maxStock") Integer maxStock,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("sort") Sort sort
    );

    @Query("""
SELECT p
FROM Product p
WHERE p.discount IS NOT NULL
AND p.discount.startDateTime <= :now
AND p.discount.endDateTime >= :now
""")
    List<Product> findDiscountedProducts(@Param("now") LocalDateTime now);
}
