package com.dakare.radiorecord.app.player.service;

import android.content.Context;
import android.util.Log;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.UpdateResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class MetadataLoader implements Runnable
{
    private final String URL = "https://www.radiorecord.ru/xml/{station}_online_v8.txt";

    private final AtomicBoolean playing = new AtomicBoolean();
    private Station station;
    private final RestTemplate template = new RestTemplate();
    @Getter
    private UpdateResponse response = new UpdateResponse();
    private final MetadataChangeCallback callback;
    private final Context context;
    private Thread thread;

    public MetadataLoader(final MetadataChangeCallback callback, final Context context)
    {
        this.callback = callback;
        this.context = context;
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json", GsonHttpMessageConverter.DEFAULT_CHARSET),
                new MediaType("text", "plain", GsonHttpMessageConverter.DEFAULT_CHARSET)));
        template.getMessageConverters().add(gsonHttpMessageConverter);
        template.setErrorHandler(new ResponseErrorHandler()
        {
            @Override
            public boolean hasError(final ClientHttpResponse response) throws IOException
            {
                return response.getStatusCode() != HttpStatus.OK;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException
            {
                Log.w("Update Task", "Cannot parse response");
            }
        });
    }

    public void start(final Station station)
    {
        this.station = station;
        if (playing.compareAndSet(false, true))
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop()
    {
        playing.set(false);
        if (thread != null)
        {
            thread.interrupt();
        }
    }

    @Override
    public void run()
    {
        while (playing.get() && thread == Thread.currentThread())
        {
            if (PreferenceManager.getInstance(context).isMusicMetadataEnabled())
            {
                UpdateResponse updateResponse = null;
                try
                {
                    ResponseEntity<UpdateResponse> response = template.getForEntity(URL, UpdateResponse.class, station.getCode());
                    if (response.getStatusCode() == HttpStatus.OK)
                    {
                        updateResponse = response.getBody();
                    } else
                    {
                        Log.w("MetadataLoader", "Received bad response " + response.getStatusCode());
                    }
                } catch (Exception e)
                {
                    Log.e("Update Task", "Failed to load update", e);
                }
                if (updateResponse == null)
                {
                    updateResponse = new UpdateResponse();
                }
                publishProgress(updateResponse);
            }
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e)
            {
                Log.w("Update task", "Exception in update task", e);
            }
        }
    }

    private void publishProgress(final UpdateResponse response)
    {
        if (playing.get())
        {

            if (!response.equals(this.response))
            {
                this.response = response;
                callback.onMetadataChanged();
            }
        }
    }


    public interface MetadataChangeCallback
    {
        void onMetadataChanged();
    }
}
