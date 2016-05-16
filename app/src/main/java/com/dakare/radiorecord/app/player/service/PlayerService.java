package com.dakare.radiorecord.app.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import com.dakare.radiorecord.app.player.listener.IncomeCallListener;
import com.dakare.radiorecord.app.player.listener.NotificationListener;

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
        player = new Player(this);
        messageHandler = new PlayerServiceMessageHandler(player);
		messenger = new Messenger(messageHandler);
		messageHandler.addPlayerStateListener(new NotificationListener(this));
        messageHandler.addPlayerStateListener(new IncomeCallListener(this, player));
	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
        if (intent != null && intent.hasExtra(PLAYLIST_KEY))
        {
            player.play((ArrayList)intent.getParcelableArrayListExtra(PLAYLIST_KEY), intent.getIntExtra(POSITION_KEY, 0));
        } else if (NotificationListener.ACTION_NEXT.equals(intent.getAction()))
		{
			player.next();
		} else if (NotificationListener.ACTION_PREVIOUS.equals(intent.getAction()))
		{
			player.previous();
		} else if (NotificationListener.ACTION_STOP.equals(intent.getAction()))
		{
			player.stop();
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
	}

}
