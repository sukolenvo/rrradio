package com.dakare.radiorecord.web.config;

import com.dakare.radiorecord.web.repository.TrackInfoRepository;
import com.dakare.radiorecord.web.service.metadata.MetadataInfoLoadService;
import com.dakare.radiorecord.web.service.metadata.MetadataLoader;
import com.dakare.radiorecord.web.service.tops.StationTopsLoader;
import com.dakare.radiorecord.web.service.tops.StationTopsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Profile("!test")
public class LiveConfiguration {

    @Bean
    public MetadataInfoLoadService metadataInfoLoadService(TrackInfoRepository trackInfoRepository,
                                                           MetadataLoader metadataLoader) {
        return new MetadataInfoLoadService(trackInfoRepository, metadataLoader);
    }

    @Bean
    public StationTopsService stationTopsService(TrackInfoRepository trackInfoRepository,
                                                 StationTopsLoader stationTopsLoader) {
        return new StationTopsService(stationTopsLoader, trackInfoRepository);
    }

}
