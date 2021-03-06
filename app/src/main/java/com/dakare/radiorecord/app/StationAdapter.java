package com.dakare.radiorecord.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dakare.radiorecord.app.station.DynamicStation;
import com.dakare.radiorecord.app.view.theme.Theme;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder>
        implements DraggableItemAdapter<StationAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<DynamicStation> items = new ArrayList<>();
    private final StationClickListener callback;
    private final PreferenceManager preferenceManager;
    private final Theme theme;

    public StationAdapter(final Context context, final StationClickListener callback) {
        preferenceManager = PreferenceManager.getInstance(context);
        theme = preferenceManager.getTheme();
        items.addAll(preferenceManager.getStations());
        this.inflater = LayoutInflater.from(context);
        this.callback = callback;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = inflater.inflate(R.layout.item_station, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DynamicStation item = items.get(position);
        holder.icon.setImageBitmap(item.getStationIcon(theme));
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(item);
            }
        });
        final int dragState = holder.getDragStateFlags();
        if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
            holder.stationFade.setSelected(true);
            clearState(holder.container.getForeground());
        } else {
            holder.stationFade.setSelected(false);
        }
        if (holder.name != null) {
            holder.name.setText(item.getName());
        }
    }

    private void clearState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(new int[0]);
        }
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getKey().hashCode();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public boolean onCheckCanStartDrag(ViewHolder viewHolder, int i, int i1, int i2) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder viewHolder, int i) {
        return null;
    }

    @Override
    public void onMoveItem(final int fromPosition, final int toPosition) {
        if (fromPosition != toPosition) {
            DynamicStation fromItem = items.remove(fromPosition);
            items.add(toPosition, fromItem);
            notifyItemMoved(fromPosition, toPosition);
            preferenceManager.setStations(items);
        }
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {

    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {

    }

    public static class ViewHolder extends AbstractDraggableItemViewHolder {
        private final TextView name;
        private ImageView icon;
        private FrameLayout container;
        private View stationFade;

        public ViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.station_container);
            icon = itemView.findViewById(R.id.station_icon);
            stationFade = itemView.findViewById(R.id.station_fade);
            name = itemView.findViewById(R.id.station_name);
        }

    }
}
