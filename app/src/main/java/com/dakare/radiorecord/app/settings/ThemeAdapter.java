package com.dakare.radiorecord.app.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.view.theme.Theme;

public class ThemeAdapter extends ArrayAdapter<Theme> {
    private final LayoutInflater inflater;
    private Theme selectedItem;

    public ThemeAdapter(final Context context) {
        super(context, 0);
        this.inflater = LayoutInflater.from(context);
        selectedItem = PreferenceManager.getInstance(context).getTheme();
        for (Theme item : Theme.values()) {
            add(item);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_quality, parent, false);
        } else {
            view = convertView;
        }
        ViewHolder.from(view);
        Theme item = getItem(position);
        ViewHolder.icon.setChecked(item == selectedItem);
        ViewHolder.title.setText(item.getNameRes());
        return view;
    }

    private static class ViewHolder {
        private static RadioButton icon;
        private static TextView title;

        private static void from(View parent) {
            icon = parent.findViewById(R.id.select_icon);
            title = parent.findViewById(R.id.text);
        }
    }
}
