package com.dakare.radiorecord.app.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerServiceClient;
import com.dakare.radiorecord.app.player.service.PlayerServiceHelper;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessageType;
import com.dakare.radiorecord.app.player.service.message.UpdateStatePlayerMessage;
import com.dakare.radiorecord.app.view.PlayerBackgroundImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class PlaylistItemFragment extends Fragment implements PlayerServiceClient.PlayerMessageHandler, PlayerServiceHelper.ServiceBindListener {

    public static final String PLAYLIST_ITEM = "playlist_item_key";
    public static final String POSITION = "playlist_item_position_key";

    private final PlayerServiceHelper playerServiceHelper = new PlayerServiceHelper();

    private TextView artist;
    private TextView song;
    private PlayerBackgroundImage icon;
    private String metadataArtist;
    private String metadataSong;
    private String metadataIcon;
    private PlaylistItem playlistItem;
    private int position;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_item, container, false);
        playlistItem = getArguments().getParcelable(PLAYLIST_ITEM);
        position = getArguments().getInt(POSITION);
        artist = (TextView) view.findViewById(R.id.artist);
        song = (TextView) view.findViewById(R.id.song);
        icon = (PlayerBackgroundImage) view.findViewById(R.id.player_icon);
        preferenceManager = PreferenceManager.getInstance(RecordApplication.getInstance());
        updateViews();
        return view;
    }

    private void updateViews() {
        if (getContext() != null) {
            if (metadataArtist != null) {
                artist.setText(metadataArtist);
                song.setText(metadataSong);
            } else if (playlistItem == null) {
                artist.setText("");
                song.setText("");
            } else {
                artist.setText(playlistItem.getTitle());
                song.setText(playlistItem.getSubtitle());
            }
            if (PreferenceManager.getInstance(getContext()).isMusicImageEnabled()) {
                if (!TextUtils.isEmpty(metadataIcon) && metadataIcon.trim().length() > 0) {
                    RequestCreator requestCreator = Picasso.with(getContext()).load(metadataIcon)
                            .error(R.drawable.default_player_background)
                            .placeholder(R.drawable.default_player_background);
                    if (icon.getInnerWidth() > 0 && icon.getInnerHeight() > 0) {
                        requestCreator.resize((int) icon.getInnerWidth(), (int) icon.getInnerHeight());
                        requestCreator.centerInside();
                    }
                    requestCreator.into(icon);
                }
            } else {
                icon.setImageResource(R.drawable.default_player_background);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(this);
        playerServiceHelper.bindService(getContext(), this);
    }

    @Override
    public void onPause() {
        super.onPause();
        playerServiceHelper.getServiceClient().setPlayerMessageHandler(null);
        try {
            playerServiceHelper.unbindService(getContext());
        } catch (IllegalArgumentException e) {
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onMessage(PlayerMessage playerMessage) {
        if (playerMessage.getMessageType() == PlayerMessageType.PLAYBACK_STATE) {
            PlaybackStatePlayerMessage playbackState = (PlaybackStatePlayerMessage) playerMessage;
            if (playlistItem.equals(playbackState.getPlaying())) {
                metadataIcon = playbackState.getIcon();
                metadataArtist = playbackState.getArtist();
                metadataSong = playbackState.getSong();
                updateViews();
            } else {
                metadataIcon = null;
                metadataArtist = null;
                metadataSong = null;
                updateViews();
            }
        }
    }

    @Override
    public void onServiceConnected() {
        if (preferenceManager.getLastPosition() == position) {
            playerServiceHelper.getServiceClient().execute(new UpdateStatePlayerMessage());
        }
    }

    @Override
    public void onServiceDisconnected() {

    }
}
