package com.dakare.radiorecord.app.station;

import android.graphics.Bitmap;
import android.util.Log;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.view.theme.Theme;

public abstract class AbstractStation {

    public abstract String getName();

    public abstract String getCode();

    public abstract Bitmap getNotificationStationIcon();

    public abstract Bitmap getStationIcon(Theme theme);

    public abstract String serialize();

    public String getCodeAsParam() {
        return getCode().substring(1);
    }

    public String getStreamUrl(final Quality quality) {
        switch (quality) {
            case AAC:
                return "http://air2.radiorecord.ru:9001" + getCode() + "_aac";
            case AAC_64:
                return "http://air2.radiorecord.ru:9001" + getCode() + "_aac_64";
            case MEDIUM:
                return "http://air2.radiorecord.ru:9002" + getCode() + "_128";
            case HIGH:
                return "http://air2.radiorecord.ru:9003" + getCode() + "_320";
            default:
                Log.w("Station", quality + " quality is not supported");
                return getStreamUrl(Quality.AAC);
        }
    }

    public static AbstractStation deserialize(String value) {
        if (value.startsWith("DynamicStation:")) {
            return new DynamicStation(value);
        }
        return new PredefinedStation(BaseStation.valueOf(value));
    }
}
