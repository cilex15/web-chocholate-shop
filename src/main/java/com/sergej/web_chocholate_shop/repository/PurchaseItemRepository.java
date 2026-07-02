package com.sergej.web_chocholate_shop.repository;

import com.sergej.web_chocholate_shop.model.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sergej.web_chocholate_shop.model.entity.PurchaseItem;

import java.util.List;

@Repository
public interface PurchaseItemRepository extends JpaRepository<PurchaseItem,Long> {

    @Query("""
SELECT pi.product
FROM PurchaseItem pi
GROUP BY pi.product
ORDER BY SUM(pi.quantity) DESC
""")
    List<Product> findBestSellingProducts(Pageable pageable);
}
