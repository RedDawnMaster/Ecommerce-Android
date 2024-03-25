package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.Statistic;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StatisticController {
    @GET("/api/statistic/")
    public Call<Statistic> findById();
}
