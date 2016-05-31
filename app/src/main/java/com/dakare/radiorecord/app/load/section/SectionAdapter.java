package com.dakare.radiorecord.app.load.section;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;
import com.dakare.radiorecord.app.load.top.TopsMusicItem;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class SectionAdapter extends AbstractLoadAdapter<SectionAdapter.ViewHolder, SectionMusicItem>
{
    private final LayoutInflater inflater;
    private List<SectionMusicItem> items = new ArrayList<>();
    private final Context context;

    public SectionAdapter(final Context context)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public void setItems(final List<SectionMusicItem> items)
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
        final SectionMusicItem item = items.get(position);
        holder.title.setText(item.getSong());
        holder.subTitle.setText(item.getArtist());
        holder.container.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                Intent intent = new Intent(context, PlayerService.class);
                ArrayList<PlaylistItem> playlist = new ArrayList<>(items.size());
                for (SectionMusicItem item : items)
                {
                    playlist.add(new PlaylistItem(item));
                }
                intent.putExtra(PlayerService.PLAYLIST_KEY, playlist);
                intent.putExtra(PlayerService.POSITION_KEY, position);
                context.startService(intent);
                Intent intentActivity = new Intent(context, PlayerActivity.class);
                intentActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intentActivity);
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
