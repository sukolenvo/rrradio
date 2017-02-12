package com.dakare.radiorecord.app.player.service.playback;

import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerServiceMessageHandler;
import com.dakare.radiorecord.app.player.service.PlayerState;

import java.util.ArrayList;

public interface Player {
    void setPlayerServiceMessageHandler(PlayerServiceMessageHandler playerServiceMessageHandler);

    void play(ArrayList<PlaylistItem> playlist, int position);

    void next();

    void previous();

    void stop();

    void pause();

    void resume();

    void updateState();

    void updatePosition();

    void seekTo(float position);

    PlayerState getState();

    void record();
}
