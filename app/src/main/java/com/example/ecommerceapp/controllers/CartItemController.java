package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.CartItem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartItemController {
    @DELETE("/api/cartItem/{label}/{username}")
    public Call<Integer> deleteByProductLabelAndCartUserUsername(@Path("label") String label, @Path("username") String username);

    @DELETE("/api/cartItem/{username}")
    public Call<Integer> deleteByCartUserUsername(@Path("username") String username);

    @POST("/api/cartItem/{id}")
    public Call<CartItem> save(@Body CartItem cartItem, @Path("id") Long id);
}
