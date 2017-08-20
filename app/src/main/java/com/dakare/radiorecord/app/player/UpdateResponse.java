package com.dakare.radiorecord.app.player;

import android.text.TextUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

@Data
@NoArgsConstructor
public class UpdateResponse {

    private String image600;
    private String title;
    private String artist;
    private String itunesURL;

    public void parse(String json) throws JSONException {
        if (!TextUtils.isEmpty(json)) {
            JSONObject object = new JSONObject(json);
            if (object.has("image600")) {
                image600 = object.getString("image600");
            }
            if (object.has("title")) {
                title = object.getString("title");
            }
            if (object.has("artist")) {
                artist = object.getString("artist");
            }
            if (object.has("itunesURL")) {
                itunesURL = object.getString("itunesURL");
            }
        }
    }
}
