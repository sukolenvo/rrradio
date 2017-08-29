package com.dakare.radiorecord.web.service.tops;

import com.dakare.radiorecord.web.domain.TrackInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class StationTopsLoaderImpl implements StationTopsLoader {

    private static final String BASE_URL = "http://www.radiorecord.ru/radioapi/station/?prefix=";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Override
    @Retryable(backoff = @Backoff(value = 3000L))
    public List<TrackInfo> load(String prefix) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(BASE_URL + prefix, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readValue(responseEntity.getBody(), Response.class).getResult().getTop()
                        .stream().map(item -> {
                            TrackInfo trackInfo = new TrackInfo();
                            trackInfo.setPrefix(prefix);
                            trackInfo.setArtist(item.getTitle());
                            trackInfo.setSong(item.getSong());
                            trackInfo.setImage600(item.getImage600());
                            return trackInfo;
                        }).collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Unexpected response code: " + responseEntity.getStatusCode());
    }

    @Data
    private static class Response {
        private Result result;
    }

    @Data
    private static class Result {
        private List<Map> history;
        private List<TopTrack> top;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TopTrack {
        private String title;
        private String song;
        private String image600;
    }
}
