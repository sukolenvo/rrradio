package com.dakare.radiorecord.app.quality;

import lombok.Getter;

@Getter
public enum Quality
{
    LOW("aac", 32),
    MEDIUM("128", 128),
    HIGH("320", 320);

    private final String suffix;
    private final int bitrate;

    Quality(final String suffix, final int bitrate)
    {
        this.suffix = suffix;
        this.bitrate = bitrate;
    }
}
