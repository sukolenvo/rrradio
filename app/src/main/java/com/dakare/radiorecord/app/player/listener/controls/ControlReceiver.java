package com.dakare.radiorecord.app.player.listener.controls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;

public class ControlReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            final KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                String action = null;
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        action = NotificationListener.ACTION_PLAY_PAUSE;
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        action = NotificationListener.ACTION_NEXT;
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        action = NotificationListener.ACTION_PREVIOUS;
                        break;
                    default:
                        Log.i("ControlReceiver", "Ignoring unknown key event " + event.getKeyCode());
                }
                if (action != null) {
                    Intent service = new Intent(context, PlayerService.class);
                    service.setAction(action);
                    context.startService(service);
                }
            }
        }
    }
}