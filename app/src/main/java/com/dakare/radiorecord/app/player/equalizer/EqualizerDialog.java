package com.dakare.radiorecord.app.player.equalizer;

import android.content.Context;
import android.media.audiofx.Equalizer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.dakare.radiorecord.app.utils.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.equalizer.preset.EqualizerPresetDialog;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import com.dakare.radiorecord.app.view.EqualizerImage;

public class EqualizerDialog extends AbstractDialog implements EqualizerPresetDialog.PresetSelectListener {

    private final Equalizer equalizer;
    private final EqualizerImage equalizerImage;
    private final Button presetButton;

    public EqualizerDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_equalizer);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        equalizer = new Equalizer(0, -109);
        presetButton = (Button) findViewById(R.id.presets_button);
        presetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EqualizerPresetDialog(context, equalizer, EqualizerDialog.this).show();
            }
        });
        equalizerImage = (EqualizerImage) findViewById(R.id.equalizer_image);
        EqualizerSettings eqSettings = PreferenceManager.getInstance(context).getEqSettings();
        presetButton.setText(eqSettings.getPreset() == null ? context.getString(R.string.no_preset) : eqSettings.getPreset());
        equalizerImage.updateSettings(eqSettings);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        equalizer.release();
    }

    @Override
    public void onPresetSelected(final int index) {
        equalizer.usePreset((short) index);
        equalizerImage.updateBands(equalizer);
        presetButton.setText(equalizer.getPresetName((short) index));
    }
}
