package com.dakare.radiorecord.app.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.listener.IncomeCallListener;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.quality.Quality;

public class PlayerService extends Service {

    public static final String STATION_KEY = "station";
    public static final String QUALITY_KEY = "quality";
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

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
        if (intent != null && intent.hasExtra(STATION_KEY))
        {
            player.play(Station.valueOf(intent.getStringExtra(STATION_KEY)), Quality.valueOf(intent.getStringExtra(QUALITY_KEY)));
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
