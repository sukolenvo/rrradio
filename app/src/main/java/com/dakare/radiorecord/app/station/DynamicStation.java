package com.dakare.radiorecord.app.station;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.view.theme.Theme;

import java.io.File;
import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@EqualsAndHashCode
@Getter
@AllArgsConstructor
public class DynamicStation {

    private static final String DELIMITER = ":";
    public static final DynamicStation DEFAULT = new DynamicStation(
            15016,
            "Record",
            "https://radiorecord.hostingradio.ru/rr_main64.aacp",
            "https://radiorecord.hostingradio.ru/rr_main64.aacp",
            "https://radiorecord.hostingradio.ru/rr_main64.aacp",
            "https://radiorecord.ru/upload/stations_images/record_image600_gray_outline.png",
            "https://radiorecord.ru/upload/stations_images/record_image600_gray_outline.png",
            "https://radiorecord.ru/upload/stations_images/record_image600_gray_outline.png"
    );

    private final long id;
    private final String shortTitle;
    private final String stream64;
    private final String stream128;
    private final String stream320;
    private final String imageGray;
    private final String imageColor;
    private final String imageWhite;

    DynamicStation(String serializedString) {
        String[] values = serializedString.split(DELIMITER);
        // values[0] == v3
        id = Long.parseLong(values[1]);
        shortTitle = new String(Base64.decode(values[2], Base64.DEFAULT), StandardCharsets.UTF_8);
        stream64 = new String(Base64.decode(values[3], Base64.DEFAULT), StandardCharsets.UTF_8);
        stream128 = new String(Base64.decode(values[4], Base64.DEFAULT), StandardCharsets.UTF_8);
        stream320 = new String(Base64.decode(values[5], Base64.DEFAULT), StandardCharsets.UTF_8);
        imageGray = new String(Base64.decode(values[6], Base64.DEFAULT), StandardCharsets.UTF_8);
        imageColor = new String(Base64.decode(values[7], Base64.DEFAULT), StandardCharsets.UTF_8);
        imageWhite = new String(Base64.decode(values[8], Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public String getKey() {
        return String.valueOf(id);
    }

    public String getName() {
        return shortTitle;
    }

    public Bitmap getNotificationStationIcon() {
        return loadBitmap();
    }

    private Bitmap loadBitmap() {
        return BitmapFactory.decodeFile(
                new File(RecordApplication.getInstance().getFilesDir(), getKey() + ".png").getAbsolutePath());
    }

    public Bitmap getStationIcon(Theme theme) {
        return loadBitmap();
    }

    public String serialize() {
        return "v3:" +
                + id + DELIMITER
                + Base64.encodeToString(shortTitle.getBytes(), Base64.DEFAULT) + DELIMITER
                + Base64.encodeToString(stream64.getBytes(), Base64.DEFAULT) + DELIMITER
                + Base64.encodeToString(stream128.getBytes(), Base64.DEFAULT) + DELIMITER
                + Base64.encodeToString(stream320.getBytes(), Base64.DEFAULT) + DELIMITER
                + Base64.encodeToString(imageGray.getBytes(), Base64.DEFAULT) + DELIMITER
                + Base64.encodeToString(imageColor.getBytes(), Base64.DEFAULT) + DELIMITER
                + Base64.encodeToString(imageWhite.getBytes(), Base64.DEFAULT) + DELIMITER;
    }

    public String getStreamUrl(Quality quality) {
        switch (quality) {
            case AAC:
            case AAC_64:
                return stream64;
            case MEDIUM:
                return stream128;
            default:
            case HIGH:
                return stream320;
        }
    }

    public static DynamicStation deserialize(String value) {
        return new DynamicStation(value);
    }
}
