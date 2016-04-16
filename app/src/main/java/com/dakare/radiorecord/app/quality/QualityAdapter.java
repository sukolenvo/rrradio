package com.dakare.radiorecord.app.quality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import lombok.Getter;

public class QualityAdapter extends ArrayAdapter<String> implements AdapterView.OnItemClickListener
{
    private final Context context;
    private final LayoutInflater inflater;
    @Getter
    private int selectedPosition;

    public QualityAdapter(final Context context)
    {
        super(context, 0);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        for (String q : context.getResources().getStringArray(R.array.qualities))
        {
            add(q);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        View view;
        if (convertView == null)
        {
            view = inflater.inflate(R.layout.item_quality, null);
        } else
        {
            view = convertView;
        }
        ViewHolder.from(view);
        String item = getItem(position);
        ViewHolder.icon.setImageResource(position == selectedPosition
                ? R.drawable.ic_radio_button_checked_black_24dp : R.drawable.ic_radio_button_unchecked_black_24dp);
        ViewHolder.title.setText(item);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    private static class ViewHolder
    {
        private static ImageView icon;
        private static TextView title;

        private static void from(View parent)
        {
            icon = (ImageView) parent.findViewById(R.id.select_icon);
            title = (TextView) parent.findViewById(R.id.quality_text);
        }
    }
}
