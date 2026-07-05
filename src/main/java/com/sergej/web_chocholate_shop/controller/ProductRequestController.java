package com.sergej.web_chocholate_shop.controller;

import com.sergej.web_chocholate_shop.model.entity.Product;
import com.sergej.web_chocholate_shop.model.entity.ProductRequest;
import com.sergej.web_chocholate_shop.model.entity.User;
import com.sergej.web_chocholate_shop.model.enums.Role;
import com.sergej.web_chocholate_shop.service.FactoryService;
import com.sergej.web_chocholate_shop.service.ProductRequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product-requests")
public class ProductRequestController {

    private final ProductRequestService productRequestService;

    private final FactoryService factoryService;

    public ProductRequestController(
            ProductRequestService productRequestService,
            FactoryService factoryService) {

        this.productRequestService = productRequestService;
        this.factoryService = factoryService;
    }

    @GetMapping("/create")
    public String createRequestPage(
            HttpSession session,
            Model model) {

        User loggedUser = requireCustomer(session);

        model.addAttribute("productRequest",
                new ProductRequest());

        model.addAttribute("factories",
                factoryService.findAll());

        return "pages/create-product-request";
    }

    @PostMapping("/create")
    public String createRequest(
            @ModelAttribute ProductRequest productRequest,
            HttpSession session) {

        User loggedUser = requireCustomer(session);

        productRequestService.create(productRequest, loggedUser);

        return "redirect:/products";
    }

    @GetMapping("/admin")
    public String adminRequestsPage(
            HttpSession session,
            Model model) {

        requireAdmin(session);

        model.addAttribute("productRequests",
                productRequestService.findAll());

        return "pages/product-requests";
    }

    @GetMapping("/admin/{id}/offer")
    public String offerPage(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        requireAdmin(session);

        ProductRequest request =
                productRequestService.findById(id);

        Product product =
                request.getCreatedProduct() != null
                        ? request.getCreatedProduct()
                        : createProductFromRequest(request);

        model.addAttribute("productRequest", request);
        model.addAttribute("product", product);
        model.addAttribute("factories", factoryService.findAll());

        return "pages/product-request-offer";
    }

    @PostMapping("/admin/{id}/offer")
    public String saveOffer(
            @PathVariable Long id,
            @ModelAttribute Product product,
            HttpSession session) {

        requireAdmin(session);

        productRequestService.saveOffer(id, product);

        return "redirect:/product-requests/admin";
    }

    @PostMapping("/admin/{id}/reject")
    public String rejectRequest(
            @PathVariable Long id,
            @RequestParam String comment,
            HttpSession session) {

        requireAdmin(session);

        productRequestService.rejectRequest(id, comment);

        return "redirect:/product-requests/admin";
    }

    @PostMapping("/{id}/revision")
    public String requestRevision(
            @PathVariable Long id,
            @RequestParam String comment,
            HttpSession session) {

        User loggedUser = requireCustomer(session);

        productRequestService.requestRevision(id, loggedUser, comment);

        return "redirect:/products";
    }

    @PostMapping("/{id}/reject-offer")
    public String rejectOffer(
            @PathVariable Long id,
            @RequestParam String comment,
            HttpSession session) {

        User loggedUser = requireCustomer(session);

        productRequestService.rejectOffer(id, loggedUser, comment);

        return "redirect:/products";
    }

    private Product createProductFromRequest(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setCode(request.getCode());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setDescription(request.getDescription());
        product.setFactory(request.getFactory());

        return product;
    }

    private User requireCustomer(HttpSession session) {

        User loggedUser =
                (User) session.getAttribute("loggedUser");

        if (loggedUser == null) {

            throw new RuntimeException("Login is required!");
        }

        if (loggedUser.getRole() != Role.CUSTOMER) {

            throw new RuntimeException("Only customers can manage product requests.");
        }

        return loggedUser;
    }

    private void requireAdmin(HttpSession session) {

        User loggedUser =
                (User) session.getAttribute("loggedUser");

        if (loggedUser == null
                || loggedUser.getRole() != Role.ADMIN) {

            throw new RuntimeException("Admin permission is required!");
        }
    }
}
