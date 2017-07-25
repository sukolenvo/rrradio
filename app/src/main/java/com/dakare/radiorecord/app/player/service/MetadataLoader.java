package com.dakare.radiorecord.app.player.service;

import android.content.Context;
import android.util.Log;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.UpdateResponse;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class MetadataLoader implements Runnable {
    private final String URL_FORMAT = "https://www.radiorecord.ru/xml/%s_online_v8.txt";

    private final AtomicBoolean playing = new AtomicBoolean();
    private Station station;
    @Getter
    private UpdateResponse response = new UpdateResponse();
    private final MetadataChangeCallback callback;
    private final Context context;
    private Thread thread;

    public MetadataLoader(final MetadataChangeCallback callback, final Context context) {
        //TODO: fix image blink
        this.callback = callback;
        this.context = context;
    }

    public void start(final Station station) {
        response = new UpdateResponse();
        this.station = station;
        if (playing.compareAndSet(false, true)) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        playing.set(false);
        response = new UpdateResponse();
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        while (playing.get() && thread == Thread.currentThread()) {
            if (PreferenceManager.getInstance(context).isMusicMetadataEnabled()) {
                UpdateResponse updateResponse = null;
                try {
                    String url = String.format(URL_FORMAT, station.getCodeAsParam());
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setConnectTimeout(10_000);
                    connection.setReadTimeout(180_000); //Their cloud protection is annoying as fuck
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder builder = new StringBuilder();
                        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                            builder.append(line);
                        }
                        updateResponse = new UpdateResponse(builder.toString());
                    } else {
                        Log.w("Update Task", "Error code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e("Update Task", "Failed to load update", e);
                }
                if (updateResponse == null) {
                    updateResponse = new UpdateResponse();
                }
                publishProgress(updateResponse);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.w("Update task", "Exception in update task", e);
            }
        }
    }

    private void publishProgress(final UpdateResponse response) {
        if (playing.get()) {
            if (!response.equals(this.response)) {
                this.response = response;
                callback.onMetadataChanged();
            }
        }
    }


    public interface MetadataChangeCallback {
        void onMetadataChanged();
    }
}
