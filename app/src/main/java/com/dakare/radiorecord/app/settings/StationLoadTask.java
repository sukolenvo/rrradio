package com.dakare.radiorecord.app.settings;

import android.graphics.*;
import android.os.AsyncTask;
import android.util.Log;

import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.station.DynamicStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class StationLoadTask extends AsyncTask<Void, String, Void> {

    private static final String URL = "https://radiorecord.ru/api/stations";

    private final WeakReference<StationLoadListener> stationLoadListener;

    StationLoadTask(StationLoadListener stationLoadListener) {
        this.stationLoadListener = new WeakReference<>(stationLoadListener);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder builder = new StringBuilder();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    builder.append(line);
                }
                parseResponse(builder.toString());
            } else {
                Log.w("Update Task", "Error code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e("StationLoadTask", "cannot load new stations", e);
            publishProgress(RecordApplication.getInstance().getString(R.string.status_loading_stations_error));
        }
        return null;
    }


    private void parseResponse(String response) throws Exception {
        List<DynamicStation> items = new ArrayList<>();
        JSONObject object = new JSONObject(response);
        JSONArray resultArray = object.getJSONObject("result").getJSONArray("stations");
        for (int i = 0; i < resultArray.length(); i++) {
            items.add(parseStation(resultArray.getJSONObject(i)));
        }
        Set<DynamicStation> existingStations = new HashSet<>(
                PreferenceManager.getInstance(RecordApplication.getInstance()).getStations()
        );
        List<DynamicStation> currentStations = new ArrayList<>(
                PreferenceManager.getInstance(RecordApplication.getInstance()).getStations());
        List<DynamicStation> newStations = new ArrayList<>();
        for (DynamicStation item : items) {
            if (!existingStations.contains(item)) {
                newStations.add(item);
            }
        }
        if (newStations.isEmpty()) {
            publishProgress(RecordApplication.getInstance().getString(R.string.status_loading_stations_no_new));
        } else {
            for (int i = 0; i < newStations.size(); i++) {
                publishProgress(RecordApplication.getInstance().getString(R.string.status_loading_stations_progress,
                        i, newStations.size()));
                DynamicStation newStation = newStations.get(i);
                prepareIcons(newStation);
                currentStations.add(newStation);
            }
            PreferenceManager.getInstance(RecordApplication.getInstance()).setStations(currentStations);
            publishProgress(RecordApplication.getInstance().getString(R.string.status_loading_stations_summary,
                    newStations.size()));
        }
    }

    private void prepareIcons(DynamicStation newStation) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(newStation.getImageColor()).openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
        int responseCode = connection.getResponseCode();
        Bitmap bitmap = null;
        if (responseCode == 200) {
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());

        }
        if (bitmap == null || bitmap.getWidth() == 0) {
            bitmap = drawStub(newStation);
        }
        File file = new File(RecordApplication.getInstance().getFilesDir(), newStation.getKey() + ".png");
        FileOutputStream stream = new FileOutputStream(file);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } finally {
            stream.close();
        }
    }

    private Bitmap drawStub(DynamicStation newStation) {
        Bitmap bitmap = Bitmap.createBitmap(201, 140, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setARGB(255, 192, 192, 192);
        Rect output = new Rect();
        paint.getTextBounds(newStation.getShortTitle(), 0, newStation.getShortTitle().length(), output);
        if (output.right > 90 && newStation.getShortTitle().length() > 10) {
            String firstLine = newStation.getShortTitle().substring(0, 10);
            String secondLine = newStation.getShortTitle().substring(10);
            paint.getTextBounds(firstLine, 0, firstLine.length(), output);
            canvas.drawText(firstLine, Math.max(0, 100 - output.right / 2), 35, paint);
            paint.getTextBounds(secondLine, 0, secondLine.length(), output);
            canvas.drawText(secondLine, Math.max(0, 100 - output.right / 2), 75, paint);
        } else {
            canvas.drawText(newStation.getShortTitle(), Math.max(0, 100 - output.right / 2), 55, paint);
        }
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        StationLoadListener listener = this.stationLoadListener.get();
        listener.updateStatus(values[0]);
    }

    interface StationLoadListener {

        void updateStatus(String status);
    }

    private static DynamicStation parseStation(JSONObject item) throws JSONException {
        long id = item.getLong("id");
        String shortTitle = item.getString("short_title");
        String stream64 = item.getString("stream_64");
        String stream128 = item.getString("stream_128");
        String stream320 = item.getString("stream_320");
        String imageGray = item.getString("icon_gray");
        String imageColor = item.getString("icon_fill_colored");
        String imageWhite = item.getString("icon_fill_white");
        return new DynamicStation(id, shortTitle, stream64,
                stream128, stream320, imageGray, imageColor, imageWhite);
    }

}
