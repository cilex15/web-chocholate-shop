package com.sergej.web_chocholate_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sergej.web_chocholate_shop.model.entity.Factory;

import java.util.Optional;

@Repository
public interface FactoryRepository extends JpaRepository<Factory, Long> {

    Optional<Factory> findByPib(String pib);
}
