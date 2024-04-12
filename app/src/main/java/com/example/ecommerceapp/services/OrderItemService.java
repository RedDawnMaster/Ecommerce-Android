package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.OrderItemController;
import com.example.ecommerceapp.models.OrderItem;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;
import java.util.List;

public class OrderItemService {
    private static OrderItemService orderItemService;

    private OrderItemController orderItemController;
    private List<OrderItem> orderItems;

    private OrderItemService() {
        RetrofitService retrofitService = new RetrofitService();
        orderItemController = retrofitService.getRetrofit().create(OrderItemController.class);
    }

    public static OrderItemService getInstance() {
        if (orderItemService == null) orderItemService = new OrderItemService();
        return orderItemService;
    }

    public void refund(String label, String reference) {
        try {
            orderItemController.refund(label, reference).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
