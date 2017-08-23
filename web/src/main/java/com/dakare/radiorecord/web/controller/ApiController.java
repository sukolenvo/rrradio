package com.dakare.radiorecord.web.controller;

import com.dakare.radiorecord.web.domain.Purchase;
import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import com.dakare.radiorecord.web.repository.PurchaseRepository;
import com.dakare.radiorecord.web.repository.TrackInfoRepository;
import com.dakare.radiorecord.web.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TrackInfoRepository trackInfoRepository;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private PurchaseRepository purchaseRepository;

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

    @PostMapping("purchase")
    public void savePurchase(@RequestBody String body) {
        log.info("Purchase: {}", body);
        Purchase purchase = new Purchase();
        purchase.setBody(body);
        purchase.setPurchaseDate(new Date());
        purchaseRepository.save(purchase);
    }
}
