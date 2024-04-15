package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.ReviewController;
import com.example.ecommerceapp.models.Review;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;
import java.util.List;

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

    public List<Review> findByProductLabel(String label) {
        try {
            return reviewController.findByProductLabel(label).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Review save(Review review) {
        try {
            return reviewController.save(review).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteByUserUsernameAndProductLabel(String username, String label) {
        try {
            reviewController.deleteByUserUsernameAndProductLabel(username, label).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
