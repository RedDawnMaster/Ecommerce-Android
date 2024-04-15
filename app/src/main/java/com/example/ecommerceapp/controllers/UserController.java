package com.example.ecommerceapp.controllers;

import com.example.ecommerceapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserController {
    @GET("/api/user/{username}/{password}")
    public Call<User> login(@Path("username") String username, @Path("password") String password);

    @GET("/api/user/username/{username}")
    public Call<List<User>> findByUsernameContains(@Path("username") String username);

    @GET("/api/user/email/{email}")
    public Call<List<User>> findByEmailContains(@Path("email") String email);

    @GET("/api/user/{role}")
    public Call<List<User>> findByRole(@Path("role") String role);

    @GET("/api/user/")
    public Call<List<User>> findAll();

    @GET("/api/user/addToWishList/{username}/{label}")
    public Call<Integer> addToWishList(@Path("username") String username, @Path("label") String label);

    @GET("/api/user/removeFromWishList/{username}/{label}")
    public Call<Void> removeFromWishList(@Path("username") String username, @Path("label") String label);

    @DELETE("/api/user/{username}")
    public Call<Void> deleteByUsername(@Path("username") String username);

    @POST("/api/user/")
    public Call<User> register(@Body User user);

    @PUT("/api/user/")
    public Call<User> update(@Body User user);
}
