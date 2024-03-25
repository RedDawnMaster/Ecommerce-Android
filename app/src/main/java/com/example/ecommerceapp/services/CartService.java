package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.CartController;
import com.example.ecommerceapp.retrofit.RetrofitService;

public class CartService {
    private static CartService cartService;

    private CartController cartController;

    private CartService() {
        RetrofitService retrofitService = new RetrofitService();
        cartController = retrofitService.getRetrofit().create(CartController.class);
    }

    public static CartService getInstance() {
        if (cartService == null) cartService = new CartService();
        return cartService;
    }

    public CartController getCartController() {
        return cartController;
    }
}
