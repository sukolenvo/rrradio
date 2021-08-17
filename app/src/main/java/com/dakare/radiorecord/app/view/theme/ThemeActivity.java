package com.dakare.radiorecord.app.view.theme;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
            case LIGHT:
            default:
                return R.style.Main;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
