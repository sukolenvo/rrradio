package com.dakare.radiorecord.app.player;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.PlayerBackgroundImage;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerServiceClient;
import com.dakare.radiorecord.app.player.service.PlayerServiceHelper;
import com.dakare.radiorecord.app.player.service.message.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class PlayerActivity extends MenuActivity implements PlayerServiceHelper.ServiceBindListener, PlayerServiceClient.PlayerMessageHandler
{
    private static final String METADATA_ICON_KEY = "player_metadata_icon";
    private static final String METADATA_ARTIST_KEY = "player_metadata_artist";
    private static final String METADATA_SONG_KEY = "player_metadata_song";

    private final PlayerServiceHelper playerServiceHelper = new PlayerServiceHelper();
    private TextView executor;
    private TextView song;
    private PlayerBackgroundImage icon;
    private boolean playing;
    private ArrayList<PlaylistItem> items;
    private int position;
    private View playButton;
    private String metadataIcon;
    private String metadataArtist;
    private String metadataSong;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_player_background)
            .showImageOnFail(R.drawable.default_player_background).build();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initToolbar();
        if (savedInstanceState != null)
        {
            metadataIcon = savedInstanceState.getString(METADATA_ICON_KEY);
            metadataArtist = savedInstanceState.getString(METADATA_ARTIST_KEY);
            metadataSong = savedInstanceState.getString(METADATA_SONG_KEY);
        }
        playButton = findViewById(R.id.play_button);
        executor = (TextView) findViewById(R.id.executor);
        song = (TextView) findViewById(R.id.song_name);
        icon = (PlayerBackgroundImage) findViewById(R.id.player_icon);
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
                if (items != null && playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                {
                    if (playing)
                    {
                        playerServiceHelper.getServiceClient().execute(new StopPlayerMessage());
                    } else
                    {
                        startPlayback();
                    }
                }
            }
        });
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (items != null && playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                {
                    position = (position + 1) % items.size();
                    startPlayback();
                }
            }
        });
        findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (items != null && playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                {
                    position = (position - 1 + items.size()) % items.size();
                    startPlayback();
                }
            }
        });
    }

    private void startPlayback()
    {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra(PlayerService.PLAYLIST_KEY, items);
        serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
        startService(serviceIntent);
    }

    private void updateViews()
    {
        ((ImageView) playButton).setImageResource(playing ? R.drawable.ic_stop_white_24dp : R.drawable.ic_play_arrow_white_24dp);
        PlaylistItem item = items == null ? null : items.get(position);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            executor.setText(metadataArtist == null ? (item == null ? "" : item.getTitle()) : metadataArtist);
            song.setText(metadataSong == null ? (item == null ? "" : item.getSubtitle()) : metadataSong);
        } else
        {
            if (metadataArtist == null)
            {
                setTitle(item == null ? getString(R.string.app_name) : item.getTitle());
            } else if (metadataSong == null)
            {
                setTitle(metadataArtist);
            } else
            {
                setTitle(metadataArtist + " - " + metadataSong);
            }
        }
        if (PreferenceManager.getInstance(this).isMusicImageEnabled())
        {
            ImageLoader.getInstance().displayImage(metadataIcon, icon, options);
        } else
        {
            icon.setImageResource(R.drawable.default_player_background);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(this);
        playerServiceHelper.bindService(this, this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(null);
        playerServiceHelper.unbindService(this);
    }

    @Override
    public void onServiceConnected()
    {
        playerServiceHelper.getServiceClient().execute(new UpdateStatePlayerMessage());
    }

    @Override
    public void onServiceDisconnected()
    {
        //Nothing to do for now
    }

    @Override
    public void onMessage(final PlayerMessage playerMessage)
    {
        if (playerMessage.getMessageType() == PlayerMessageType.PLAYBACK_STATE)
        {
            PlaybackStatePlayerMessage playbackState = (PlaybackStatePlayerMessage) playerMessage;
            this.items = playbackState.getItems();
            this.position = playbackState.getPosition();
            this.playing = playbackState.isPlaying();
            metadataIcon = playbackState.getIcon();
            metadataArtist = playbackState.getArtist();
            metadataSong = playbackState.getSong();
            updateViews();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(METADATA_ICON_KEY, metadataIcon);
        outState.putString(METADATA_ARTIST_KEY, metadataArtist);
        outState.putString(METADATA_SONG_KEY, metadataSong);
    }
}
