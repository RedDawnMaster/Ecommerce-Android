package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderController {
    @GET("/api/order/{username}")
    public Call<List<Order>> findByUserUsername(@Path("username") String username);

    @GET("/api/order/id/{id}")
    public Call<Boolean> checkRefundable(@Path("id") Long id);
}
