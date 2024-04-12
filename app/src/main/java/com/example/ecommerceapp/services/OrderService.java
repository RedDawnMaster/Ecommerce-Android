package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.OrderController;
import com.example.ecommerceapp.models.Order;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;
import java.util.List;

public class OrderService {
    private static OrderService orderService;

    private OrderController orderController;
    private List<Order> orders;

    private OrderService() {
        RetrofitService retrofitService = new RetrofitService();
        orderController = retrofitService.getRetrofit().create(OrderController.class);
    }

    public static OrderService getInstance() {
        if (orderService == null) orderService = new OrderService();
        return orderService;
    }

    public List<Order> findByUserUsername(String username) {
        try {
            orders = orderController.findByUserUsername(username).execute().body();
            return orders;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkRefundable(Long id) {
        try {
            return Boolean.TRUE.equals(orderController.checkRefundable(id).execute().body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Order> getOrders() {
        return orders;
    }
}
