package com.dakare.radiorecord.app.download.service;

import android.database.Cursor;
import android.util.Log;
import com.dakare.radiorecord.app.database.DownloadAudioTable;
import lombok.Getter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask implements Runnable
{
    @Getter
    private final long id;
    private final String url;
    @Getter
    private final File destinationFile;
    private final String directory;
    private final byte[] buffer;
    private boolean stop = false;
    @Getter
    private int length;
    private final DownloadListener listener;
    private HttpURLConnection connection;
    private long lastUpdate = 0;
    @Getter
    private boolean remove;

    public DownloadTask(final Cursor cursor, final DownloadListener listener)
    {
        this.listener = listener;
        id = cursor.getLong(cursor.getColumnIndex(DownloadAudioTable.COLUMN_ID));
        url = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_URL));
        String title = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_TITLE));
        directory = cursor.getString(cursor.getColumnIndex(DownloadAudioTable.COLUMN_DIRECTORY));
        destinationFile = DownloadManager.getAudioFile(directory, id, title);
        buffer = new byte[4096];
    }

    @Override
    public void run()
    {
        File dir = new File(directory);
        if (!dir.exists())
        {
            if (!dir.mkdirs())
            {
                listener.notifyError(id, DownloadAudioTable.Status.ERROR_SAVING);
                return;
            }
        }
        listener.notifyStart(id);
        while (!stop)
        {
            long ready = destinationFile.length();
            int responseCode = connect(ready);
            if (responseCode == 404)
            {
                listener.notifyError(id, DownloadAudioTable.Status.ERROR_FILE_MISSING);
                break;
            }
            if (responseCode >= 200 && responseCode < 300)
            {
                if (!startCopy(ready))
                {
                    break;
                }
                Log.i("Download Task", "Reconnecting...");
                continue;
            }
            if (responseCode == 416)
            {
                length = (int) ready;
                break;
            }
            Log.e("DownloadTask", "Unexpected response code " + responseCode);
            listener.notifyExit(id, false);
            return;
        }
        listener.notifyExit(id, true);
    }

    private int connect(final long skip)
    {
        try
        {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(60000);
            connection.setRequestProperty("Range", "bytes=" + skip + "-");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.63 Safari/537.36");
            connection.setRequestProperty("Referer", url);
            connection.setRequestProperty("Accept-Encoding", "identity;q=1, *;q=0");
            connection.setRequestProperty("Accept", "*/*");
            int responseCode = connection.getResponseCode();
            return responseCode;
        } catch (IOException e)
        {
            Log.w("DownloadTask", "Cannot connect to server");
            return 400;
        }
    }

    private boolean startCopy(final long skip)
    {
        FileOutputStream out = null;
        try
        {
            try
            {
                out = new FileOutputStream(destinationFile, true);
            } catch (FileNotFoundException e)
            {
                Log.e("DownloadTask", "Failed to open out stream");
                listener.notifyError(id, DownloadAudioTable.Status.ERROR_SAVING);
                return false;
            }
            if (connection.getContentLength() > 0)
            {
                length = (int) (connection.getContentLength() + skip);
            }
            try
            {
                while (!stop)
                {
                    int readCount = connection.getInputStream().read(buffer);
                    if (readCount == -1)
                    {
                        return false;
                    } else
                    {
                        try
                        {
                            out.write(buffer, 0, readCount);
                            out.flush();
                            if (System.currentTimeMillis() - lastUpdate > 1000)
                            {
                                listener.updateSize(id, destinationFile.length(), length);
                                lastUpdate = System.currentTimeMillis();
                            }
                        } catch (IOException e)
                        {
                            Log.e("DownloadTask", "Failed to write portion");
                            listener.notifyError(id, DownloadAudioTable.Status.ERROR_SAVING);
                            return false;
                        }
                    }
                }
                return false;
            } catch (IOException e)
            {
                Log.w("DownloadTask", "Connection refused", e);
                return true;
            }
        } finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            } catch (IOException e)
            {
                Log.w("DownloadTask", "Failed to close out stream");
            }
        }
    }

    public void cancel(final boolean remove)
    {
        stop = true;
        this.remove = remove;
    }

    public interface DownloadListener
    {
        void notifyError(long id, DownloadAudioTable.Status error);

        void notifyExit(long id, boolean success);

        void notifyStart(long id);

        void updateSize(long id, long size, long total);
    }
}
