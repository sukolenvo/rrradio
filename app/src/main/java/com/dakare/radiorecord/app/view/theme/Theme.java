package com.dakare.radiorecord.app.view.theme;

import com.dakare.radiorecord.app.R;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Theme {

    CLASSIC(R.string.classic_theme_name),
    LIGHT(R.string.light_theme_name),
    DARK(R.string.dark_theme_name);

    private final int nameRes;
}
