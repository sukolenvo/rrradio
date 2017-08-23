package com.dakare.radiorecord.web.service;

import com.dakare.radiorecord.web.domain.TrackInfo;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.reader.MetricReader;

@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final String ALBUM_REQUEST_SUCCESS = "counter.request.album.success";
    private static final String ALBUM_REQUEST_FAIL = "counter.request.album.fail";

    private CounterService counterService;
    private MetricReader metricRepository;

    @Override
    public void logAlbumResponse(TrackInfo trackInfo) {
        if (trackInfo == null) {
            counterService.increment(ALBUM_REQUEST_FAIL);
        } else {
            counterService.increment(ALBUM_REQUEST_SUCCESS);
        }
    }

    @Override
    public long getAlbumSuccess() {
        Metric<?> one = metricRepository.findOne(ALBUM_REQUEST_SUCCESS);
        return one == null ? 0 : one.getValue().longValue();
    }

    @Override
    public long getAlbumFailed() {
        Metric<?> one = metricRepository.findOne(ALBUM_REQUEST_FAIL);
        return one == null ? 0 : one.getValue().longValue();
    }
}
