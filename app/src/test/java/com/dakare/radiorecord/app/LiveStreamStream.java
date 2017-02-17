package com.dakare.radiorecord.app;

import com.dakare.radiorecord.app.load.AbstractLoadFragment;
import com.dakare.radiorecord.app.quality.Quality;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class LiveStreamStream {

    @Test
    public void testAllStations() throws IOException {
        for (Station station : Station.values()) {
            for (Quality quality : Quality.values()) {
                String streamUrl = station.getStreamUrl(quality);
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(streamUrl).openConnection();
                urlConnection.setRequestProperty("User-Agent", AbstractLoadFragment.USER_AGENT);
                Assert.assertEquals(streamUrl,200, urlConnection.getResponseCode());
            }
        }
    }
}
