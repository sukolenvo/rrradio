package com.dakare.radiorecord.app.quality;

import com.dakare.radiorecord.app.R;
import lombok.Getter;

@Getter
public enum Quality
{
    LOW(64, R.string.quality_low),
    MEDIUM(128, R.string.quality_medium),
    HIGH(320, R.string.quality_high);

    private final int bitrate;
    private final int nameRes;

    Quality(final int bitrate, final int name)
    {
        this.bitrate = bitrate;
        this.nameRes = name;
    }
}
