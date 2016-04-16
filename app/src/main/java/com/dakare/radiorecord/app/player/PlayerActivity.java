package com.dakare.radiorecord.app.player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dakare.radiorecord.app.*;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.message.*;
import com.dakare.radiorecord.app.player.service.PlayerServiceClient;
import com.dakare.radiorecord.app.player.service.PlayerServiceHelper;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityDialog;

public class PlayerActivity extends Activity implements PlayerServiceHelper.ServiceBindListener, PlayerServiceClient.PlayerMessageHandler
{

    private final PlayerServiceHelper playerServiceHelper = new PlayerServiceHelper();
    private Station station = Station.RADIO_RECORD;
    private TextView executor;
    private TextView song;
    private ImageView icon;
    private boolean playing;
    private View playButton;
    private UpdateTask updateTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playButton = findViewById(R.id.play_button);
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
            }
        });
        executor = (TextView) findViewById(R.id.executor);
        song = (TextView) findViewById(R.id.song_name);
        icon = (ImageView) findViewById(R.id.player_icon);
        findViewById(R.id.player_control).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                findViewById(R.id.player_control).getViewTreeObserver().removeGlobalOnLayoutListener(this);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playButton.getLayoutParams();
                layoutParams.topMargin = (int) findViewById(R.id.player_control).getY() - playButton.getHeight() / 2;
                playButton.setLayoutParams(layoutParams);
                playButton.requestLayout();
            }
        });
        updateViews();
    }

    private void updateViews()
    {
        ((ImageView) playButton).setImageResource(playing ? R.drawable.pause : R.drawable.play);
        executor.setText(station.getName());
        song.setText("");
        if (playing)
        {
            updateTaskRecreate();
        } else if (updateTask != null)
        {
            updateTask.cancel(true);
        }
    }

    private void updateTaskRecreate()
    {
        if (updateTask != null)
        {
            updateTask.cancel(true);
        }
        updateTask = new UpdateTask(executor, song, icon, station);
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
}
