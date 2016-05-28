package com.dakare.radiorecord.app.player;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.PlayerBackgroundImage;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerServiceClient;
import com.dakare.radiorecord.app.player.service.PlayerServiceHelper;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class PlayerActivity extends MenuActivity implements PlayerServiceHelper.ServiceBindListener, PlayerServiceClient.PlayerMessageHandler, Runnable
{
    private static final String METADATA_ICON_KEY = "player_metadata_icon";
    private static final String METADATA_ARTIST_KEY = "player_metadata_artist";
    private static final String METADATA_SONG_KEY = "player_metadata_song";

    private final PlayerServiceHelper playerServiceHelper = new PlayerServiceHelper();
    private PlayerBackgroundImage icon;
    private PlayerState state;
    private ArrayList<PlaylistItem> items;
    private int position;
    private View playButton;
    private View pauseButton;
    private String metadataIcon;
    private String metadataArtist;
    private String metadataSong;
    private PlaylistAdapter adapter;
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_player_background)
            .showImageOnFail(R.drawable.default_player_background).build();
    private ListView playlistView;
    private Thread positionUpdater;
    private View playbackProgressView;

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
        pauseButton = findViewById(R.id.pause_button);
        icon = (PlayerBackgroundImage) findViewById(R.id.player_icon);
        playlistView = (ListView) findViewById(R.id.playlist);
        adapter = new PlaylistAdapter(this);
        playlistView.setAdapter(adapter);
        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
            {
                Intent serviceIntent = new Intent(PlayerActivity.this, PlayerService.class);
                serviceIntent.putExtra(PlayerService.PLAYLIST_KEY, items);
                serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
                startService(serviceIntent);
            }
        });
        playlistView.setEmptyView(findViewById(R.id.no_results));
        playbackProgressView = findViewById(R.id.playback_progress_view);
        setupOnClickListeners();
        updateViews();
    }

    private void setupOnClickListeners()
    {
        playButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (items == null)
                {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else
                {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                    {
                        if (state == PlayerState.PAUSE)
                        {
                            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                            intent.setAction(NotificationListener.ACTION_RESUME);
                            startService(intent);
                        } else
                        {
                            Intent serviceIntent = new Intent(PlayerActivity.this, PlayerService.class);
                            serviceIntent.putExtra(PlayerService.PLAYLIST_KEY, items);
                            serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
                            startService(serviceIntent);
                        }
                    }
                }
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (items == null)
                {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else
                {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                    {
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(NotificationListener.ACTION_PAUSE);
                        startService(intent);
                    }
                }
            }
        });
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (items == null)
                {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else
                {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                    {
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(NotificationListener.ACTION_NEXT);
                        startService(intent);
                    }
                }
            }
        });
        findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                if (items == null)
                {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else
                {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted())
                    {
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(NotificationListener.ACTION_PREVIOUS);
                        startService(intent);
                    }
                }
            }
        });
    }

    private void updateViews()
    {
        setTitle(buildTitle());
        if (PreferenceManager.getInstance(this).isMusicImageEnabled())
        {
            ImageLoader.getInstance().displayImage(metadataIcon, icon, options);
        } else
        {
            icon.setImageResource(R.drawable.default_player_background);
        }
        if (state == PlayerState.PLAY)
        {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else
        {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
    }

    private String buildTitle()
    {
        PlaylistItem item = items == null ? null : items.get(position);
        String song;
        if (metadataArtist == null)
        {
            song = item == null ? getString(R.string.app_name) : (item.getTitle() + " - " + item.getSubtitle());
        } else if (metadataSong == null)
        {
            song = metadataArtist;
        } else
        {
            song = metadataArtist + " - " + metadataSong;
        }
        return song;
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
        positionUpdater = new Thread(this);
        positionUpdater.start();
    }

    @Override
    public void onServiceDisconnected()
    {
        if (positionUpdater != null)
        {
            positionUpdater.interrupt();
        }
    }

    @Override
    public void onMessage(final PlayerMessage playerMessage)
    {
        if (playerMessage.getMessageType() == PlayerMessageType.PLAYBACK_STATE)
        {
            PlaybackStatePlayerMessage playbackState = (PlaybackStatePlayerMessage) playerMessage;
            if (items == null || !items.equals(playbackState.getItems()))
            {
                adapter.clear();
                if (playbackState.getItems() != null)
                {
                    for (PlaylistItem item : playbackState.getItems())
                    {
                        adapter.add(item);
                    }
                }
            }
            this.items = playbackState.getItems();
            this.position = playbackState.getPosition();
            this.state = playbackState.getState();
            metadataIcon = playbackState.getIcon();
            metadataArtist = playbackState.getArtist();
            metadataSong = playbackState.getSong();
            updateViews();
            adapter.setPosition(position);
            adapter.notifyDataSetChanged();
        } else if (playerMessage.getMessageType() == PlayerMessageType.POSITION_STATE)
        {
            PositionStateMessage positionStateMessage = (PositionStateMessage) playerMessage;
            updateProgress(positionStateMessage.getPosition(), positionStateMessage.getDuration());
        }
    }

    private void updateProgress(final float position, final int duration)
    {
        if (duration == 0 || position / duration < 0.01)
        {
            playbackProgressView.setVisibility(View.GONE);
        } else
        {
            playbackProgressView.setVisibility(View.VISIBLE);
            ((LinearLayout.LayoutParams) playbackProgressView.getLayoutParams()).weight = position / duration * 100;
            playbackProgressView.requestLayout();
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

    @Override
    public void onBackPressed()
    {
        if (state == PlayerState.PAUSE)
        {
            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
            intent.setAction(NotificationListener.ACTION_STOP);
            startService(intent);
        }
        super.onBackPressed();
    }

    @Override
    public void run()
    {
        try
        {
            while(true)
            {
                if (state == PlayerState.PLAY && items != null && items.get(position) != null && !items.get(position).isLive())
                {
                    playerServiceHelper.getServiceClient().execute(new UpdatePositionMessage());
                }
                Thread.sleep(2000);
            }
        } catch (InterruptedException e)
        {
            //Nothing to do
        }
    }
}
