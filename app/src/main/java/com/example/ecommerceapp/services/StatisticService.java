package com.example.ecommerceapp.services;

import com.example.ecommerceapp.controllers.StatisticController;
import com.example.ecommerceapp.models.Statistic;
import com.example.ecommerceapp.retrofit.RetrofitService;

import java.io.IOException;

public class StatisticService {
    private static StatisticService statisticService;

    private StatisticController statisticController;
    private Statistic statistic;

    private StatisticService() {
        RetrofitService retrofitService = new RetrofitService();
        statisticController = retrofitService.getRetrofit().create(StatisticController.class);
    }

    public static StatisticService getInstance() {
        if (statisticService == null) statisticService = new StatisticService();
        return statisticService;
    }

    public Statistic findById() {
        try {
            return statistic = statisticController.findById().execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            statistic = statisticController.save(statistic).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Statistic getStatistic() {
        return statistic;
    }
}
