package com.dakare.radiorecord.app.widget;


import android.app.Service;
import android.content.ComponentName;
import com.dakare.radiorecord.app.R;
import lombok.Getter;

public class WidgetListenerDark extends WidgetListener {

    @Getter
    private final ComponentName componentName;

    public WidgetListenerDark(final Service service) {
        super(service);
        componentName = new ComponentName(service, WidgetReceiverDark.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.widget_dark;
    }
}
