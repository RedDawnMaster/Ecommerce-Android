package org.sop.ecommerceapp.controllers;

import org.sop.ecommerceapp.models.Cart;
import org.sop.ecommerceapp.models.Response;
import org.sop.ecommerceapp.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/{username}")
    public Cart findByUserUsername(@PathVariable String username) {
        return cartService.findByUserUsername(username);
    }

    @GetMapping("/checkout/{username}")
    public Response checkout(@PathVariable String username) {
        return cartService.checkout(username);
    }

    @GetMapping("/getCartTotal/{id}")
    public double getCartTotal(@PathVariable Long id) {
        return cartService.getCartTotal(id);
    }


}
