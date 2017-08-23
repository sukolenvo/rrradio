package com.dakare.radiorecord.web.controller;

import com.dakare.radiorecord.web.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("album")
    public String albumStats() {
        return "success: " + statisticsService.getAlbumSuccess() + ", failed: " + statisticsService.getAlbumFailed();
    }
}
