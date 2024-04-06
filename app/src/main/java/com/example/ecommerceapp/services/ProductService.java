package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.ProductController;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductService {
    private static ProductService productService;

    private ProductController productController;

    private List<Product> products;
    private List<Product> bestSellerProducts;
    private List<Product> mostReviewedProducts;

    private ProductService() {
        RetrofitService retrofitService = new RetrofitService();
        productController = retrofitService.getRetrofit().create(ProductController.class);
    }

    public static ProductService getInstance() {
        if (productService == null) productService = new ProductService();
        return productService;
    }

    public List<Product> findAll() {
        try {
            products = productController.findAll().execute().body();
            bestSellerProducts = new ArrayList<>(products);
            mostReviewedProducts = new ArrayList<>(products);
            Collections.sort(bestSellerProducts, (p1, p2) -> Integer.compare(p2.getNumberOfOrders(), p1.getNumberOfOrders()));
            bestSellerProducts = bestSellerProducts.subList(0, Math.min(bestSellerProducts.size(), 4));
            Collections.sort(mostReviewedProducts, (p1, p2) -> Integer.compare(p2.getNumberOfOrders(), p1.getNumberOfOrders()));
            mostReviewedProducts = mostReviewedProducts.subList(0, Math.min(mostReviewedProducts.size(), 2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public List<Product> getProducts() {
        return products;
    }
}
