/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package com.dakare.radiorecord.app.settings;

import android.content.Context;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.utils.AbstractDialog;

/**
 * @author vadym
 */
public class LoadStationsDialog extends AbstractDialog implements StationLoadTask.StationLoadListener {

    private final TextView progressView;

    public LoadStationsDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_load_stations);
        progressView = findViewById(R.id.load_station_progress);
        new StationLoadTask(this).execute();
    }

    @Override
    public void updateStatus(String status) {
        progressView.setText(status);
    }
}
