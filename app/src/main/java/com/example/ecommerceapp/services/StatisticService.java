package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.StatisticController;
import com.example.ecommerceapp.retrofit.RetrofitService;

public class StatisticService {
    private static StatisticService statisticService;

    private StatisticController statisticController;

    private StatisticService() {
        RetrofitService retrofitService = new RetrofitService();
        statisticController = retrofitService.getRetrofit().create(StatisticController.class);
    }

    public static StatisticService getInstance() {
        if (statisticService == null) statisticService = new StatisticService();
        return statisticService;
    }

    public StatisticController getStatisticController() {
        return statisticController;
    }
}
