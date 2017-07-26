package com.dakare.radiorecord.app.quality;

import android.content.Context;
import android.view.View;
import android.widget.Checkable;
import android.widget.ListView;
import com.dakare.radiorecord.app.utils.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;

public class QualityDialog extends AbstractDialog {

    private QualityAdapter adapter;

    public QualityDialog(final Context context, final QualityHandler handler) {
        super(context);
        setContentView(R.layout.dialog_quality);
        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new QualityAdapter(context, false);
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
                Quality quality = adapter.getSelectedQuality();
                if (((Checkable) findViewById(R.id.checkbox_container)).isChecked()) {
                    PreferenceManager.getInstance(context).setDefaultQuality(quality);
                }
                handler.onQualitySelected(quality);
                dismiss();
            }
        });
    }

    public static void getQuality(final Context context, final QualityHandler handler) {
        Quality defaultQuality = PreferenceManager.getInstance(context).getDefaultQuality(null);
        if (defaultQuality == null) {
            new QualityDialog(context, handler).show();
        } else {
            handler.onQualitySelected(defaultQuality);
        }
    }

    public interface QualityHandler {
        void onQualitySelected(Quality quality);
    }
}
