package com.dakare.radiorecord.web.service.metadata;

import com.dakare.radiorecord.web.domain.TrackInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public class MetadataLoaderImpl implements MetadataLoader {

    private static final String URL = "http://www.radiorecord.ru/radioapi/stations/now/";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Override
    @Retryable(backoff = @Backoff(value = 3000L))
    public List<TrackInfo> load() throws IOException {
        String response = restTemplate.getForObject(URL, String.class);
        return objectMapper.readValue(response, Response.class).getResult();
    }

    @Data
    private static class Response {
        private List<TrackInfo> result;
    }

}
