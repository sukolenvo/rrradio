package com.dakare.radiorecord.app.download.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;

public class FileService extends Service {

    public static final String IDS_KEY = "audio_ids";

	private FileServiceMessageHandler messageHandler;
	private Messenger messenger;
    private DownloadManager downloadManager;

	@Override
	public void onCreate() {
		super.onCreate();
        messageHandler = new FileServiceMessageHandler();
		messenger = new Messenger(messageHandler);
        downloadManager = new DownloadManager(messageHandler, this);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        if (intent != null && intent.hasExtra(IDS_KEY))
        {
            downloadManager.delete(intent.getIntegerArrayListExtra(IDS_KEY));
        } else
        {
            downloadManager.updateQueue();
        }
		return START_STICKY;
	}

	@Override
	public IBinder onBind(final Intent arg0) {
		return messenger.getBinder();
	}

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        downloadManager.shutdown();
    }
}
