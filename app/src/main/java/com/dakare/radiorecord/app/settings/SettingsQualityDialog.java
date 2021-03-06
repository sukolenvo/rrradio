package com.dakare.radiorecord.app.settings;

import android.content.Context;
import android.view.View;
import android.widget.ListView;
import com.dakare.radiorecord.app.utils.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityAdapter;

public class SettingsQualityDialog extends AbstractDialog {

    private QualityAdapter adapter;

    public SettingsQualityDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_settings_quality);
        ListView listView = findViewById(R.id.list);
        adapter = new QualityAdapter(context, true);
        Quality selected = PreferenceManager.getInstance(context).getDefaultQuality(null);
        adapter.setSelectedQuality(selected);
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
                PreferenceManager.getInstance(context).setDefaultQuality(adapter.getSelectedQuality());
                dismiss();
            }
        });
    }
}
