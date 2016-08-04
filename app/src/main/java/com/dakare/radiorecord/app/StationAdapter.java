package com.dakare.radiorecord.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> implements DraggableItemAdapter<StationAdapter.ViewHolder>
{
    private final LayoutInflater inflater;
    private final List<Station> items = new ArrayList<Station>();
    private final StationClickListener callback;
    private final PreferenceManager preferenceManager;

    public StationAdapter(final Context context, final StationClickListener callback)
    {
        preferenceManager = PreferenceManager.getInstance(context);
        for (Station station : preferenceManager.getStations())
        {
            items.add(station);
        }
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
        final int dragState = holder.getDragStateFlags();
        if (((dragState & DraggableItemConstants.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                clearState(holder.container.getForeground());
            } else if ((dragState & DraggableItemConstants.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.container.setBackgroundResource(bgResId);
        }
    }

    private void clearState(Drawable drawable)
    {
        if (drawable != null) {
            drawable.setState(new int[0]);
        }
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

    @Override
    public boolean onCheckCanStartDrag(ViewHolder viewHolder, int i, int i1, int i2)
    {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(ViewHolder viewHolder, int i)
    {
        return null;
    }

    @Override
    public void onMoveItem(final int fromPosition, final int toPosition)
    {
        if (fromPosition != toPosition)
        {
            Station fromItem = items.get(fromPosition);
            items.set(fromPosition, items.get(toPosition));
            items.set(toPosition, fromItem);
            notifyItemMoved(fromPosition, toPosition);
            preferenceManager.setStations(items);
        }
    }

    public static class ViewHolder extends AbstractDraggableItemViewHolder
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
