package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductController {
    @GET("/id/{id}")
    public Call<Product> findById(@Path("id") Long id);

    @GET("/api/product/{label}")
    public Call<List<Product>> findByLabelContains(@Path("label") String label);

    @GET("/api/product/category/{label}")
    public Call<List<Product>> findByCategoryLabel(@Path("label") String label);

    @GET("/api/product/{lowerBoundPrice}/{higherBoundPrice}")
    public Call<List<Product>> findByPriceBetween(@Path("lowerBoundPrice") double lowerBoundPrice,
                                                  @Path("higherBoundPrice") double higherBoundPrice);

    @GET("/api/product/")
    public Call<List<Product>> findAll();

    @GET("/api/product/countProductsByPrice/{lowerBoundPrice}/{higherBoundPrice}")
    public Call<Integer> countByPriceBetween(@Path("lowerBoundPrice") double lowerBoundPrice, @Path("higherBoundPrice") double higherBoundPrice);

    @GET("/api/product/countByCategoryLabel/{label}")
    public Call<Integer> countByCategoryLabel(@Path("label") String label);

    @DELETE("/api/product/{label}")
    public Call<Response> deleteByLabel(@Path("label") String label);

    @POST("/api/product/")
    public Call<Product> save(@Body Product product);

    @PUT("/api/product/")
    public Call<Product> update(@Body Product product);
}
