package com.dakare.radiorecord.app.player;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.dakare.radiorecord.app.AbstractDialog;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.settings.SettingsActivity;

public class EqDisabledWarningDialog extends AbstractDialog {

    public EqDisabledWarningDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_eq_disabled);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SettingsActivity.class));
                dismiss();
            }
        });
    }
}
