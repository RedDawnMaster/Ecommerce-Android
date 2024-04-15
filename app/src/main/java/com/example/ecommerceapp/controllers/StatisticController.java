package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.Statistic;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface StatisticController {
    @GET("/api/statistic/")
    public Call<Statistic> findById();

    @POST("/api/statistic/")
    public Call<Statistic> save(@Body Statistic statistic);
}
