package com.dakare.radiorecord.app.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryMusicSelectAdapter extends AbstractHistoryLoadAdapter<HistoryMusicSelectAdapter.ViewHolder, HistoryMusicItem>
{
    private final LayoutInflater inflater;
    private List<HistoryMusicItem> items = new ArrayList<HistoryMusicItem>();
    private final HistoryFragmentMediator historyFragmentMediator;

    public HistoryMusicSelectAdapter(final Context context, final HistoryFragmentMediator historyFragmentMediator)
    {
        this.historyFragmentMediator = historyFragmentMediator;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    public void setItems(final List<HistoryMusicItem> items)
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
        View view = inflater.inflate(R.layout.item_history_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final HistoryMusicItem item = items.get(position);
        holder.title.setText(item.getSong());
        holder.subTitle.setText(item.getArtist());
        holder.duration.setText(item.getWhen());
        holder.container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

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
}
