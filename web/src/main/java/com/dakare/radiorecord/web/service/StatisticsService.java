package com.dakare.radiorecord.web.service;

import com.dakare.radiorecord.web.domain.TrackInfo;

public interface StatisticsService {

    void logAlbumResponse(TrackInfo trackInfo);

    long getAlbumSuccess();

    long getAlbumFailed();
}
