package com.sergej.web_chocholate_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sergej.web_chocholate_shop.model.entity.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {

}
