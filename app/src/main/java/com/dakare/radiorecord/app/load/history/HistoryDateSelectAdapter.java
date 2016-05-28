package com.dakare.radiorecord.app.load.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoryDateSelectAdapter extends AbstractLoadAdapter<HistoryDateSelectAdapter.ViewHolder, String>
{
    private final LayoutInflater inflater;
    private List<String> items = new ArrayList<String>();
    private final HistoryFragmentMediator historyFragmentMediator;
    private final Station station;

    public HistoryDateSelectAdapter(final Context context, final HistoryFragmentMediator historyFragmentMediator, final Station station)
    {
        this.historyFragmentMediator = historyFragmentMediator;
        this.station = station;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    //TODO: make abstract adapter and extract items logic there
    public void setItems(final List<String> items)
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
        View view = inflater.inflate(R.layout.item_history_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        final String item = items.get(position);
        holder.textView.setText(item);
        holder.container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                historyFragmentMediator.onDateSelected(station, item);
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
        private final TextView textView;
        private final View container;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            container = itemView;
            textView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
