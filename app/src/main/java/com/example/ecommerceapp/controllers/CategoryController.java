package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryController {
    @GET("/api/category/")
    public Call<List<Category>> findAll();

    @DELETE("/api/category/{label}")
    public Call<Integer> deleteByLabel(@Path("label") String label);

    @POST("/api/category/")
    public Call<Category> save(@Body Category category);

    @PUT("/api/category/")
    public Call<Integer> update(@Body Category category);
}
