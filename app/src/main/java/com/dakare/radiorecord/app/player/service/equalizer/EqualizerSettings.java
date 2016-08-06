package com.dakare.radiorecord.app.player.service.equalizer;

import android.media.audiofx.Equalizer;
import android.util.Log;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EqualizerSettings {

    private boolean enabled;
    private int[] bands;
    private int[] range;
    private int[] levels;

    public EqualizerSettings(final Equalizer equalizer) {
        enabled = true;
        bands = new int[equalizer.getNumberOfBands()];
        levels = new int[equalizer.getNumberOfBands()];
        for (int i = 0; i < bands.length; i++) {
            bands[i] = equalizer.getCenterFreq((short) i);
            levels[i] = equalizer.getBandLevel((short) i);
        }
        short[] rangeShort = equalizer.getBandLevelRange();
        range = new int[2];
        range[0] = rangeShort[0];
        range[1] = rangeShort[1];
    }

    public int getLevelRange() {
        return range[1] - range[0];
    }

    public void applyLevels(final Equalizer equalizer) {
        try
        {
            if (levels != null & levels.length > 0) {
                for (int i = 0; i < levels.length; i++) {
                    equalizer.setBandLevel((short) i, (short) levels[i]);
                }
            }
        } catch (Exception e) {
            Log.e("Equalizer", "Failed to apply levels", e);
        }
    }
}
