package com.dakare.radiorecord.app.load.top;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;
import com.dakare.radiorecord.app.load.history.HistoryFragmentMediator;
import com.dakare.radiorecord.app.load.history.HistoryMusicItem;

import java.util.ArrayList;
import java.util.List;

public class TopsMusicSelectAdapter extends AbstractLoadAdapter<TopsMusicSelectAdapter.ViewHolder, TopsMusicItem>
{
    private final LayoutInflater inflater;
    private List<TopsMusicItem> items = new ArrayList<TopsMusicItem>();
    private final TopsFragmentMediator fragmentMediator;
    private final Station station;

    public TopsMusicSelectAdapter(final Context context, final TopsFragmentMediator fragmentMediator, final Station station)
    {
        this.station = station;
        this.fragmentMediator = fragmentMediator;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public void setItems(final List<TopsMusicItem> items)
    {
        if (this.items != items || !this.items.containsAll(items))
        {
            this.items = items;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        View view = inflater.inflate(R.layout.item_tops_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final TopsMusicItem item = items.get(position);
        holder.title.setText(item.getSong());
        holder.subTitle.setText(item.getArtist());
        holder.container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                fragmentMediator.onItemsSelected(items, position, station);
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
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView title;
        private final TextView subTitle;
        private final View container;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            container = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            title.setSelected(true);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title);
        }
    }
}
