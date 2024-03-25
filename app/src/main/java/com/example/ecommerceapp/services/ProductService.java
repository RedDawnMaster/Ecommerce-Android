package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.ProductController;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductService {
    private static ProductService productService;

    private ProductController productController;

    private List<Product> products;

    private ProductService() {
        RetrofitService retrofitService = new RetrofitService();
        productController = retrofitService.getRetrofit().create(ProductController.class);
    }

    public static ProductService getInstance() {
        if (productService == null) productService = new ProductService();
        return productService;
    }

    public void findByCategoryLabel(String label) {
        productController.findByCategoryLabel(label).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                products = response.body();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    public List<Product> findAll() {
        try {
            products = productController.findAll().execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getProducts();
    }

    public List<Product> getProducts() {
        if (products == null) return new ArrayList<>();
        return products;
    }
}
