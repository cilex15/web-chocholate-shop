package com.sergej.web_chocholate_shop.service;

import com.sergej.web_chocholate_shop.model.entity.Factory;
import com.sergej.web_chocholate_shop.model.entity.Product;
import com.sergej.web_chocholate_shop.model.entity.ProductRequest;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.model.enums.ProductRequestStatus;
import com.sergej.web_chocholate_shop.repository.ProductRequestRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductRequestService {

    private final ProductRequestRepository productRequestRepository;

    private final ProductService productService;

    private final FactoryService factoryService;

    public ProductRequestService(
            ProductRequestRepository productRequestRepository,
            ProductService productService,
            FactoryService factoryService) {

        this.productRequestRepository = productRequestRepository;
        this.productService = productService;
        this.factoryService = factoryService;
    }

    public List<ProductRequest> findAll() {

        return productRequestRepository.findAll();
    }

    public List<ProductRequest> findByCustomer(User customer) {

        return productRequestRepository.findByCustomerOrderByUpdatedAtDesc(customer);
    }

    public ProductRequest findById(Long id) {

        return productRequestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product request not found!"));
    }

    public ProductRequest create(ProductRequest request, User customer) {

        validateRequestFields(request);

        Factory factory =
                factoryService.findById(
                        request.getFactory().getId());

        request.setFactory(factory);
        request.setCustomer(customer);
        request.setStatus(ProductRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        return productRequestRepository.save(request);
    }

    public ProductRequest saveOffer(Long requestId, Product product) {

        ProductRequest request = findById(requestId);

        validateProductFields(product);

        if (request.getCreatedProduct() == null) {

            product.setCustomProductOwner(request.getCustomer());

            Product createdProduct =
                    productService.create(product);

            request.setCreatedProduct(createdProduct);

        } else {

            Product existingProduct =
                    productService.findById(
                            request.getCreatedProduct().getId());

            existingProduct.setName(product.getName());
            existingProduct.setCode(product.getCode());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setStockQuantity(product.getStockQuantity());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setFactory(product.getFactory());

            productService.update(existingProduct);
        }

        request.setName(product.getName());
        request.setCode(product.getCode());
        request.setPrice(product.getPrice());
        request.setStockQuantity(product.getStockQuantity());
        request.setDescription(product.getDescription());
        request.setFactory(
                factoryService.findById(
                        product.getFactory().getId()));
        request.setStatus(ProductRequestStatus.APPROVED);
        request.setUpdatedAt(LocalDateTime.now());

        return productRequestRepository.save(request);
    }

    public ProductRequest rejectRequest(Long requestId, String comment) {

        requireComment(comment);

        ProductRequest request = findById(requestId);

        request.setAdminRejectionComment(comment);
        request.setStatus(ProductRequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());

        return productRequestRepository.save(request);
    }

    public ProductRequest requestRevision(Long requestId, User customer, String comment) {

        requireComment(comment);

        ProductRequest request = findById(requestId);

        verifyCustomer(request, customer);

        request.setRevisionComment(comment);
        request.setStatus(ProductRequestStatus.REVISION_REQUESTED);
        request.setUpdatedAt(LocalDateTime.now());

        return productRequestRepository.save(request);
    }

    public ProductRequest rejectOffer(Long requestId, User customer, String comment) {

        requireComment(comment);

        ProductRequest request = findById(requestId);

        verifyCustomer(request, customer);

        request.setOfferRejectionComment(comment);
        request.setStatus(ProductRequestStatus.OFFER_REJECTED);
        request.setUpdatedAt(LocalDateTime.now());

        return productRequestRepository.save(request);
    }

    public void verifyCustomer(ProductRequest request, User customer) {

        if (!request.getCustomer().getId().equals(customer.getId())) {

            throw new RuntimeException(
                    "You don't have permission to manage this request.");
        }
    }

    private void validateRequestFields(ProductRequest request) {

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Name is required!");
        }

        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new RuntimeException("Code is required!");
        }

        validateProductFields(toProduct(request));
    }

    private void validateProductFields(Product product) {

        if (product.getName() == null || product.getName().isBlank()) {
            throw new RuntimeException("Name is required!");
        }

        if (product.getCode() == null || product.getCode().isBlank()) {
            throw new RuntimeException("Code is required!");
        }

        if (product.getPrice() == null
                || product.getPrice().signum() < 0) {
            throw new RuntimeException("Price cannot be negative!");
        }

        if (product.getStockQuantity() == null
                || product.getStockQuantity() < 0) {
            throw new RuntimeException("Stock quantity cannot be negative!");
        }

        if (product.getFactory() == null
                || product.getFactory().getId() == null) {
            throw new RuntimeException("Factory must be selected!");
        }
    }

    private Product toProduct(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setCode(request.getCode());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setFactory(request.getFactory());

        return product;
    }

    private void requireComment(String comment) {

        if (comment == null || comment.isBlank()) {

            throw new RuntimeException("Comment is required!");
        }
    }
}
