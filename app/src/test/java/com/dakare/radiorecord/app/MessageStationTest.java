package com.dakare.radiorecord.app;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class MessageStationTest {

    @Test
    public void testRequest() throws Exception {
        String url = "http://www.radiorecord.ru/radioapi/feedback/?prefix=rr&message=%D0%A0%D0%B5%D0%BA%D0%BE%D1%80%D0%B4%20%D0%BB%D1%83%D1%87%D1%88%D0%B8%D0%B9.%20%D0%9B%D1%8E%D0%B1%D0%B8%D0%BC%D0%B0%D1%8F%20%D1%80%D0%B0%D0%B4%D0%B8%D0%BE%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D1%8F&phone=%2B380991204485";
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("188.32.11.90", 8081));
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(proxy);
        connection.setRequestMethod("POST");
        int responseCode = connection.getResponseCode();
        Assert.assertTrue(responseCode == 200);
        Assert.assertEquals("{\"result\":{\"success\":true}}", new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());
    }
}
