package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.model.entity.Factory;
import com.sergej.web_chocholate_shop.repository.FactoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FactoryService {

    private final FactoryRepository factoryRepository;

    public FactoryService(FactoryRepository factoryRepository) {
        this.factoryRepository = factoryRepository;
    }

    public List<Factory> findAll() {
        return factoryRepository.findAll();
    }

    public Factory findById(Long id) {

        return factoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Factory not found!"));
    }

    public Factory findByPib(String pib) {

        return factoryRepository.findByPib(pib)
                .orElseThrow(() ->
                        new RuntimeException("Factory not found!"));
    }

    public boolean existsByPib(String pib) {

        return factoryRepository.findByPib(pib).isPresent();
    }

    public Factory create(Factory factory) {

        if (existsByPib(factory.getPib())) {
            throw new RuntimeException("PIB already exists!");
        }

        return factoryRepository.save(factory);
    }

    public Factory update(Factory factory) {

        findById(factory.getId());

        return factoryRepository.save(factory);
    }

    public void deleteById(Long id) {

        findById(id);

        factoryRepository.deleteById(id);
    }


}