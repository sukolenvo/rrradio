package com.dakare.radiorecord.app.settings;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Checkable;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;

import java.io.File;

public class SettingsDirectoryDialog extends Dialog {

    public SettingsDirectoryDialog(final Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings_directory);
        final TextView editText = (TextView) findViewById(R.id.saved_directory_input);
        editText.setText(PreferenceManager.getInstance(context).getDownloadDirectory());
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File newPath = new File(editText.getText().toString());
                if (!newPath.exists()) {
                    newPath.mkdirs();
                }
                if (!newPath.canWrite()) {
                    Toast.makeText(context, context.getString(R.string.directory_access_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (((Checkable) findViewById(R.id.checkbox_container)).isChecked()) {
                    findViewById(R.id.status_text).setVisibility(View.VISIBLE);
                    //TODO: init audio move
                    return;
                }
                PreferenceManager.getInstance(context).setDownloadDirectory(newPath.getAbsolutePath());
                dismiss();
            }
        });
    }
}
