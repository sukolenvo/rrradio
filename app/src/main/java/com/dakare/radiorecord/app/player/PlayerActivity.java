package com.dakare.radiorecord.app.player;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dakare.radiorecord.app.*;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerServiceClient;
import com.dakare.radiorecord.app.player.service.PlayerServiceHelper;
import com.dakare.radiorecord.app.player.service.message.*;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityDialog;

import java.util.List;

public class PlayerActivity extends MenuActivity implements PlayerServiceHelper.ServiceBindListener, PlayerServiceClient.PlayerMessageHandler, MetadataHandler
{

    private final PlayerServiceHelper playerServiceHelper = new PlayerServiceHelper();
    private Station station;
    private TextView executor;
    private TextView song;
    private PlayerBackgroundImage icon;
    private boolean playing;
    private View playButton;
    private UpdateTask updateTask;
    private List<Station> stations;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initToolbar();
        playButton = findViewById(R.id.play_button);
        executor = (TextView) findViewById(R.id.executor);
        song = (TextView) findViewById(R.id.song_name);
        icon = (PlayerBackgroundImage) findViewById(R.id.player_icon);
        stations = PreferenceManager.getInstance(this).getStations();
        station = PreferenceManager.getInstance(this).getLastStation();
        setupOnclickListeners();
        updateViews();
        hidePlayerMenuButton();
    }

    private void setupOnclickListeners()
    {
        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (playing)
                {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                    {
                        playerServiceHelper.getServiceClient().execute(new StopPlayerMessage());
                    }
                } else
                {
                    startPlayback();
                }
            }
        });
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                station = stations.get((stations.indexOf(station) + 1) % stations.size());
                updateViews();
                startPlayback();
            }
        });
        findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                station = stations.get((stations.indexOf(station) - 1 + stations.size()) % stations.size());
                updateViews();
                startPlayback();
            }
        });
    }

    private void startPlayback()
    {
        PreferenceManager.getInstance(this).setLastStation(station);
        QualityDialog.getQuality(PlayerActivity.this, new QualityDialog.QualityHandler()
        {
            @Override
            public void onQualitySelected(Quality quality)
            {
                Intent serviceIntent = new Intent(PlayerActivity.this, PlayerService.class);
                serviceIntent.putExtra(PlayerService.STATION_KEY, station.name());
                serviceIntent.putExtra(PlayerService.QUALITY_KEY, quality.name());
                startService(serviceIntent);
            }
        });
    }

    private void updateViews()
    {
        ((ImageView) playButton).setImageResource(playing ? R.drawable.ic_stop_white_24dp : R.drawable.ic_play_arrow_white_24dp);
        executor.setText("");
        song.setText("");
        if (playing)
        {
            updateTaskRecreate();
        } else if (updateTask != null)
        {
            updateTask.cancel(true);
        }
        if (station == null)
        {
            setTitle(R.string.app_name);
        } else
        {
            setTitle(station.getName());
        }
    }

    private void updateTaskRecreate()
    {
        if (updateTask != null)
        {
            updateTask.cancel(true);
        }
        updateTask = new UpdateTask(icon, station, this);
        updateTask.execute();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(this);
        playerServiceHelper.bindService(this, this);
        updateTaskRecreate();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(null);
        playerServiceHelper.unbindService(this);
        if (updateTask != null)
        {
            updateTask.cancel(true);
        }
    }

    @Override
    public void onServiceConnected()
    {
        playerServiceHelper.getServiceClient().execute(new UpdateStatePlayerMessage());
    }

    @Override
    public void onServiceDisconnected()
    {
    }

    @Override
    public void onMessage(PlayerMessage playerMessage)
    {
        if (playerMessage.getMessageType() == PlayerMessageType.PLAYBACK_STATE)
        {
            PlaybackStatePlayerMessage playbackState = (PlaybackStatePlayerMessage) playerMessage;
            if (this.station != playbackState.getStation() || this.playing != playbackState.isPlaying())
            {
                this.playing = playbackState.isPlaying();
                if (playbackState.getStation() != null)
                {
                    this.station = playbackState.getStation();
                }
                updateViews();
            }
        }
    }

    @Override
    public void onMetadataChanged(final String artist, final String song)
    {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            executor.setText(artist);
            this.song.setText(song);
        } else
        {
            setTitle(artist + " - " + song);
        }
    }
}
