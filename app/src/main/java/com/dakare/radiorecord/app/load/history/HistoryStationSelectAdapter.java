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
import com.dakare.radiorecord.app.station.AbstractStation;
import com.dakare.radiorecord.app.StationClickListener;
import com.dakare.radiorecord.app.view.theme.Theme;

import java.util.ArrayList;
import java.util.List;

public class HistoryStationSelectAdapter extends RecyclerView.Adapter<HistoryStationSelectAdapter.ViewHolder> {
    private final Context context;
    private final LayoutInflater inflater;
    private final List<AbstractStation> items = new ArrayList<>();
    private final StationClickListener callback;
    private final PreferenceManager preferenceManager;
    private final Theme theme;

    public HistoryStationSelectAdapter(final Context context, final StationClickListener callback) {
        preferenceManager = PreferenceManager.getInstance(context);
        theme = preferenceManager.getTheme();
        for (AbstractStation station : preferenceManager.getStations()) {
            items.add(station);
        }
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.callback = callback;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = inflater.inflate(theme == Theme.CLASSIC ? R.layout.item_station_classic : R.layout.item_station, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AbstractStation item = items.get(position);
        holder.icon.setImageBitmap(item.getStationIcon(theme));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(item);
            }
        });
        if (holder.name != null) {
            holder.name.setText(item.getName());
        }
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getCode().hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private FrameLayout container;
        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.station_container);
            icon = itemView.findViewById(R.id.station_icon);
            name = itemView.findViewById(R.id.station_name);
        }
    }
}
