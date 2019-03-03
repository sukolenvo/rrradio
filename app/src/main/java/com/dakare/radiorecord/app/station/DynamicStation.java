package com.dakare.radiorecord.app.station;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.view.theme.Theme;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;


@Getter
@AllArgsConstructor
public class DynamicStation extends AbstractStation {

    private static final String DELIMITER = ":";

    private final String name;
    private final String prefix;

    DynamicStation(String serializedString) {
        name = new String(Base64.decode(serializedString.split(":")[1], Base64.DEFAULT));
        prefix = serializedString.split(":")[2];
    }

    @Override
    public String getCode() {
        return "/" + prefix;
    }

    @Override
    public Bitmap getNotificationStationIcon() {
        return loadBitmap();
    }

    private Bitmap loadBitmap() {
        return BitmapFactory.decodeFile(
                new File(RecordApplication.getInstance().getFilesDir(), prefix + ".png").getAbsolutePath());
    }

    @Override
    public Bitmap getStationIcon(Theme theme) {
        return loadBitmap();
    }

    @Override
    public String serialize() {
        return "DynamicStation:" + Base64.encodeToString(name.getBytes(), Base64.DEFAULT) + DELIMITER + prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DynamicStation that = (DynamicStation) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return prefix != null ? prefix.equals(that.prefix) : that.prefix == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        return result;
    }
}
