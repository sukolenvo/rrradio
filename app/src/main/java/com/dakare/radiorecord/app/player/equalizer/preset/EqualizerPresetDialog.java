package com.dakare.radiorecord.app.player.equalizer.preset;

import android.content.Context;
import android.media.audiofx.Equalizer;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.dakare.radiorecord.app.utils.AbstractDialog;
import com.dakare.radiorecord.app.R;

public class EqualizerPresetDialog extends AbstractDialog {

    public EqualizerPresetDialog(final Context context, final Equalizer equalizer, final PresetSelectListener listener) {
        super(context);
        setContentView(R.layout.dialog_preset_equlizer);
        ListView listView = findViewById(R.id.list);
        final EqualizerPresetAdapter adapter = new EqualizerPresetAdapter(context, equalizer);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter.getIndex() >= 0) {
                    listener.onPresetSelected(adapter.getIndex());
                    dismiss();
                } else {
                    Toast.makeText(context, R.string.message_no_selected, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public interface PresetSelectListener {

        void onPresetSelected(final int index);
    }
}
