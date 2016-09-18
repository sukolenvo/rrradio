package com.dakare.radiorecord.app.player;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import com.dakare.radiorecord.app.view.EqualizerImage;
import com.dakare.radiorecord.app.view.PlayerBackgroundImage;
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

public class PlayerActivity extends MenuActivity
        implements PlayerServiceHelper.ServiceBindListener, PlayerServiceClient.PlayerMessageHandler,
        Runnable, SeekBar.OnSeekBarChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
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
    private SeekBar playbackProgressView;
    private View progressContainer;
    private TextView positionView;
    private TextView durationView;
    private View eqButton;
    private EqualizerImage equalizerImage;
    private View eqRefreshView;
    private boolean isEqView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initToolbar();
        if (savedInstanceState != null) {
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
        playlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                Intent serviceIntent = new Intent(PlayerActivity.this, PlayerService.class);
                serviceIntent.putExtra(PlayerService.PLAYLIST_KEY, items);
                serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
                startService(serviceIntent);
            }
        });
        playlistView.setEmptyView(findViewById(R.id.no_results));
        playbackProgressView = (SeekBar) findViewById(R.id.playback_progress_view);
        playbackProgressView.setOnSeekBarChangeListener(this);
        progressContainer = findViewById(R.id.playback_progress);
        positionView = (TextView) findViewById(R.id.position);
        durationView = (TextView) findViewById(R.id.duration);
        eqButton = findViewById(R.id.equalizer_button);
        eqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (PreferenceManager.getInstance(PlayerActivity.this).isEqSettingsEnabled()) {
                    isEqView ^= true;
                    updateIcon();
                } else {
                    new EqDisabledWarningDialog(PlayerActivity.this).show();
                }
            }
        });
        equalizerImage = (EqualizerImage) findViewById(R.id.equalizer_image);
        eqRefreshView = findViewById(R.id.equalizer_refresh);
        eqRefreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equalizerImage.refreshEq();
            }
        });
        setupOnClickListeners();
        updateViews();
        PreferenceManager.getInstance(this).registerChangeListener(this);
        updateEqViews();
        updateIcon();
    }

    private void updateIcon() {
        if (isEqView) {
            icon.setVisibility(View.GONE);
            findViewById(R.id.player_control_container).setVisibility(View.GONE);
            equalizerImage.setVisibility(View.VISIBLE);
            eqRefreshView.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.VISIBLE);
            findViewById(R.id.player_control_container).setVisibility(View.VISIBLE);
            equalizerImage.setVisibility(View.GONE);
            eqRefreshView.setVisibility(View.GONE);
        }
    }

    private void setupOnClickListeners() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items == null || items.isEmpty()) {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                        if (state == PlayerState.PAUSE) {
                            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                            intent.setAction(NotificationListener.ACTION_RESUME);
                            startService(intent);
                        } else {
                            Intent serviceIntent = new Intent(PlayerActivity.this, PlayerService.class);
                            serviceIntent.putExtra(PlayerService.PLAYLIST_KEY, items);
                            serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
                            startService(serviceIntent);
                        }
                    }
                }
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items == null) {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(NotificationListener.ACTION_PAUSE);
                        startService(intent);
                    }
                }
            }
        });
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (items == null) {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(NotificationListener.ACTION_NEXT);
                        startService(intent);
                    }
                }
            }
        });
        findViewById(R.id.prev_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (items == null) {
                    Toast.makeText(PlayerActivity.this, R.string.no_results, Toast.LENGTH_LONG).show();
                } else {
                    if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(NotificationListener.ACTION_PREVIOUS);
                        startService(intent);
                    }
                }
            }
        });
    }

    private void updateViews() {
        setTitle(buildTitle());
        if (PreferenceManager.getInstance(this).isMusicImageEnabled()) {
            ImageLoader.getInstance().displayImage(metadataIcon, icon, options);
        } else {
            icon.setImageResource(R.drawable.default_player_background);
        }
        if (state == PlayerState.PLAY) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
    }

    private String buildTitle() {
        PlaylistItem item = items == null || items.isEmpty() ? null : items.get(position);
        String song;
        if (metadataArtist == null) {
            song = item == null ? getString(R.string.app_name) : (item.getTitle() + " - " + item.getSubtitle());
        } else if (metadataSong == null) {
            song = metadataArtist;
        } else {
            song = metadataArtist + " - " + metadataSong;
        }
        return song;
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(this);
        playerServiceHelper.bindService(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(null);
        playerServiceHelper.unbindService(this);
    }

    @Override
    public void onServiceConnected() {
        playerServiceHelper.getServiceClient().execute(new UpdateStatePlayerMessage());
        positionUpdater = new Thread(this);
        positionUpdater.start();
    }

    @Override
    public void onServiceDisconnected() {
        if (positionUpdater != null) {
            positionUpdater.interrupt();
        }
    }

    @Override
    public void onMessage(final PlayerMessage playerMessage) {
        if (playerMessage.getMessageType() == PlayerMessageType.PLAYBACK_STATE) {
            PlaybackStatePlayerMessage playbackState = (PlaybackStatePlayerMessage) playerMessage;
            if (items == null || !items.equals(playbackState.getItems())) {
                adapter.clear();
                if (playbackState.getItems() != null) {
                    for (PlaylistItem item : playbackState.getItems()) {
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
        } else if (playerMessage.getMessageType() == PlayerMessageType.POSITION_STATE) {
            PositionStateMessage positionStateMessage = (PositionStateMessage) playerMessage;
            updateProgress(positionStateMessage.getPosition(), positionStateMessage.getDuration(), positionStateMessage.getBuffered());
        }
    }

    private void updateProgress(final int position, final int duration, final int buffered) {
        if (duration <= 0) {
            progressContainer.setVisibility(View.GONE);
        } else {
            progressContainer.setVisibility(View.VISIBLE);
            playbackProgressView.setProgress((int) (position * 100. / duration));
            positionView.setText(msToString(position));
            durationView.setText(msToString(duration));
            if (buffered > 0) {
                playbackProgressView.setSecondaryProgress(buffered * 100 / duration);
            } else {
                playbackProgressView.setSecondaryProgress(0);
            }
        }
    }

    private String msToString(final int time) {
        if (time > 3_600_000) {
            return String.format("%d:%02d:%02d", time / 3_600_000, time / 60_000 % 60, time / 1000 % 60);
        }
        return String.format("%d:%02d", time / 60_000 % 60, time / 1000 % 60);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(METADATA_ICON_KEY, metadataIcon);
        outState.putString(METADATA_ARTIST_KEY, metadataArtist);
        outState.putString(METADATA_SONG_KEY, metadataSong);
    }

    @Override
    public void onBackPressed() {
        if (state == PlayerState.PAUSE) {
            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
            intent.setAction(NotificationListener.ACTION_STOP);
            startService(intent);
        }
        super.onBackPressed();
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (state == PlayerState.PLAY && items != null && items.get(position) != null && !items.get(position).isLive()) {
                    playerServiceHelper.getServiceClient().execute(new UpdatePositionMessage());
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            //Nothing to do
        }
    }

    @Override
    protected int getMenuContainer() {
        return R.id.menu_player_container;
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
        if (fromUser) {
            playerServiceHelper.getServiceClient().execute(new SeekToMessage(progress / 100f));
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        //Nothing to do
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        //Nothing to do
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy_to_clipboard:
                try {
                    ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(getString(R.string.clipboard_label), buildTitle());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(this, R.string.clipboard_success, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(this, R.string.clipboeard_error, Toast.LENGTH_LONG).show();
                }
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        adapter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (PreferenceManager.EQ_ENABLED_KEY.equals(key)) {
            updateEqViews();
        }
    }

    private void updateEqViews() {
        EqualizerSettings equalizerSettings = PreferenceManager.getInstance(this).getEqSettings();
        if (equalizerSettings.isEnabled()) {
            eqButton.setVisibility(View.VISIBLE);
            equalizerImage.updateSettings(equalizerSettings);
        } else {
            eqButton.setVisibility(View.GONE);
            isEqView = false;
            updateIcon();
        }
    }
}
