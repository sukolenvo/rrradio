package com.dakare.radiorecord.app;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Checkable;
import android.widget.ListView;

public class QualityDialog extends Dialog
{
    private static final String[] qualities = new String[] {"aac", "128", "320"};
    private QualityAdapter adapter;

    public QualityDialog(final Context context, final QualityHandler handler)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_quality);
        ListView listView = (ListView) findViewById(R.id.quality_list);
        adapter = new QualityAdapter(context);
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
                String quality = qualities[adapter.getSelectedPosition()];
                if (((Checkable) findViewById(R.id.checkbox_container)).isChecked())
                {
                    PreferenceManager.getInstance(context).setDefaultQuality(quality);
                }
                handler.onQualitySelected(quality);
                dismiss();
            }
        });
    }

    public static void getQuality(Context context, QualityHandler handler)
    {
        String defaultQuality = PreferenceManager.getInstance(context).getDefaultQuality();
        if (defaultQuality == null)
        {
            new QualityDialog(context, handler).show();
        } else
        {
            handler.onQualitySelected(defaultQuality);
        }
    }

    public interface QualityHandler
    {
        void onQualitySelected(String quality);
    }
}
