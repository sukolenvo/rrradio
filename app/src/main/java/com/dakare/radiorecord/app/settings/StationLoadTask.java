package com.dakare.radiorecord.app.settings;

import android.graphics.*;
import android.os.AsyncTask;
import android.util.Log;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.station.AbstractStation;
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

    private static final String URL = "http://www.radiorecord.ru/radioapi/stations/";

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
        List<Item> items = new ArrayList<>();
        JSONObject object = new JSONObject(response);
        JSONArray resultArray = object.getJSONArray("result");
        for (int i = 0; i < resultArray.length(); i++) {
            items.add(new Item(resultArray.getJSONObject(i)));
        }
        Set<String> existingStations = new HashSet<>();
        List<AbstractStation> currentStations = new ArrayList<>(PreferenceManager.getInstance(RecordApplication.getInstance())
                .getStations());
        for (AbstractStation station : currentStations) {
            existingStations.add(station.getCodeAsParam());
        }
        List<Item> newStations = new ArrayList<>();
        for (Item item : items) {
            if (!existingStations.contains(item.code)) {
                newStations.add(item);
            }
        }
        if (newStations.isEmpty()) {
            publishProgress(RecordApplication.getInstance().getString(R.string.status_loading_stations_no_new));
        } else {
            for (int i = 0; i < newStations.size(); i++) {
                publishProgress(RecordApplication.getInstance().getString(R.string.status_loading_stations_progress,
                        i, newStations.size()));
                Item newStation = newStations.get(i);
                prepareIcons(newStation);
                for (AbstractStation currentStation : currentStations) {
                    if (currentStation.getCodeAsParam().equals(newStation.code)) {
                        currentStations.remove(currentStation);
                        break;
                    }
                }
                currentStations.add(new DynamicStation(newStation.stationName, newStation.code));
            }
            PreferenceManager.getInstance(RecordApplication.getInstance()).setStations(currentStations);
            publishProgress(RecordApplication.getInstance().getString(R.string.status_loading_stations_summary,
                    newStations.size()));
        }
    }

    private void prepareIcons(Item newStation) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(newStation.image).openConnection();
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
        File file = new File(RecordApplication.getInstance().getFilesDir(), newStation.code + ".png");
        FileOutputStream stream = new FileOutputStream(file);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } finally {
            stream.close();
        }
    }

    private Bitmap drawStub(Item newStation) {
        Bitmap bitmap = Bitmap.createBitmap(201, 140, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setARGB(255, 192, 192, 192);
        Rect output = new Rect();
        paint.getTextBounds(newStation.stationName, 0, newStation.stationName.length(), output);
        if (output.right > 90 && newStation.stationName.length() > 10) {
            String firstLine = newStation.stationName.substring(0, 10);
            String secondLine = newStation.stationName.substring(10);
            paint.getTextBounds(firstLine, 0, firstLine.length(), output);
            canvas.drawText(firstLine, Math.max(0, 100 - output.right / 2), 35, paint);
            paint.getTextBounds(secondLine, 0, secondLine.length(), output);
            canvas.drawText(secondLine, Math.max(0, 100 - output.right / 2), 75, paint);
        } else {
            canvas.drawText(newStation.stationName, Math.max(0, 100 - output.right / 2), 55, paint);
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

    private static class Item {
        String stationName;
        String code;
        String image;

        private Item(JSONObject item) throws JSONException {
            stationName = item.getString("title");
            code = item.getString("prefix");
            image = item.getString("icon_png");
        }
    }
}
