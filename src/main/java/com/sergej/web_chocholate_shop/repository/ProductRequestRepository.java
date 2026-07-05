package com.sergej.web_chocholate_shop.repository;

import com.sergej.web_chocholate_shop.model.entity.ProductRequest;
import com.sergej.web_chocholate_shop.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRequestRepository extends JpaRepository<ProductRequest, Long> {

    List<ProductRequest> findByCustomerOrderByUpdatedAtDesc(User customer);
}
