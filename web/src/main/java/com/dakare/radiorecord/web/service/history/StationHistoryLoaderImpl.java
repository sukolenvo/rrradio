package com.dakare.radiorecord.web.service.history;

import com.dakare.radiorecord.web.domain.HistoryRecordItem;
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

@AllArgsConstructor
public class StationHistoryLoaderImpl implements StationHistoryLoader {

    private static final String BASE_URL = "http://www.radiorecord.ru/radioapi/station/?prefix=";

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @Override
    @Retryable(backoff = @Backoff(value = 3000L))
    public List<HistoryRecordItem> load(String prefix) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(BASE_URL + prefix, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readValue(responseEntity.getBody(), Response.class).getResult().getHistory();
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
        private List<HistoryRecordItem> history;
        private List<Map> top;
    }
}
