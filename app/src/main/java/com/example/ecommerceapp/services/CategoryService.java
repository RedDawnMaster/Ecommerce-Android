package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.CategoryController;
import com.example.ecommerceapp.models.Category;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;
import java.util.List;

public class CategoryService {
    private static CategoryService categoryService;

    private CategoryController categoryController;

    private List<Category> categories;

    private CategoryService() {
        RetrofitService retrofitService = new RetrofitService();
        categoryController = retrofitService.getRetrofit().create(CategoryController.class);
    }

    public static CategoryService getInstance() {
        if (categoryService == null) categoryService = new CategoryService();
        return categoryService;
    }

    public List<Category> findAll() {
        try {
            categories = categoryController.findAll().execute().body();
            return categories;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Category save(Category category) {
        try {
            return categoryController.save(category).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Category> getCategories() {
        return categories;
    }

}
