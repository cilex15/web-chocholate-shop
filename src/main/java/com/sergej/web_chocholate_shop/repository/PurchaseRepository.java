package com.sergej.web_chocholate_shop.repository;

import com.sergej.web_chocholate_shop.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sergej.web_chocholate_shop.model.entity.Purchase;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByUser(User user);

    List<Purchase> findByUserAndPurchaseDateTimeBetween(User user, LocalDateTime from, LocalDateTime to);

}
