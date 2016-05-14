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
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.quality.Quality;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener
{

    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
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
    }

    private void initQuality()
    {
        updateQualitySecondary();
        findViewById(R.id.quality_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                SettingsQualityDialog settingsQualityDialog = new SettingsQualityDialog(SettingsActivity.this);
                settingsQualityDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        updateQualitySecondary();
                    }
                });
                settingsQualityDialog.show();
            }
        });
    }

    private void updateQualitySecondary()
    {
        TextView text = (TextView) findViewById(R.id.quality_secondary);
        Quality quality = preferenceManager.getDefaultQuality();
        if (quality == null)
        {
            text.setText(R.string.no_default_quality);
        } else
        {
            text.setText(quality.getNameRes());
        }
    }

    private void initMusicMetadata()
    {
        updateMusicMetadataCheckbox();
        findViewById(R.id.music_metadata_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                preferenceManager.setMusicMedatada(preferenceManager.isMusicMetadataEnabled() ^ true);
                updateMusicMetadataCheckbox();
            }
        });
    }

    private void updateMusicMetadataCheckbox()
    {
        Checkable checkable = (Checkable) findViewById(R.id.music_metadata_checkbox);
        checkable.setChecked(preferenceManager.isMusicMetadataEnabled());
    }

    private void initMusicImage()
    {
        updateMusicImageCheckbox();
        findViewById(R.id.music_image_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                preferenceManager.setMusicImage(preferenceManager.isMusicImageEnabled() ^ true);
                updateMusicImageCheckbox();
            }
        });
    }

    private void updateMusicImageCheckbox()
    {
        Checkable checkable = (Checkable) findViewById(R.id.music_image_checkbox);
        checkable.setChecked(preferenceManager.isMusicImageEnabled());
    }

    private void initCallSettings()
    {
        updateCallCheckbox();
        findViewById(R.id.call_settings_container).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                boolean enable = !preferenceManager.isOnCallEnabled();
                if (enable)
                {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    } else
                    {
                        preferenceManager.setOnCall(true);
                    }
                } else
                {
                    preferenceManager.setOnCall(false);
                }
                updateCallCheckbox();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            preferenceManager.setOnCall(true);
            updateCallCheckbox();
        }
    }

    private void updateCallCheckbox()
    {
        Checkable checkable = (Checkable) findViewById(R.id.call_settings_checkbox);
        checkable.setChecked(preferenceManager.isOnCallEnabled());
    }

    @Override
    public void onClick(View v)
    {
        onBackPressed();
    }
}
