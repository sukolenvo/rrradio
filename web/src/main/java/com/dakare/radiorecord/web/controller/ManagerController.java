package com.dakare.radiorecord.web.controller;

import com.dakare.radiorecord.web.domain.EmptyTrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.domain.TrackInfoPK;
import com.dakare.radiorecord.web.repository.EmptyTrackInfoRepository;
import com.dakare.radiorecord.web.repository.TrackInfoRepository;
import com.dakare.radiorecord.web.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private EmptyTrackInfoRepository emptyTrackInfoRepository;

    @Autowired
    private TrackInfoRepository trackInfoRepository;

    @GetMapping("album")
    @ResponseBody
    public String albumStats() {
        return "success: " + statisticsService.getAlbumSuccess() + ", failed: " + statisticsService.getAlbumFailed();
    }

    @GetMapping("emptyTracks")
    public String emptyTracksPage(Model model, @RequestParam(defaultValue = "1") int page) {
        Page<EmptyTrackInfo> tracks = emptyTrackInfoRepository.findAll(new PageRequest(page - 1, 20, new Sort(Sort.Direction.DESC, "count")));
        model.addAttribute("page", tracks);
        return "emptyTracks";
    }

    @PostMapping("emptyTracks")
    public String updateEmptyTrack(@RequestParam String artist,
                                   @RequestParam String station,
                                   @RequestParam String song,
                                   @RequestParam String image) {
        TrackInfo trackInfo = new TrackInfo();
        trackInfo.setImage600(image);
        trackInfo.setSong(song);
        trackInfo.setArtist(artist);
        trackInfo.setPrefix(station);
        trackInfoRepository.save(trackInfo);
        emptyTrackInfoRepository.delete(new TrackInfoPK(artist, song, station));
        return "redirect:/manager/emptyTracks";
    }

    @GetMapping("trackSearch")
    public String trackInfoPage(Model model,
                                @RequestParam(required = false) String station,
                                @RequestParam(required = false) String song) {
        if (station == null && song == null) {
            model.addAttribute("tracks", Collections.emptyList());
            model.addAttribute("station", "");
            model.addAttribute("song", "");
        } else {
            Page<TrackInfo> tracks = trackInfoRepository.findBySongContainsAndPrefix(song, station, new PageRequest(0, 20));
            model.addAttribute("tracks", tracks.getContent());
            model.addAttribute("station", station);
            model.addAttribute("song", song);
        }
        return "trackSearch";
    }

}
