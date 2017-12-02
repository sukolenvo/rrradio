package com.dakare.radiorecord.app;

import com.dakare.radiorecord.app.quality.Quality;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.dakare.radiorecord.app.load.loader.BasicCategoryLoader.USER_AGENT;

public class LiveStreamTest {

    @Test
    public void testAllStations() throws IOException {
        for (Station station : Station.values()) {
            for (Quality quality : Quality.values()) {
                String streamUrl = station.getStreamUrl(quality);
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(streamUrl).openConnection();
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
                Assert.assertEquals(streamUrl, 200, urlConnection.getResponseCode());
            }
        }
    }
}
