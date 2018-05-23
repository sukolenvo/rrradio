package com.dakare.radiorecord.app.view.theme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return super.onKeyDown(keyCode, event);
        }
        String action = null;
        //todo: deduplicate ControlReceiver
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                action = NotificationListener.ACTION_PLAY_PAUSE;
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                action = NotificationListener.ACTION_NEXT;
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                action = NotificationListener.ACTION_PREVIOUS;
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                action = NotificationListener.ACTION_STOP;
                break;
            default:
                Log.i("ThemeActivity", "Ignoring unknown key event " + event.getKeyCode());
        }
        if (action != null) {
            Intent service = new Intent(this, PlayerService.class);
            service.setAction(action);
            ContextCompat.startForegroundService(this, service);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
