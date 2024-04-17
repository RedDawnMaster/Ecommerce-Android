package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.ProductController;
import com.example.ecommerceapp.models.Product;
import com.example.ecommerceapp.models.Response;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductService {
    private static ProductService productService;

    private ProductController productController;

    private List<Product> products;
    private List<Product> bestSellerProducts;
    private List<Product> mostReviewedProducts;
    private List<Product> boughProducts;
    private List<Integer> quantities;

    private Map<String, File> localFiles;

    private ProductService() {
        RetrofitService retrofitService = new RetrofitService();
        productController = retrofitService.getRetrofit().create(ProductController.class);
        localFiles = new HashMap<>();
    }

    public static ProductService getInstance() {
        if (productService == null) productService = new ProductService();
        return productService;
    }

    public void calculateProducts() {
        bestSellerProducts = new ArrayList<>(products);
        mostReviewedProducts = new ArrayList<>(products);
        bestSellerProducts.sort((p1, p2) -> Integer.compare(p2.getNumberOfOrders(), p1.getNumberOfOrders()));
        bestSellerProducts = bestSellerProducts.subList(0, Math.min(bestSellerProducts.size(), 4));
        mostReviewedProducts.sort((p1, p2) -> Integer.compare(p2.getNumberOfOrders(), p1.getNumberOfOrders()));
        mostReviewedProducts = mostReviewedProducts.subList(0, Math.min(mostReviewedProducts.size(), 2));
    }

    public List<Product> findAll() {
        try {
            products = productController.findAll().execute().body().stream().filter(product -> !product.isDeleted()).collect(Collectors.toList());
            calculateProducts();
            return products;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Product update(Product product) {
        try {
            return productController.update(product).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Product findById(Long id) {
        try {
            return productController.findById(id).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Product save(Product product) {
        try {
            return productController.save(product).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Response deleteByLabel(String label) {
        try {
            return productController.deleteByLabel(label).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Product> getBestSellerProducts() {
        return bestSellerProducts;
    }

    public List<Product> getMostReviewedProducts() {
        return mostReviewedProducts;
    }

    public List<Product> getBoughProducts() {
        if (boughProducts == null) boughProducts = new ArrayList<>();
        return boughProducts;
    }

    public void setBoughProducts(List<Product> boughProducts) {
        this.boughProducts = boughProducts;
    }

    public List<Integer> getQuantities() {
        if (quantities == null) quantities = new ArrayList<>();
        return quantities;
    }

    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

    public Map<String, File> getLocalFiles() {
        return localFiles;
    }
}
