package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.CartItemController;
import com.example.ecommerceapp.retrofit.RetrofitService;

public class CartItemService {
    private static CartItemService cartItemService;

    private CartItemController cartItemController;

    private CartItemService() {
        RetrofitService retrofitService = new RetrofitService();
        cartItemController = retrofitService.getRetrofit().create(CartItemController.class);
    }

    public static CartItemService getInstance() {
        if (cartItemService == null) cartItemService = new CartItemService();
        return cartItemService;
    }

    public CartItemController getCartItemController() {
        return cartItemController;
    }
}
