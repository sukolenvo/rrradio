package com.dakare.radiorecord.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationAdapter extends ArrayAdapter<Station>
{
    private final Context context;
    private final LayoutInflater inflater;

    public StationAdapter(final Context context)
    {
        super(context, 0);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        for (Station station : Station.values())
        {
            add(station);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        View view;
        if (convertView == null)
        {
            view = inflater.inflate(R.layout.item_station, null);
        } else
        {
            view = convertView;
        }
        ViewHolder.from(view);
        Station item = getItem(position);
        ViewHolder.icon.setImageResource(item.getIcon());
        ViewHolder.title.setText(item.getName());
        return view;
    }

    private static class ViewHolder
    {
        private static ImageView icon;
        private static TextView title;

        private static void from(View parent)
        {
            icon = (ImageView) parent.findViewById(R.id.station_icon);
            title = (TextView) parent.findViewById(R.id.station_name);
        }
    }
}
