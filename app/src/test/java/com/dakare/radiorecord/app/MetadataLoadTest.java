package com.dakare.radiorecord.app;

import com.dakare.radiorecord.app.player.UpdateResponse;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class MetadataLoadTest {

    private String body = "//{\"artist\":\"DAVID GUETTA JUSTIN BIEBER\",\"title\":\"2U\",\"date_start\":1500918995," +
            "\"date_end\":1500919111,\"image100\":\"http:\\/\\/e-cdn-images.deezer.com\\/images\\/cover\\/a392a6c833438" +
            "989f1c2803da7f50f1b\\/100x100-000000-80-0-0.jpg\",\"image600\":\"http:\\/\\/e-cdn-images.deezer.com\\/ima" +
            "ges\\/cover\\/a392a6c833438989f1c2803da7f50f1b\\/600x600-000000-80-0-0.jpg\",\"itunesURL\":\"http:\\/\\/ww" +
            "w.deezer.com\\/track\\/369397961\",\"listenURL\":\"http:\\/\\/e-cdn-preview-6.deezer.com\\/stream\\/67f" +
            "8d3870391a2f51efd31edf4c0c23e-3.mp3\",\"trackPrice\":\"\"}";

    @Test
    public void testMetadataParsing() throws JSONException {
        UpdateResponse updateResponse = new UpdateResponse();
        updateResponse.parse(body);
        Assert.assertEquals("DAVID GUETTA JUSTIN BIEBER", updateResponse.getArtist());
        Assert.assertEquals("http:\\/\\/e-cdn-images.deezer.com\\/images\\/cover\\/a392a6c833438989f1c2803da7f50f1b\\/600x600-000000-80-0-0.jpg", updateResponse.getImage600());
        Assert.assertEquals("http:\\/\\/www.deezer.com\\/track\\/369397961", updateResponse.getItunesURL());
        Assert.assertEquals("2U", updateResponse.getTitle());
    }

    @Test
    public void testMetadataLoad() throws Exception {
        String url = "https://www.radiorecord.ru/xml/rr_online_v8.txt";
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("222.124.219.202", 8080));
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(proxy);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:52.0) Gecko/20100101 Firefox/52.0");
        int responseCode = connection.getResponseCode();
        if (connection.getHeaderField("set-cookie") != null) {
            String cookie = connection.getHeaderField("set-cookie").split(";")[0];
            connection = (HttpURLConnection) new URL(url).openConnection(proxy);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:52.0) Gecko/20100101 Firefox/52.0");
            connection.setRequestProperty("Cookie", cookie);
            responseCode = connection.getResponseCode();
        }
        Assert.assertEquals(200, responseCode);
    }
}
