package com.example.ecommerceapp.models;

import java.util.Date;
import java.util.List;

public class Order {
    private Long id;
    private String reference;
    private double total;
    private double deletedProductsTotal;
    private Date orderDate;
    private Date deliveryDate;
    private boolean delivered;
    private User user;
    private List<OrderItem> orderItems;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDeletedProductsTotal() {
        return deletedProductsTotal;
    }

    public void setDeletedProductsTotal(double deletedProductsTotal) {
        this.deletedProductsTotal = deletedProductsTotal;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
