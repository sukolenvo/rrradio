package com.dakare.radiorecord.app.player.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.player.UpdateResponse;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MetadataLoader extends BroadcastReceiver implements Runnable {
    private final String URL_FORMAT = "https://www.radiorecord.ru/xml/%s_online_v8.txt";
    private final String OFFLINE_URL_FORMAT = "http://13.59.180.140:3001/api/albumInfo?artist=%s&song=%s&station=%s";
    private final MetadataChangeCallback callback;
    private final Context context;
    private final Object lock = new Object();
    private final Thread thread;
    @Getter
    private PlaylistItem playlistItem;
    @Getter
    private UpdateResponse response = new UpdateResponse();
    private volatile boolean playing;
    private volatile boolean destroyed;
    private volatile boolean interactive;

    public MetadataLoader(final MetadataChangeCallback callback, final Context context) {
        this.callback = callback;
        this.context = context;
        destroyed = false;
        interactive = true;
        thread = new Thread(this);
        thread.setName("Metadata loader");
        thread.setDaemon(true);
        thread.start();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(this, intentFilter);
    }

    public void start(final PlaylistItem playlistItem) {
        this.playlistItem = playlistItem;
        playing = true;
        response = new UpdateResponse();
        thread.interrupt();
    }

    public void stop() {
        playing = false;
        response = new UpdateResponse();
        thread.interrupt();
    }

    @Override
    public void run() {
        if (PreferenceManager.getInstance(context).isMusicMetadataEnabled()) {
            while (!destroyed) {
                while (playlistItem != null && playing && interactive && doLoad()) ;
                try {
                    synchronized (lock) {
                        if (playing && interactive && playlistItem.isLive()) {
                            lock.wait(5000);
                        } else {
                            lock.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    //Nothing to do
                }
            }
        }
    }

    private boolean doLoad() {
        if (playlistItem.isLive()) {
            return loadLive();
        } else {
            return loadOffline();
        }
    }

    private boolean loadOffline() {
        if (response.getImage600() == null
                && !TextUtils.isEmpty(playlistItem.getTitle())
                && !TextUtils.isEmpty(playlistItem.getSubtitle())) {
            String url = String.format(OFFLINE_URL_FORMAT, URLEncoder.encode(playlistItem.getTitle()),
                    URLEncoder.encode(playlistItem.getSubtitle()),
                    URLEncoder.encode(playlistItem.getStation().getCodeAsParam()));
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(10_000);
                connection.setReadTimeout(10_000);
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    UpdateResponse response = new UpdateResponse();
                    response.setArtist(playlistItem.getTitle());
                    response.setTitle(playlistItem.getSubtitle());
                    response.setImage600(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine());
                    publishProgress(response);
                } else {
                    Log.i("MetadataLoader", "No album found");
                }
            } catch (IOException | IndexOutOfBoundsException e) {
                Log.w("MetadataLoader", "Failed to connect to metadata server", e);
            }
        }
        return false;
    }

    private boolean loadLive() {
        UpdateResponse updateResponse = new UpdateResponse();
        try {
            String url = String.format(URL_FORMAT, playlistItem.getStation().getCodeAsParam());
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    builder.append(line);
                }
                updateResponse.parse(builder.toString());
            } else {
                Log.w("Update Task", "Error code: " + responseCode);
            }
        } catch (Exception e) {
            Log.w("Update Task", "Failed to load update", e);
            if (Thread.currentThread().isInterrupted()) {
                return true;
            }
        }
        publishProgress(updateResponse);
        return false;
    }

    private void publishProgress(final UpdateResponse response) {
        if (playing && interactive) {
            if (!response.equals(this.response)) {
                this.response = response;
                callback.onMetadataChanged();
            }
        }
    }

    public void shutdown() {
        destroyed = true;
        thread.interrupt();
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
            Log.w("Metadata receiver", "Failed to unregister", e);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        interactive = Intent.ACTION_SCREEN_ON.equals(intent.getAction())
                || PreferenceManager.getInstance(context).isBackgroundLoad();
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public interface MetadataChangeCallback {
        void onMetadataChanged();
    }
}
