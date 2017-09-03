package com.dakare.radiorecord.web.service;

import com.dakare.radiorecord.web.controller.event.FailedTrackRequestEvent;
import com.dakare.radiorecord.web.controller.event.SuccessTrackRequestEvent;
import com.dakare.radiorecord.web.domain.TrackInfo;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.springframework.context.event.EventListener;

@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private static final String ALBUM_REQUEST_SUCCESS = "counter.request.album.success";
    private static final String ALBUM_REQUEST_FAIL = "counter.request.album.fail";

    private CounterService counterService;
    private MetricReader metricRepository;

    @EventListener(SuccessTrackRequestEvent.class)
    public void handleSuccessAlbumRequest() {
            counterService.increment(ALBUM_REQUEST_SUCCESS);
    }

    @EventListener(classes = FailedTrackRequestEvent.class)
    public void handleFailedAlbumRequest() {
        counterService.increment(ALBUM_REQUEST_FAIL);
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
