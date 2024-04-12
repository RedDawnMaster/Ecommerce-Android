package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.CartItemController;
import com.example.ecommerceapp.models.CartItem;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;
import java.util.List;

public class CartItemService {
    private static CartItemService cartItemService;

    private CartItemController cartItemController;
    private List<CartItem> cartItems;

    private CartItemService() {
        RetrofitService retrofitService = new RetrofitService();
        cartItemController = retrofitService.getRetrofit().create(CartItemController.class);
    }

    public static CartItemService getInstance() {
        if (cartItemService == null) cartItemService = new CartItemService();
        return cartItemService;
    }

    public CartItem save(CartItem cartItem, Long id) {
        try {
            return cartItemController.save(cartItem, id).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer deleteByProductLabelAndCartUserUsername(String label, String username) {
        try {
            return cartItemController.deleteByProductLabelAndCartUserUsername(label, username).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
