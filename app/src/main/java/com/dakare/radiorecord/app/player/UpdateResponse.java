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

    public UpdateResponse(String json) throws JSONException {
        if (!TextUtils.isEmpty(json)) {
            JSONObject object = new JSONObject(json);
            image600 = object.getString("image600");
            title = object.getString("title");
            artist = object.getString("artist");
            itunesURL = object.getString("itunesURL");
        }
    }
}
