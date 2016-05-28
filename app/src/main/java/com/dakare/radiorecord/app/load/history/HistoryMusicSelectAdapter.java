package com.dakare.radiorecord.app.load.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryMusicSelectAdapter extends AbstractLoadAdapter<HistoryMusicSelectAdapter.ViewHolder, HistoryMusicItem>
{
    private final LayoutInflater inflater;
    private List<HistoryMusicItem> items = new ArrayList<HistoryMusicItem>();
    private final HistoryFragmentMediator historyFragmentMediator;
    private final Station station;
    private final PreferenceManager preferenceManager;
    private List<HistoryMusicItem> visibleList = new ArrayList<HistoryMusicItem>();

    public HistoryMusicSelectAdapter(final Context context, final HistoryFragmentMediator historyFragmentMediator, final Station station)
    {
        this.station = station;
        this.historyFragmentMediator = historyFragmentMediator;
        this.inflater = LayoutInflater.from(context);
        preferenceManager = PreferenceManager.getInstance(context);
        setHasStableIds(true);
    }

    @Override
    public void setItems(final List<HistoryMusicItem> items)
    {
        if (this.items != items || !this.items.containsAll(items))
        {
            this.items = items;
            onPrefChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        View view = inflater.inflate(R.layout.item_history_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final HistoryMusicItem item = visibleList.get(position);
        holder.title.setText(item.getSong());
        holder.subTitle.setText(item.getArtist());
        holder.duration.setText(item.getWhen());
        holder.container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                historyFragmentMediator.onMusicSelected(visibleList, position, station);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return visibleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView title;
        private final TextView subTitle;
        private final TextView duration;
        private final View container;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            container = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            title.setSelected(true);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title);
            duration = (TextView) itemView.findViewById(R.id.duration);
        }
    }

    public void onPrefChanged()
    {
        if (preferenceManager.isHistoryShowAll())
        {
            visibleList = items;
        } else
        {
            visibleList = new ArrayList<HistoryMusicItem>();
            for (HistoryMusicItem item : items)
            {
                if (item.isVisible())
                {
                    visibleList.add(item);
                }
            }
        }
        if (preferenceManager.isHistorySortOld())
        {
            Collections.sort(visibleList, new Comparator<HistoryMusicItem>()
            {
                @Override
                public int compare(final HistoryMusicItem lhs, final HistoryMusicItem rhs)
                {
                    return lhs.getWhen().compareTo(rhs.getWhen());
                }
            });
        } else
        {
            Collections.sort(visibleList, new Comparator<HistoryMusicItem>()
            {
                @Override
                public int compare(final HistoryMusicItem lhs, final HistoryMusicItem rhs)
                {
                    return rhs.getWhen().compareTo(lhs.getWhen());
                }
            });
        }
        notifyDataSetChanged();
    }
}
