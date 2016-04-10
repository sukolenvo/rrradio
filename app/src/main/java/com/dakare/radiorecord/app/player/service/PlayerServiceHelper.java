package com.dakare.radiorecord.app.player.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import lombok.Getter;

public class PlayerServiceHelper implements ServiceConnection {

	@Getter
	private final PlayerServiceClient serviceClient;
	private ServiceBindListener bindListener;
	private boolean isBound;

	public PlayerServiceHelper() {
		serviceClient = new PlayerServiceClient();
	}

	public void bindService(final Context context,
			final ServiceBindListener bindListener) {
		this.bindListener = bindListener;
		Intent bindIntent = new Intent(context, PlayerService.class);
		context.bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
		isBound = true;
	}

	public void unbindService(final Context context) {
		if (isBound) {
			serviceClient.stopReceivingServiceResponses();
			context.unbindService(this);
			isBound = false;
		}
	}

	@Override
	public void onServiceConnected(final ComponentName name,
			final IBinder service) {
		if (isBound) {
			serviceClient.startMessagingSession(service);
			if (bindListener != null) {
				bindListener.onServiceConnected();
			}			
		}
	}

	@Override
	public void onServiceDisconnected(final ComponentName name) {
		if (bindListener != null) {
			bindListener.onServiceDisconnected();
		}
		if (!isBound) {
			bindListener = null;
		}
		serviceClient.stopMessagingSession();
	}

	public interface ServiceBindListener {

		void onServiceConnected();

		void onServiceDisconnected();
	}
}
