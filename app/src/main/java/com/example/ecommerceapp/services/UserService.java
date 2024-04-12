package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.UserController;
import com.example.ecommerceapp.models.User;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;

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

    public User login(String username, String password) {
        try {
            user = userController.login(username, password).execute().body();
            return user;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User register(User user) {
        try {
            this.user = userController.register(user).execute().body();
            return this.user;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User update() {
        try {
            User user;
            user = userController.update(this.user).execute().body();
            if (user != null) this.user = user;
            return user;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
