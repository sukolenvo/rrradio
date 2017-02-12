package com.dakare.radiorecord.app.view.theme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;

public class ThemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        setTheme(getThemeId(PreferenceManager.getInstance(this).getTheme()));
        super.onCreate(savedInstanceState);
    }

    protected int getThemeId(final Theme theme) {
        switch (theme) {
            case DARK:
                return R.style.MainDark;
            case CLASSIC:
                return R.style.Classic;
            case LIGHT:
            default:
                return R.style.Main;
        }
    }
}
