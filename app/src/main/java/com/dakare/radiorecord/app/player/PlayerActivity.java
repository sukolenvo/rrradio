package com.dakare.radiorecord.app.player;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.dakare.radiorecord.app.download.service.FileService;
import com.dakare.radiorecord.app.player.equalizer.EqDisabledWarningDialog;
import com.dakare.radiorecord.app.player.equalizer.EqualizerDialogFragment;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.playlist.PlaylistDialogFragment;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerServiceClient;
import com.dakare.radiorecord.app.player.service.PlayerServiceHelper;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.*;
import com.dakare.radiorecord.app.player.sleep_mode.SleepMode;
import com.dakare.radiorecord.app.player.sleep_mode.SleepTimerSetupDialog;
import com.dakare.radiorecord.app.view.theme.Theme;

public class PlayerActivity extends MenuActivity
        implements PlayerServiceHelper.ServiceBindListener, PlayerServiceClient.PlayerMessageHandler,
        Runnable, SeekBar.OnSeekBarChangeListener, DialogInterface.OnDismissListener {
    private static final String METADATA_ICON_KEY = "player_metadata_icon";
    private static final String METADATA_ARTIST_KEY = "player_metadata_artist";
    private static final String METADATA_SONG_KEY = "player_metadata_song";
    private static final String RECORDING_STATE_KEY = "recording";

    private final PlayerServiceHelper playerServiceHelper = new PlayerServiceHelper();
    private PlayerState state;
    private View playButton;
    private View pauseButton;
    private String metadataIcon;
    private String metadataArtist;
    private String metadataSong;
    private Thread positionUpdater;
    private SeekBar playbackProgressView;
    private TextView positionView;
    private TextView durationView;
    private PlaylistItem playlistItem;
    private View sleepTimerButton;
    private View recordButton;
    private boolean recording;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initToolbar();
        if (savedInstanceState != null) {
            metadataIcon = savedInstanceState.getString(METADATA_ICON_KEY);
            metadataArtist = savedInstanceState.getString(METADATA_ARTIST_KEY);
            metadataSong = savedInstanceState.getString(METADATA_SONG_KEY);
            recording = savedInstanceState.getBoolean(RECORDING_STATE_KEY);
        }
        playbackProgressView = findViewById(R.id.playback_progress_view);
        playbackProgressView.setOnSeekBarChangeListener(this);
        positionView = findViewById(R.id.position);
        durationView = findViewById(R.id.duration);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PlaylistPagerAdapter(getSupportFragmentManager(), viewPager));
        viewPager.setCurrentItem(PreferenceManager.getInstance(RecordApplication.getInstance()).getLastPosition());
        setupOnClickListeners();
        updateViews();
        updateProgress(0, 0, 0);
    }

    private void setupOnClickListeners() {
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                    Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                    intent.setAction(NotificationListener.ACTION_RESUME);
                    startService(intent);
                }
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                    Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                    intent.setAction(NotificationListener.ACTION_PAUSE);
                    startService(intent);
                }
            }
        });
        View equalizerButton = findViewById(R.id.equalizer_button);
        equalizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (PreferenceManager.getInstance(PlayerActivity.this).isEqSettingsEnabled()
                        && PreferenceManager.getInstance(PlayerActivity.this).getEqSettings().getBands() != null) {
                    new EqualizerDialogFragment().show(getSupportFragmentManager(), "equalizer_dialog");
                } else {
                    new EqDisabledWarningDialog(PlayerActivity.this).show();
                }
            }
        });
        View nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                    Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                    intent.setAction(NotificationListener.ACTION_NEXT);
                    startService(intent);
                }
            }
        });
        View previousButton = findViewById(R.id.prev_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                    Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                    intent.setAction(NotificationListener.ACTION_PREVIOUS);
                    startService(intent);
                }
            }
        });
        View clipboardButton = findViewById(R.id.clipboard_button);
        clipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    String line = "";
                    if (metadataArtist != null) {
                        line = metadataArtist + " - " + metadataSong;
                    } else if (playlistItem != null) {
                        line = playlistItem.getTitle() + " - " + playlistItem.getSubtitle();
                    }
                    ClipData clip = ClipData.newPlainText(getString(R.string.clipboard_label), line);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(PlayerActivity.this, R.string.clipboard_success, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(PlayerActivity.this, R.string.clipboeard_error, Toast.LENGTH_LONG).show();
                }
            }
        });
        sleepTimerButton = findViewById(R.id.sleep_timer_button);
        sleepTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SleepTimerSetupDialog sleepTimerSetupDialog = new SleepTimerSetupDialog(PlayerActivity.this);
                sleepTimerSetupDialog.show();
                sleepTimerSetupDialog.setOnDismissListener(PlayerActivity.this);
            }
        });
        View downloadButton = findViewById(R.id.download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playlistItem == null || !playlistItem.isDownloadable()) {
                    Toast.makeText(PlayerActivity.this, R.string.undownloadable_audio, Toast.LENGTH_LONG).show();
                } else if (ActivityCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PlayerActivity.this, R.string.permission_guide, Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(PlayerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    StorageContract.getInstance().insertDownloadAudio(playlistItem);
                    startService(new Intent(PlayerActivity.this, FileService.class));
                    Toast.makeText(PlayerActivity.this, R.string.download_starting, Toast.LENGTH_LONG).show();
                }
            }
        });
        View playlistButton = findViewById(R.id.playlist_button);
        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlaylistDialogFragment().show(getSupportFragmentManager(), "playlist_fragment");
            }
        });
        recordButton = findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (playlistItem == null || !playlistItem.isLive()) {
                    Toast.makeText(PlayerActivity.this, R.string.record_info_text, Toast.LENGTH_LONG).show();
                } else if (ActivityCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (recording) {
                        Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
                        intent.setAction(NotificationListener.ACTION_STOP);
                        startService(intent);
                    } else {
                        playerServiceHelper.getServiceClient().execute(new RecordPlayerMessage());
                    }
                } else {
                    ActivityCompat.requestPermissions(PlayerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
        });
        if (PreferenceManager.getInstance(this).isLargeButtons()) {
            playButton.getLayoutParams().height *= 1.5;
            pauseButton.getLayoutParams().height *= 1.5;
            nextButton.getLayoutParams().height *= 1.5;
            previousButton.getLayoutParams().height *= 1.5;
            playlistButton.getLayoutParams().height *= 1.5;
            equalizerButton.getLayoutParams().height *= 1.5;
            clipboardButton.getLayoutParams().height *= 2;
            clipboardButton.getLayoutParams().width *= 2;
            downloadButton.getLayoutParams().height *= 2;
            downloadButton.getLayoutParams().width *= 2;
            sleepTimerButton.getLayoutParams().height *= 2;
            sleepTimerButton.getLayoutParams().width *= 2;
            recordButton.getLayoutParams().height *= 2;
            recordButton.getLayoutParams().width *= 2;
        }
    }

    private void updateViews() {
        setTitle(buildTitle());
        if (state == PlayerState.PLAY) {
            playButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
        }
        recordButton.setSelected(recording);
    }

    private String buildTitle() {
        return playlistItem == null ? getString(R.string.app_name) : playlistItem.getStation().getName();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(this);
        playerServiceHelper.bindService(this, this);
        if (positionUpdater != null) {
            positionUpdater.interrupt();
        }
        positionUpdater = new Thread(this);
        positionUpdater.start();
        setupSleepTimerButton();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(null);
        try {
            playerServiceHelper.unbindService(this);
        } catch (IllegalArgumentException e) {
            Log.e("PlayerActivity", "failed to unbind", e);
        }
        if (positionUpdater != null) {
            positionUpdater.interrupt();
            positionUpdater = null;
        }
    }

    @Override
    public void onServiceConnected() {
        playerServiceHelper.getServiceClient().execute(new UpdateStatePlayerMessage());
    }

    @Override
    public void onServiceDisconnected() {
    }

    @Override
    public void onMessage(final PlayerMessage playerMessage) {
        if (playerMessage.getMessageType() == PlayerMessageType.PLAYBACK_STATE) {
            PlaybackStatePlayerMessage playbackState = (PlaybackStatePlayerMessage) playerMessage;
            playlistItem = playbackState.getPlaying();
            this.state = playbackState.getState();
            metadataIcon = playbackState.getIcon();
            metadataArtist = playbackState.getArtist();
            metadataSong = playbackState.getSong();
            recording = playbackState.isRecord();
            updateViews();
        } else if (playerMessage.getMessageType() == PlayerMessageType.POSITION_STATE) {
            PositionStateMessage positionStateMessage = (PositionStateMessage) playerMessage;
            updateProgress(positionStateMessage.getPosition(), positionStateMessage.getDuration(), positionStateMessage.getBuffered());
        }
    }

    private void updateProgress(final int position, final int duration, final int buffered) {
        if (duration == 0) {
            positionView.setText(msToString(0));
            durationView.setText(msToString(0));
            playbackProgressView.setProgress(0);
        } else {
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
        outState.putBoolean(RECORDING_STATE_KEY, recording);
    }

    @Override
    public void onBackPressed() {
        if (state == PlayerState.PAUSE) {
            Intent intent = new Intent(PlayerActivity.this, PlayerService.class);
            intent.setAction(NotificationListener.ACTION_STOP);
            startService(intent);
        }
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            Log.i("PlayerActivity", "Ignoring state loss", e);
        }
    }

    @Override
    public void run() {
        String old = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("UpdateDurationThread");
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
                if (state == PlayerState.PLAY && playlistItem != null
                        && playerServiceHelper.getServiceClient().isMessagingSessionStarted()) {
                    playerServiceHelper.getServiceClient().execute(new UpdatePositionMessage());
                }
            }
        } catch (InterruptedException e) {
            //Nothing to do
        } finally {
            Thread.currentThread().setName(old);
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
    public void onDismiss(final DialogInterface dialog) {
        setupSleepTimerButton();
    }

    private void setupSleepTimerButton() {
        sleepTimerButton.setSelected(PreferenceManager.getInstance(this).getSleepMode() != SleepMode.OFF);
    }

    @Override
    protected int getThemeId(final Theme theme) {
        return theme == Theme.DARK ? R.style.MainDark_Player : R.style.Main_Player;
    }
}
