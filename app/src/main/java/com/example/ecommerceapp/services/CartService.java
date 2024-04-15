package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.CartController;
import com.example.ecommerceapp.models.Cart;
import com.example.ecommerceapp.models.Response;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;

public class CartService {
    private static CartService cartService;

    private CartController cartController;
    private Cart cart;

    private CartService() {
        RetrofitService retrofitService = new RetrofitService();
        cartController = retrofitService.getRetrofit().create(CartController.class);
    }

    public static CartService getInstance() {
        if (cartService == null) cartService = new CartService();
        return cartService;
    }

    public Cart findByUserUsername(String username) {
        try {
            return cart = cartController.findByUserUsername(username).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Response checkout(String username) {
        try {
            return cartController.checkout(username).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Cart getCart() {
        return cart;
    }
}
