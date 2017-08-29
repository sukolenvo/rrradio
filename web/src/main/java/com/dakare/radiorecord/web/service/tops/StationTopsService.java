package com.dakare.radiorecord.web.service.tops;

import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.repository.TrackInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class StationTopsService {

    /**
     * 1 day.
     */
    private static final long REFRESH_DELAY = 86_400_000;

    private StationTopsLoader stationTopsLoader;
    private TrackInfoRepository trackInfoRepository;

    @Value("${record.tops-stations:}")
    private List<String> tops;

    public StationTopsService(StationTopsLoader stationTopsLoader, TrackInfoRepository trackInfoRepository) {
        this.stationTopsLoader = stationTopsLoader;
        this.trackInfoRepository = trackInfoRepository;
    }

    @Scheduled(fixedRate = REFRESH_DELAY)
    public void loadData() {
        for (String station : tops) {
            try {
                List<TrackInfo> load = stationTopsLoader.load(station);
                if (load == null) {
                    throw new IllegalStateException("Empty response for: " + station);
                }
                trackInfoRepository.save(load.stream()
                        .filter(TrackInfo::checkValid)
                        .collect(Collectors.toList()));
            } catch (Exception e) {
                log.error("Failed to load data for: " + station, e);
            }
        }
    }
}
