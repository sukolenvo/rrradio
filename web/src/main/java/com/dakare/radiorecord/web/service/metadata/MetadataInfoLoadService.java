package com.dakare.radiorecord.web.service.metadata;

import com.dakare.radiorecord.web.domain.TrackInfo;
import com.dakare.radiorecord.web.repository.TrackInfoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class MetadataInfoLoadService {

    private TrackInfoRepository trackInfoRepository;
    private MetadataLoader metadataLoader;

    @Scheduled(fixedRate = 90_000)
    public void loadMetadata() {
        try {
            List<TrackInfo> result = metadataLoader.load();
            trackInfoRepository.save(result.stream()
                    .filter(TrackInfo::checkValid)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.warn("Failed to load metadata", e);
        }
    }
}
