package com.dakare.radiorecord.app.player.equalizer.preset;

import android.content.Context;
import android.media.audiofx.Equalizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import lombok.Getter;

public class EqualizerPresetAdapter extends ArrayAdapter<String> implements AdapterView.OnItemClickListener {
    private final LayoutInflater inflater;
    @Getter
    private int index = - 1;

    public EqualizerPresetAdapter(final Context context, final Equalizer equalizer) {
        super(context, 0);
        this.inflater = LayoutInflater.from(context);
        int count = equalizer.getNumberOfPresets();
        for (int i = 0; i < count; i++) {
            add(equalizer.getPresetName((short) i));
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_eq_preset, parent, false);
        } else {
            view = convertView;
        }
        ViewHolder.from(view);
        ViewHolder.icon.setChecked(index == position);
        ViewHolder.title.setText(getItem(position));
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        index = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private static RadioButton icon;
        private static TextView title;

        private static void from(View parent) {
            icon = (RadioButton) parent.findViewById(R.id.select_icon);
            title = (TextView) parent.findViewById(R.id.text);
        }
    }
}
