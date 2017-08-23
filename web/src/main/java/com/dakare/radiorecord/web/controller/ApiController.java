package com.dakare.radiorecord.web.controller;

import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import com.dakare.radiorecord.web.repository.TrackInfoRepository;
import com.dakare.radiorecord.web.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TrackInfoRepository trackInfoRepository;

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("hello")
    public String helloWorld() {
        return "hello!";
    }

    @GetMapping("albumInfo")
    public String getAlbum(@RequestParam String artist,
                           @RequestParam String song,
                           @RequestParam String station) {
        TrackInfo result = trackInfoRepository.findOne(new TrackInfoPK(artist, song, station));
        statisticsService.logAlbumResponse(result);
        if (result == null) {
            throw new NotFoundException();
        }
        return result.getImage600();
    }
}
