package com.dakare.radiorecord.app.settings;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityAdapter;

public class SettingsQualityDialog extends Dialog
{
    private QualityAdapter adapter;

    public SettingsQualityDialog(final Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings_quality);
        ListView listView = (ListView) findViewById(R.id.quality_list);
        adapter = new QualityAdapter(context, true);
        Quality selected = PreferenceManager.getInstance(context).getDefaultQuality(null);
        adapter.setSelectedQuality(selected);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PreferenceManager.getInstance(context).setDefaultQuality(adapter.getSelectedQuality());
                dismiss();
            }
        });
    }
}
