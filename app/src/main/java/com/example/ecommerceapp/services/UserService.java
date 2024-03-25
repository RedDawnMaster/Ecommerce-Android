package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.UserController;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.retrofit.RetrofitService;

public class UserService {
    private static UserService userService;

    private UserController userController;

    private User user;

    private UserService() {
        RetrofitService retrofitService = new RetrofitService();
        userController = retrofitService.getRetrofit().create(UserController.class);
    }

    public static UserService getInstance() {
        if (userService == null) userService = new UserService();
        return userService;
    }

    public User getUser() {
        return user;
    }
}
