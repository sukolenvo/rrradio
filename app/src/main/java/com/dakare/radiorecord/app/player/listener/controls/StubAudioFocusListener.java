package com.dakare.radiorecord.app.player.listener.controls;

import android.media.AudioManager;

public class StubAudioFocusListener implements AudioManager.OnAudioFocusChangeListener {

    private static final StubAudioFocusListener INSTANCE = new StubAudioFocusListener();

    private StubAudioFocusListener() {

    }

    public static StubAudioFocusListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void onAudioFocusChange(int i) {

    }

}
