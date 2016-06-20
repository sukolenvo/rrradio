package com.dakare.radiorecord.app.quality;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class QualityAdapterItem
{
    private final Quality quality;
    private final String text;

    public QualityAdapterItem(final String text)
    {
        this.text = text;
        quality = null;
    }
}
