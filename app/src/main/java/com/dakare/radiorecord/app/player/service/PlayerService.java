package com.dakare.radiorecord.app.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.player.listener.IPlayerStateListener;
import com.dakare.radiorecord.app.player.listener.HeadsetUnplugListener;
import com.dakare.radiorecord.app.player.listener.LockListener;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.listener.controls.MediaControlsListener;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.playback.Player;
import com.dakare.radiorecord.app.player.service.playback.PlayerImpl;
import com.dakare.radiorecord.app.player.service.playback.PlayerJellybean;
import com.dakare.radiorecord.app.widget.WidgetListener;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service {

    public static final String PLAYLIST_KEY = "playlist";
    public static final String POSITION_KEY = "position";

    private PlayerServiceMessageHandler messageHandler;
    private Messenger messenger;
    private Player player;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            player = new PlayerImpl(this);
        } else {
            player = new PlayerJellybean(this);
        }
        messageHandler = new PlayerServiceMessageHandler(player);
        messenger = new Messenger(messageHandler);
        messageHandler.addPlayerStateListener(new NotificationListener(this));
        messageHandler.addPlayerStateListener(new HeadsetUnplugListener(this));
        messageHandler.addPlayerStateListener(new WidgetListener(this));
        messageHandler.addPlayerStateListener(new MediaControlsListener(this));
        messageHandler.addPlayerStateListener(new LockListener(this));
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (intent != null) {
            if (intent.hasExtra(PLAYLIST_KEY)) {
                ArrayList<PlaylistItem> playlist = (ArrayList) intent.getParcelableArrayListExtra(PLAYLIST_KEY);
                PreferenceManager.getInstance(this).setLastPlaylist(playlist);
                player.play(playlist, intent.getIntExtra(POSITION_KEY, 0));
            } else if (NotificationListener.ACTION_NEXT.equals(intent.getAction())) {
                player.next();
            } else if (NotificationListener.ACTION_PREVIOUS.equals(intent.getAction())) {
                player.previous();
            } else if (NotificationListener.ACTION_STOP.equals(intent.getAction())) {
                player.stop();
            } else if (NotificationListener.ACTION_PAUSE.equals(intent.getAction())) {
                player.pause();
            } else if (NotificationListener.ACTION_RESUME.equals(intent.getAction())) {
                player.resume();
            } else if (NotificationListener.ACTION_PLAY_PAUSE.equals(intent.getAction())) {
                if (player.getState() == PlayerState.PLAY) {
                    player.pause();
                } else {
                    player.resume();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(final Intent arg0) {
        return messenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (IPlayerStateListener listener : messageHandler.getListeners()) {
            if (listener instanceof Closeable) {
                try {
                    ((Closeable) listener).close();
                } catch (IOException e) {
                    Log.e("PlayerService", "Got exception while closing listener" + listener.getClass().getSimpleName());
                }
            }
        }
    }
}
