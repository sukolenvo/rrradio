package com.dakare.radiorecord.app.download.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.dakare.radiorecord.app.download.service.message.RemoveFileResponse;
import com.dakare.radiorecord.app.download.service.message.UpdateFileMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager extends BroadcastReceiver implements DownloadTask.DownloadListener {
    private static final int TASKS_COUNT = 2;

    private final ExecutorService executorService;
    private final List<DownloadTask> tasks = new ArrayList<>(TASKS_COUNT);
    private final FileServiceMessageHandler handler;
    private final Service service;

    public DownloadManager(final FileServiceMessageHandler handler, final Service service) {
        executorService = Executors.newFixedThreadPool(TASKS_COUNT);
        this.handler = handler;
        this.service = service;
        service.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void delete(final List<Integer> ids) {
        Cursor cursor = StorageContract.getInstance().getAudioByIds(ids);
        if (cursor == null) {
            return;
        }
        Map<Long, File> removeMap = new HashMap<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                DownloadItem downloadItem = new DownloadItem(cursor);
                removeMap.put(downloadItem.getId(), downloadItem.getFile());
            } while (cursor.moveToNext());
        }
        List<Long> idsToDrop = new ArrayList<>();
        synchronized (tasks) {

            for (Map.Entry<Long, File> entry : removeMap.entrySet()) {
                DownloadTask downloadTask = getTask(entry.getKey());
                if (downloadTask == null) {
                    idsToDrop.add(entry.getKey());
                } else {
                    downloadTask.cancel(true);
                }
            }
            if (!idsToDrop.isEmpty()) {
                StorageContract.getInstance().deleteAudio(idsToDrop);
            }
        }
        RemoveFileResponse response = new RemoveFileResponse();
        for (Long id : idsToDrop) {
            removeMap.get(id).delete();
            response.addId(id.intValue());
        }
        handler.handleServiceResponse(response);
    }

    public void updateQueue() {
        synchronized (tasks) {
            if (tasks.size() < TASKS_COUNT) {
                Cursor cursor = StorageContract.getInstance().getAudioToDownload();
                if (cursor.moveToFirst()) {
                    do {
                        DownloadTask task = new DownloadTask(cursor, this);
                        boolean add = true;
                        for (DownloadTask downloadTask : tasks) {
                            if (downloadTask.getId() == task.getId()) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            tasks.add(task);
                            executorService.execute(task);
                        }
                    } while (cursor.moveToNext() && tasks.size() < TASKS_COUNT);
                }
                cursor.close();
            }
            if (tasks.isEmpty()) {
                service.stopSelf();
            }
        }
    }

    public void shutdown() {
        synchronized (tasks) {
            for (DownloadTask task : tasks) {
                task.cancel(false);
            }
        }
        executorService.shutdown();
        service.unregisterReceiver(this);
    }

    @Override
    public void notifyError(final long id, final DownloadAudioTable.Status error) {
        StorageContract.getInstance().updateAudioStatus(id, error);
        handler.handleServiceResponse(new UpdateFileMessage(id, error));
    }

    private DownloadTask getTask(final long id) {
        synchronized (tasks) {
            for (DownloadTask task : tasks) {
                if (task.getId() == id) {
                    return task;
                }
            }
        }
        return null;
    }

    @Override
    public void notifyStart(final long id) {
        StorageContract.getInstance().updateAudioStatus(id, DownloadAudioTable.Status.DOWNLOADING);
        handler.handleServiceResponse(new UpdateFileMessage(id, DownloadAudioTable.Status.DOWNLOADING));
    }

    @Override
    public void updateSize(final long id, final long size, final long total) {
        ContentValues values = new ContentValues();
        values.put(DownloadAudioTable.COLUMN_SIZE, size);
        if (total > 0) {
            values.put(DownloadAudioTable.COLUMN_TOTAL_SIZE, total);
        }
        StorageContract.getInstance().updateAudio(id, values);
        handler.handleServiceResponse(new UpdateFileMessage(id, size, total));
    }

    @Override
    public void notifyExit(final long id, final boolean success) {
        DownloadTask task = getTask(id);
        synchronized (tasks) {
            tasks.remove(task);
        }
        if (task.isRemove()) {
            StorageContract.getInstance().deleteAudio(Arrays.asList(id));
            task.getDestinationFile().delete();
            handler.handleServiceResponse(new RemoveFileResponse((int) id));
        } else if (success) {
            ContentValues values = new ContentValues();
            values.put(DownloadAudioTable.COLUMN_STATUS, DownloadAudioTable.Status.DOWNLOADED.getCode());
            values.put(DownloadAudioTable.COLUMN_SIZE, task.getDestinationFile().length());
            if (task.getLength() > 0) {
                values.put(DownloadAudioTable.COLUMN_TOTAL_SIZE, task.getLength());
            }
            StorageContract.getInstance().updateAudio(id, values);
            UpdateFileMessage message = new UpdateFileMessage(id, DownloadAudioTable.Status.DOWNLOADED);
            message.setSize(task.getDestinationFile().length());
            message.setTotal(task.getLength());
            handler.handleServiceResponse(message);
            updateQueue();
        }
    }

    public static File getAudioFile(final String directory, final long id, final String title) {
        String fileName = id + " - " + String.valueOf(title).replaceAll("[^a-zA-Z 0-9]", "");
        if (!fileName.endsWith(".mps")) {
            fileName += ".mp3";
        }
        return new File(new File(directory), fileName);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = manager.getActiveNetworkInfo();
        if (network != null && network.isConnected()) {
            updateQueue();
        }
    }
}
