package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.ReviewController;
import com.example.ecommerceapp.retrofit.RetrofitService;

public class ReviewService {
    private static ReviewService reviewService;

    private ReviewController reviewController;

    private ReviewService() {
        RetrofitService retrofitService = new RetrofitService();
        reviewController = retrofitService.getRetrofit().create(ReviewController.class);
    }

    public static ReviewService getInstance() {
        if (reviewService == null) reviewService = new ReviewService();
        return reviewService;
    }

    public ReviewController getReviewController() {
        return reviewController;
    }
}
