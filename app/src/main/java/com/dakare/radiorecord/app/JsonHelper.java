package com.dakare.radiorecord.app;

import android.text.TextUtils;
import android.util.JsonWriter;
import android.util.Log;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class JsonHelper {

    public static String writePlaylist(List<PlaylistItem> playlist) {
        if (playlist == null || playlist.isEmpty()) {
            return "[]";
        }
        try {
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.beginArray();
            for (PlaylistItem item : playlist) {
                jsonWriter.beginObject();
                jsonWriter.name("title").value(item.getTitle());
                if (!TextUtils.isEmpty(item.getSubtitle())) {
                    jsonWriter.name("subtitle").value(item.getSubtitle());
                }
                jsonWriter.name("url").value(item.getUrl());
                jsonWriter.name("station").value(item.getStation().getName());
                jsonWriter.name("live").value(item.isLive());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            return writer.toString();
        } catch (IOException e) {
            Log.w("JsonHelper", "Failed to serialize playlist", e);
            return "[]";
        }
    }

    public static List<PlaylistItem> readPlaylist(String value) throws JSONException {
        if (TextUtils.isEmpty(value) || "[]".equals(value)) {
            throw new NoSuchElementException();
        }
        JSONArray array = new JSONArray(value);
        List<PlaylistItem> result = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            PlaylistItem item = new PlaylistItem();
            item.setTitle(jsonObject.getString("title"));
            item.setStation(Station.valueOf(jsonObject.getString("station")));
            if (jsonObject.has("subtitle")) {
                item.setSubtitle(jsonObject.getString("subtitle"));
            }
            item.setUrl(jsonObject.getString("url"));
            item.setLive(jsonObject.getBoolean("live"));
            result.add(item);
        }
        return result;
    }
}
