package org.sop.ecommerceapp.controllers;

import org.sop.ecommerceapp.models.CartItem;
import org.sop.ecommerceapp.services.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/cartItem")
public class CartItemController {
    @Autowired
    private CartItemService cartItemService;

    @DeleteMapping("/{label}/{username}")
    public int deleteByProductLabelAndCartUserUsername(@PathVariable String label, @PathVariable String username) {
        return cartItemService.deleteByProductLabelAndCartUserUsername(label, username);
    }

    @DeleteMapping("/{username}")
    public int deleteByCartUserUsername(@PathVariable String username) {
        return cartItemService.deleteByCartUserUsername(username);
    }

    @PostMapping("/{id}")
    public CartItem save(@RequestBody CartItem cartItem, @PathVariable Long id) {
        return cartItemService.save(cartItem, id);
    }
}
