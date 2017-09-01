package com.dakare.radiorecord.app.utils;

import android.media.audiofx.AudioEffect;
import android.os.Build;
import com.crashlytics.android.Crashlytics;

import java.util.UUID;

public class EqUtils {

    public static boolean isEqAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                AudioEffect.Descriptor[] descriptors = AudioEffect.queryEffects();
                for (AudioEffect.Descriptor descriptor : descriptors) {
                    if (descriptor.type.equals(UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b"))) {
                        return true;
                    }
                }
            } catch (NoClassDefFoundError e) {
                Crashlytics.logException(e);
                return false;
            }
        }
        return false;
    }
}
