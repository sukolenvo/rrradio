package com.dakare.radiorecord.app.utils;

import android.media.audiofx.AudioEffect;

import java.util.UUID;

public class EqUtils {

    public static boolean isEqAvailable() {
        try {
            AudioEffect.Descriptor[] descriptors = AudioEffect.queryEffects();
            for (AudioEffect.Descriptor descriptor : descriptors) {
                if (descriptor.type.equals(UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b"))) {
                    return true;
                }
            }
        } catch (NoClassDefFoundError e) {
            return false;
        }
        return false;
    }
}
