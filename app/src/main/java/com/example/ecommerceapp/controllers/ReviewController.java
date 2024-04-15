package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReviewController {
    @GET("/api/review/{label}/{username}")
    public Call<List<Review>> findByProductLabelAndUserUsernameContains(@Path("label") String label,
                                                                        @Path("username") String username);

    @GET("/api/review/{label}")
    public Call<List<Review>> findByProductLabel(@Path("label") String label);

    @DELETE("/api/review/{username}/{label}")
    public Call<Void> deleteByUserUsernameAndProductLabel(@Path("username") String username, @Path("label") String label);

    @POST("/api/review/")
    public Call<Review> save(@Body Review review);

    @PUT("/api/review/")
    public Call<Void> update(@Body Review review);
}
