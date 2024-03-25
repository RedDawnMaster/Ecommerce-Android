package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.OrderController;
import com.example.ecommerceapp.retrofit.RetrofitService;

public class OrderService {
    private static OrderService orderService;

    private OrderController orderController;

    private OrderService() {
        RetrofitService retrofitService = new RetrofitService();
        orderController = retrofitService.getRetrofit().create(OrderController.class);
    }

    public static OrderService getInstance() {
        if (orderService == null) orderService = new OrderService();
        return orderService;
    }

    public OrderController getOrderController() {
        return orderController;
    }
}
