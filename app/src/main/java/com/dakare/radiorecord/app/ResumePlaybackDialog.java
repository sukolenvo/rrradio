package com.dakare.radiorecord.app;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.utils.AbstractDialog;

public class ResumePlaybackDialog extends AbstractDialog {

    public ResumePlaybackDialog(final Context context, final String name) {
        super(context);
        setContentView(R.layout.dialog_resume_playback);
        ((TextView) findViewById(R.id.body)).setText(context.getString(R.string.resume_playback_dialog_body, name));
        findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerService.class);
                intent.setAction(NotificationListener.ACTION_RESUME);
                context.startService(intent);
                Intent playerActivity = new Intent(context, PlayerActivity.class);
                playerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(playerActivity);
                dismiss();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
