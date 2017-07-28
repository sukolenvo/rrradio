package com.dakare.radiorecord.app.message_studio;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessageSender extends AsyncTask<String, Void, Boolean> {


    public void sendMessage(String message, String phone) {
        execute(message, phone);
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String url = new Uri.Builder()
                .scheme("http")
                .authority("www.radiorecord.ru")
                .appendPath("radioapi")
                .appendPath("feedback")
                .appendPath("")
                .appendQueryParameter("prefix", "rr")
                .appendQueryParameter("message", strings[0])
                .appendQueryParameter("phone", strings[1])
                .build().toString();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    builder.append(line);
                }
                JSONObject object = new JSONObject(builder.toString());
                return object.getJSONObject("result").getBoolean("success");
            }
        } catch (IOException | JSONException e) {
            Log.w("MessageSender", "Failed to send message to studio", e);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(RecordApplication.getInstance(), R.string.message_studio_succes, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(RecordApplication.getInstance(), R.string.message_studio_error, Toast.LENGTH_LONG).show();
        }
    }
}
