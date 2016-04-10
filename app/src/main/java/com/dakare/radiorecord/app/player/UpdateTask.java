package com.dakare.radiorecord.app.player;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

public class UpdateTask extends AsyncTask<Void, UpdateResponse, Void>
{
    private final String URL = "https://www.radiorecord.ru/xml/{station}_online_v8.txt";
    private final TextView executor;
    private final TextView song;
    private final ImageView icon;
    private final Station station;
    private final RestTemplate template = new RestTemplate();
    private final ImageLoader imageLoader;
    private UpdateResponse response = new UpdateResponse();
    private final DisplayImageOptions options;


    public UpdateTask(TextView executor, TextView song, ImageView icon, Station station)
    {
        this.executor = executor;
        this.song = song;
        this.icon = icon;
        this.station = station;
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("application", "json", GsonHttpMessageConverter.DEFAULT_CHARSET),
                new MediaType("text", "plain", GsonHttpMessageConverter.DEFAULT_CHARSET)));
        template.getMessageConverters().add(gsonHttpMessageConverter);
        template.setErrorHandler(new ResponseErrorHandler()
        {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException
            {
                return response.getStatusCode() != HttpStatus.OK;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException
            {
                Log.w("Update Task", "Cannot parse response");
            }
        });
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().showImageOnFail(R.drawable.default_player_background).build();
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        while(!isCancelled())
        {
            try
            {
                ResponseEntity<UpdateResponse> response = template.getForEntity(URL, UpdateResponse.class, station.getCode());
                if (response.getStatusCode() == HttpStatus.OK)
                {
                    publishProgress(response.getBody());
                } else
                {
                    Log.w("UpdateTask", "Received bad response " + response.getStatusCode());
                }
            } catch (Exception e)
            {
                Log.e("Update Task", "Failed to load update", e);
            }
            try
            {
                Thread.sleep(5000);
            } catch (InterruptedException e)
            {
                Log.w("Update task", "Exception in update task", e);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(UpdateResponse... values)
    {
        super.onProgressUpdate(values);
        if (!isCancelled())
        {
            if (values.length == 1 && values[0] != null)
            {
                if (values[0].getImage600() != null && !values[0].getImage600().equals(response.getImage600()))
                {
                    imageLoader.displayImage(values[0].getImage600(), icon, options);
                }
                if (!getArtistString(values[0]).equals(getArtistString(response)))
                {
                    executor.setText(getArtistString(values[0]));
                }

                if (!getSongString(values[0]).equals(getSongString(response)))
                {
                    song.setText(getSongString(values[0]));
                }
                response = values[0];
            } else
            {
                Log.w("Update Task", "Got invalid update args " + values);
            }
        }
    }

    private String getArtistString(UpdateResponse response)
    {
        return response.getArtist() == null ? station.getName() : response.getArtist();
    }

    private String getSongString(UpdateResponse response)
    {
        return response.getTitle() == null ? "" : response.getTitle();
    }
}
