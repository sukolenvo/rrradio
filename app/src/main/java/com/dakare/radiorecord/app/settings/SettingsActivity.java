package com.dakare.radiorecord.app.settings;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.utils.EqUtils;
import com.dakare.radiorecord.app.view.theme.Theme;
import com.dakare.radiorecord.app.view.theme.ThemeActivity;

public class SettingsActivity extends ThemeActivity implements View.OnClickListener {
    private static final int WRITE_REQUEST = 2;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        preferenceManager = PreferenceManager.getInstance(SettingsActivity.this);
        toolbar.setNavigationOnClickListener(this);
        initQuality();
        initTheme();
        initMusicMetadata();
        initMusicImage();
        initAutoPause();
        initBackgroundLoad();
        initDownloadDirectory();
        initEqSettings();
        initLargeButtons();
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
        TextView text = findViewById(R.id.quality_secondary);
        Quality quality = preferenceManager.getDefaultQuality(null);
        if (quality == null) {
            text.setText(R.string.no_default_quality);
        } else {
            text.setText(quality.getNameRes());
        }
    }

    private void initTheme() {
        updateThemeSecondary();
        findViewById(R.id.theme_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new SettingsThemeDialog(SettingsActivity.this).show();
            }
        });
    }

    private void updateThemeSecondary() {
        TextView text = findViewById(R.id.theme_secondary);
        Theme theme = preferenceManager.getTheme();
        text.setText(theme.getNameRes());
    }

    private void initMusicMetadata() {
        updateMusicMetadataCheckbox();
        findViewById(R.id.music_metadata_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                preferenceManager.setMusicMedatada(!preferenceManager.isMusicMetadataEnabled());
                updateMusicMetadataCheckbox();
            }
        });
    }

    private void updateMusicMetadataCheckbox() {
        Checkable checkable = findViewById(R.id.music_metadata_checkbox);
        checkable.setChecked(preferenceManager.isMusicMetadataEnabled());
    }

    private void initBackgroundLoad() {
        updateBackgroundLoad();
        findViewById(R.id.save_battery_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                preferenceManager.setBackgroundLoad(!preferenceManager.isBackgroundLoad());
                updateBackgroundLoad();
            }
        });
    }

    private void updateBackgroundLoad() {
        Checkable checkable = findViewById(R.id.save_battery_checkbox);
        checkable.setChecked(!preferenceManager.isBackgroundLoad());
    }

    private void initMusicImage() {
        updateMusicImageCheckbox();
        findViewById(R.id.music_image_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                preferenceManager.setMusicImage(!preferenceManager.isMusicImageEnabled());
                updateMusicImageCheckbox();
            }
        });
    }

    private void updateMusicImageCheckbox() {
        Checkable checkable = findViewById(R.id.music_image_checkbox);
        checkable.setChecked(preferenceManager.isMusicImageEnabled());
    }

    private void initAutoPause() {
        updateAutoPause();
        findViewById(R.id.auto_pause_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                preferenceManager.setAutoPause(!preferenceManager.isAutoPause());
                updateAutoPause();
            }
        });
    }

    private void updateAutoPause() {
        Checkable checkable = findViewById(R.id.auto_pause_checkbox);
        checkable.setChecked(preferenceManager.isAutoPause());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case WRITE_REQUEST:
                    findViewById(R.id.download_container).performClick();
                    break;
                default:
                    Log.e("SettingActivity", "unknown request permission code " + requestCode);
            }
        }
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
                if (ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST);
                } else {
                    SettingsDirectoryDialog settingsQualityDialog = new SettingsDirectoryDialog(SettingsActivity.this);
                    settingsQualityDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            updateDownloadSecondary();
                        }
                    });
                    settingsQualityDialog.show();
                }
            }
        });
    }

    private void updateDownloadSecondary() {
        TextView text = findViewById(R.id.download_secondary);
        text.setText(preferenceManager.getDownloadDirectory());
    }

    private void initEqSettings() {
        updateEqCheckbox();
        findViewById(R.id.eq_settings_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (EqUtils.isEqAvailable()) {
                    preferenceManager.setEqSettings(!preferenceManager.isEqSettingsEnabled());
                    Toast.makeText(SettingsActivity.this, R.string.eq_change_message, Toast.LENGTH_SHORT).show();
                    updateEqCheckbox();
                } else {
                    Toast.makeText(SettingsActivity.this, R.string.eq_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateEqCheckbox() {
        Checkable checkable = findViewById(R.id.eq_settings_checkbox);
        checkable.setChecked(preferenceManager.isEqSettingsEnabled());
    }

    private void initLargeButtons() {
        updateLargeButtons();
        findViewById(R.id.large_buttons_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                preferenceManager.setLargeButtons(!preferenceManager.isLargeButtons());
                updateLargeButtons();
            }
        });
    }

    private void updateLargeButtons() {
        Checkable checkable = findViewById(R.id.large_buttons_checkbox);
        checkable.setChecked(preferenceManager.isLargeButtons());
    }

    @Override
    protected int getThemeId(final Theme theme) {
        return theme == Theme.DARK ? R.style.MainDark_Settings : R.style.Main_Settings;
    }
}
