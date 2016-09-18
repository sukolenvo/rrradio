package com.dakare.radiorecord.app.settings;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.quality.Quality;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CALL_REQUEST = 1;
    private static final int WRITE_REQUEST = 2;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        preferenceManager = PreferenceManager.getInstance(SettingsActivity.this);
        toolbar.setNavigationOnClickListener(this);
        initQuality();
        initMusicMetadata();
        initMusicImage();
        initCallSettings();
        initDownloadDirectory();
        initEqSettings();
    }

    private void initQuality() {
        updateQualitySecondary();
        findViewById(R.id.quality_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SettingsQualityDialog settingsQualityDialog = new SettingsQualityDialog(SettingsActivity.this);
                settingsQualityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateQualitySecondary();
                    }
                });
                settingsQualityDialog.show();
            }
        });
    }

    private void updateQualitySecondary() {
        TextView text = (TextView) findViewById(R.id.quality_secondary);
        Quality quality = preferenceManager.getDefaultQuality(null);
        if (quality == null) {
            text.setText(R.string.no_default_quality);
        } else {
            text.setText(quality.getNameRes());
        }
    }

    private void initMusicMetadata() {
        updateMusicMetadataCheckbox();
        findViewById(R.id.music_metadata_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                preferenceManager.setMusicMedatada(preferenceManager.isMusicMetadataEnabled() ^ true);
                updateMusicMetadataCheckbox();
            }
        });
    }

    private void updateMusicMetadataCheckbox() {
        Checkable checkable = (Checkable) findViewById(R.id.music_metadata_checkbox);
        checkable.setChecked(preferenceManager.isMusicMetadataEnabled());
    }

    private void initMusicImage() {
        updateMusicImageCheckbox();
        findViewById(R.id.music_image_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                preferenceManager.setMusicImage(preferenceManager.isMusicImageEnabled() ^ true);
                updateMusicImageCheckbox();
            }
        });
    }

    private void updateMusicImageCheckbox() {
        Checkable checkable = (Checkable) findViewById(R.id.music_image_checkbox);
        checkable.setChecked(preferenceManager.isMusicImageEnabled());
    }

    private void initCallSettings() {
        updateCallCheckbox();
        findViewById(R.id.call_settings_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                boolean enable = !preferenceManager.isOnCallEnabled();
                if (enable) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, CALL_REQUEST);
                    } else {
                        preferenceManager.setOnCall(true);
                    }
                } else {
                    preferenceManager.setOnCall(false);
                }
                updateCallCheckbox();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case CALL_REQUEST:
                    preferenceManager.setOnCall(true);
                    updateCallCheckbox();
                    break;
                case WRITE_REQUEST:
                    findViewById(R.id.download_container).performClick();
                    break;
                default:
                    Log.e("SettingActivity", "unknown request permission code " + requestCode);
            }
        }
    }

    private void updateCallCheckbox() {
        Checkable checkable = (Checkable) findViewById(R.id.call_settings_checkbox);
        checkable.setChecked(preferenceManager.isOnCallEnabled());
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    private void initDownloadDirectory() {
        updateDownloadSecondary();
        findViewById(R.id.download_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST);
                        return;
                    }
                }
                SettingsDirectoryDialog settingsQualityDialog = new SettingsDirectoryDialog(SettingsActivity.this);
                settingsQualityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        updateDownloadSecondary();
                    }
                });
                settingsQualityDialog.show();
            }
        });
    }

    private void updateDownloadSecondary() {
        TextView text = (TextView) findViewById(R.id.download_secondary);
        text.setText(preferenceManager.getDownloadDirectory());
    }

    private void initEqSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(R.id.eq_settings_container).setVisibility(View.GONE);
        } else {
            updateEqCheckbox();
            findViewById(R.id.eq_settings_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    preferenceManager.setEqSettings(preferenceManager.isEqSettingsEnabled() ^ true);
                    Toast.makeText(SettingsActivity.this, R.string.eq_change_message, Toast.LENGTH_SHORT).show();
                    updateEqCheckbox();
                }
            });
        }
    }

    private void updateEqCheckbox() {
        Checkable checkable = (Checkable) findViewById(R.id.eq_settings_checkbox);
        checkable.setChecked(preferenceManager.isEqSettingsEnabled());
    }
}
