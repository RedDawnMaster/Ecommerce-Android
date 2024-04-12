package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.Cart;
import com.example.ecommerceapp.models.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CartController {
    @GET("/api/cart/{username}")
    public Call<Cart> findByUserUsername(@Path("username") String username);

    @GET("/api/cart/checkout/{username}")
    public Call<Response> checkout(@Path("username") String username);

    @GET("/api/cart/getCartTotal/{id}")
    public Call<Double> getCartTotal(@Path("id") Long id);

}
