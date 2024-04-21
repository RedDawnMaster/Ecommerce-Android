package org.sop.ecommerceapp.controllers;

import org.sop.ecommerceapp.models.Statistic;
import org.sop.ecommerceapp.services.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/statistic")
public class StatisticController {
    @Autowired
    private StatisticService statisticService;

    @GetMapping("/")
    public Statistic findById() {
        return statisticService.findById(1L);
    }

    @PostMapping("/")
    public Statistic save(@RequestBody Statistic statistic){
        return  statisticService.save(statistic);
    }
}
