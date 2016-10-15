package com.dakare.radiorecord.app.quality;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import lombok.Getter;
import lombok.Setter;

public class QualityAdapter extends ArrayAdapter<QualityAdapterItem> implements AdapterView.OnItemClickListener {
    private final LayoutInflater inflater;
    @Getter
    @Setter
    private Quality selectedQuality = Quality.HIGH;

    public QualityAdapter(final Context context, boolean withNoQuality) {
        super(context, 0);
        this.inflater = LayoutInflater.from(context);
        if (withNoQuality) {
            add(new QualityAdapterItem(context.getString(R.string.no_default_quality)));
        }
        for (Quality quality : Quality.values()) {
            if (quality != Quality.AAC || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                add(new QualityAdapterItem(quality, context.getString(quality.getNameRes())));
            }
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
        QualityAdapterItem item = getItem(position);
        ViewHolder.icon.setChecked((item.getQuality() == null && selectedQuality == null) || item.getQuality() == selectedQuality);
        ViewHolder.title.setText(item.getText());
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedQuality = getItem(position).getQuality();
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private static RadioButton icon;
        private static TextView title;

        private static void from(View parent) {
            icon = (RadioButton) parent.findViewById(R.id.select_icon);
            title = (TextView) parent.findViewById(R.id.quality_text);
        }
    }
}
