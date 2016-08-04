package com.dakare.radiorecord.app.load.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.StationClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class HistoryStationSelectAdapter extends RecyclerView.Adapter<HistoryStationSelectAdapter.ViewHolder>
{
    private final Context context;
    private final LayoutInflater inflater;
    private final List<Station> items = new ArrayList<Station>();
    private final StationClickListener callback;
    private final PreferenceManager preferenceManager;

    public HistoryStationSelectAdapter(final Context context, final StationClickListener callback)
    {
        preferenceManager = PreferenceManager.getInstance(context);
        for (Station station : preferenceManager.getStations())
        {
            items.add(station);
        }
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.callback = callback;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        View view = inflater.inflate(R.layout.item_station, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final Station item = items.get(position);
        ImageLoader.getInstance().displayImage("drawable://" + item.getIcon(), holder.icon);
        holder.title.setText(item.getName());
        holder.container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callback.onClick(item);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).ordinal();
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView icon;
        private TextView title;
        private FrameLayout container;

        public ViewHolder(View itemView)
        {
            super(itemView);
            container = (FrameLayout) itemView.findViewById(R.id.station_container);
            icon = (ImageView) itemView.findViewById(R.id.station_icon);
            title = (TextView) itemView.findViewById(R.id.station_name);
        }
    }
}
