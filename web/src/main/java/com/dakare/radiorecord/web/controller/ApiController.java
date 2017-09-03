package com.dakare.radiorecord.web.controller;

import com.dakare.radiorecord.web.controller.event.FailedTrackRequestEvent;
import com.dakare.radiorecord.web.controller.event.SuccessTrackRequestEvent;
import com.dakare.radiorecord.web.domain.Purchase;
import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import com.dakare.radiorecord.web.repository.PurchaseRepository;
import com.dakare.radiorecord.web.repository.TrackInfoRepository;
import com.dakare.radiorecord.web.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TrackInfoRepository trackInfoRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("albumInfo")
    public String getAlbum(@RequestParam String artist,
                           @RequestParam String song,
                           @RequestParam String station) {
        TrackInfoPK trackInfoPK = new TrackInfoPK(artist, song, station);
        TrackInfo result = trackInfoRepository.findOne(trackInfoPK);
        if (result == null) {
            FailedTrackRequestEvent failedTrackRequestEvent = new FailedTrackRequestEvent();
            failedTrackRequestEvent.setArtist(trackInfoPK.getArtist());
            failedTrackRequestEvent.setSong(trackInfoPK.getSong());
            failedTrackRequestEvent.setStation(trackInfoPK.getPrefix());
            applicationEventPublisher.publishEvent(failedTrackRequestEvent);
            throw new NotFoundException();
        }
        SuccessTrackRequestEvent successTrackRequestEvent = new SuccessTrackRequestEvent();
        successTrackRequestEvent.setArtist(trackInfoPK.getArtist());
        successTrackRequestEvent.setSong(trackInfoPK.getSong());
        successTrackRequestEvent.setStation(trackInfoPK.getPrefix());
        applicationEventPublisher.publishEvent(successTrackRequestEvent);
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
