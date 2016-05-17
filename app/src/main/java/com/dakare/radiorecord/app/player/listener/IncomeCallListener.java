package com.dakare.radiorecord.app.player.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.player.service.Player;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

public class IncomeCallListener extends AbstractPlayerStateListener
{

    private int lastState = -1;
    private final PhoneStateListener callback = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(final int state, final String incomingNumber)
        {
            if (lastState == state)
            {
                return;
            }
            lastState = state;
            if (state == TelephonyManager.CALL_STATE_RINGING && PreferenceManager.getInstance(context).isOnCallEnabled())
            {
                player.stop();
            }
        }
    };
    private final Player player;
    private final TelephonyManager manager;
    private final Context context;

    public IncomeCallListener(final Context context, final Player player)
    {
        this.context = context;
        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.player = player;
    }

    @Override
    protected void onPlaybackChange(final PlaybackStatePlayerMessage message)
    {
        if (message.getState() == PlayerState.PLAY)
        {
            manager.listen(callback, PhoneStateListener.LISTEN_CALL_STATE);
        } else
        {
            manager.listen(callback, PhoneStateListener.LISTEN_NONE);
            lastState = -1;
        }
    }
}
