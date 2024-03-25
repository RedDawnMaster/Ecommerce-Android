package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.OrderItemController;
import com.example.ecommerceapp.retrofit.RetrofitService;

public class OrderItemService {
    private static OrderItemService orderItemService;

    private OrderItemController orderItemController;

    private OrderItemService() {
        RetrofitService retrofitService = new RetrofitService();
        orderItemController = retrofitService.getRetrofit().create(OrderItemController.class);
    }

    public static OrderItemService getInstance() {
        if (orderItemService == null) orderItemService = new OrderItemService();
        return orderItemService;
    }

    public OrderItemController getOrderItemController() {
        return orderItemController;
    }
}
