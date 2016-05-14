package com.dakare.radiorecord.app.quality;

import com.dakare.radiorecord.app.R;
import lombok.Getter;

@Getter
public enum Quality
{
    LOW("aac", 64, R.string.quality_low),
    MEDIUM("128", 128, R.string.quality_medium),
    HIGH("320", 320, R.string.quality_high);

    private final String suffix;
    private final int bitrate;
    private final int nameRes;

    Quality(final String suffix, final int bitrate, final int name)
    {
        this.suffix = suffix;
        this.bitrate = bitrate;
        this.nameRes = name;
    }
}
