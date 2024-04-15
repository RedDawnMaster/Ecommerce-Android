package com.example.ecommerceapp.models;

public class Statistic {
    private Long id;
    private int totalProducts;
    private int totalSoldProducts;
    private double totalSales;
    private int totalClients;

    private int refundPeriod = 5;
    private int deliveryTime = 2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public int getTotalSoldProducts() {
        return totalSoldProducts;
    }

    public void setTotalSoldProducts(int totalSoldProducts) {
        this.totalSoldProducts = totalSoldProducts;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(int totalClients) {
        this.totalClients = totalClients;
    }

    public int getRefundPeriod() {
        return refundPeriod;
    }

    public void setRefundPeriod(int refundPeriod) {
        this.refundPeriod = refundPeriod;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
