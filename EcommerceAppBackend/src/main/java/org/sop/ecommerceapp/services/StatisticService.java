package org.sop.ecommerceapp.services;

import java.util.List;

import org.sop.ecommerceapp.models.Statistic;
import org.sop.ecommerceapp.repositories.StatisticDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;


@Service
public class StatisticService {
    @Autowired
    private StatisticDao statisticDao;

    public Statistic findById(Long id) {
        return statisticDao.findById(id).orElse(null);
    }

    public Statistic save(Statistic statistic) {
        return statisticDao.save(statistic);
    }
}
