package com.dakare.radiorecord.app.quality;

import com.dakare.radiorecord.app.R;
import lombok.Getter;

@Getter
public enum Quality {
    AAC("aac", R.string.quality_aac),
    LOW("32", R.string.quality_low),
    AAC_64("aac_64", R.string.quality_medium_aac),
    MEDIUM("128", R.string.quality_medium),
    HIGH("320", R.string.quality_high);

    @Deprecated
    private final String bitrate;
    private final int nameRes;

    Quality(final String bitrate, final int name) {
        this.bitrate = bitrate;
        this.nameRes = name;
    }
}
