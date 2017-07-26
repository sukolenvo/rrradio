package com.dakare.radiorecord.app.settings;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.utils.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.database.DownloadAudioTable;

import java.io.File;

public class SettingsDirectoryDialog extends AbstractDialog implements TextWatcher {

    private final String sdCardDirectory;
    private final Button buttonInternal;
    private final Button buttonExternal;
    private final TextView editText;

    public SettingsDirectoryDialog(final Context context) {
        super(context);
        sdCardDirectory = getStorageDirectory(context);
        setContentView(R.layout.dialog_settings_directory);
        editText = (TextView) findViewById(R.id.saved_directory_input);
        editText.setText(PreferenceManager.getInstance(context).getDownloadDirectory());
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        buttonInternal = (Button) findViewById(R.id.button_internal);
        buttonInternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(new File(Environment.getExternalStorageDirectory(), DownloadAudioTable.DEFAULT_DIRECTORY_NAME).toString());
            }
        });
        buttonExternal = (Button) findViewById(R.id.button_external);
        buttonExternal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(sdCardDirectory)) {
                    Toast.makeText(context, "Failed to find SD-card. If you think this is error - please enter path manually.", Toast.LENGTH_LONG).show();
                } else {
                    editText.setText(new File(new File(sdCardDirectory), DownloadAudioTable.DEFAULT_DIRECTORY_NAME).toString());
                }
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File newPath = new File(editText.getText().toString());
                if ((newPath.exists() || newPath.mkdirs()) && newPath.canWrite()) {
                    PreferenceManager.getInstance(context).setDownloadDirectory(newPath.getAbsolutePath());
                    dismiss();
                } else {
                    Toast.makeText(context, context.getString(R.string.directory_access_error), Toast.LENGTH_LONG).show();
                }
            }
        });
        editText.addTextChangedListener(this);
        updateButtons();
    }

    private String getStorageDirectory(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            File[] externalCacheDirs = context.getExternalMediaDirs();
            for (File file : externalCacheDirs) {
                if (file != null && file.exists() && Environment.isExternalStorageRemovable(file)) {
                    return file.getPath();
                }
            }
        }
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        if(!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(":");
            if (rawSecondaryStorages.length != 0) {
                return rawSecondaryStorages[0];
            }
        }
        return null;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(final Editable s) {
        updateButtons();
    }

    private void updateButtons() {
        CharSequence text = editText.getText();
        if (TextUtils.isEmpty(text)) {
            buttonExternal.setSelected(false);
            buttonInternal.setSelected(false);
        } else if (text.toString().startsWith(Environment.getExternalStorageDirectory().toString())) {
            buttonInternal.setSelected(true);
            buttonExternal.setSelected(false);
        } else if (!TextUtils.isEmpty(sdCardDirectory) && text.toString().startsWith(sdCardDirectory)) {
            buttonInternal.setSelected(false);
            buttonExternal.setSelected(true);
        }
    }
}
