package com.dakare.radiorecord.web.config;

import com.dakare.radiorecord.web.service.StatisticsService;
import com.dakare.radiorecord.web.service.StatisticsServiceImpl;
import com.dakare.radiorecord.web.service.history.StationHistoryLoader;
import com.dakare.radiorecord.web.service.history.StationHistoryLoaderImpl;
import com.dakare.radiorecord.web.service.metadata.MetadataLoader;
import com.dakare.radiorecord.web.service.metadata.MetadataLoaderImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableRetry
public class CoreConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public StationHistoryLoader stationHistoryLoader() {
        return new StationHistoryLoaderImpl(restTemplate(), objectMapper());
    }

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Bean
    public MetadataLoader metadataLoader() {
        return new MetadataLoaderImpl(restTemplate(), objectMapper());
    }

    @Bean
    public StatisticsService statisticsService(CounterService counterService, MetricReader metricReader) {
        return new StatisticsServiceImpl(counterService, metricReader);
    }
}
