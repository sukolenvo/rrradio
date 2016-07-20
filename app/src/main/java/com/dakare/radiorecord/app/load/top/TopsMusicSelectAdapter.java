package com.dakare.radiorecord.app.load.top;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.SelectionManager;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;

public class TopsMusicSelectAdapter extends AbstractSelectionAdapter<TopsMusicSelectAdapter.ViewHolder, TopsMusicItem>
{
    private final Station station;

    public TopsMusicSelectAdapter(final Context context, final Station station,
                                  final SelectionManager selectionManager, final PermissionProvider permissionProvider)
    {
        super(context, selectionManager, permissionProvider);
        this.station = station;
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.item_tops_music;
    }

    @Override
    protected ViewHolder createHolder(final View view)
    {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        super.onBindViewHolder(holder, position);
        TopsMusicItem item = items.get(position);
        holder.title.setText(item.getSong());
        holder.subTitle.setText(item.getArtist());
    }

    @Override
    protected PlaylistItem toPlaylistItem(final int position)
    {
        return new PlaylistItem(station, items.get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView title;
        private final TextView subTitle;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            title.setSelected(true);
            subTitle = (TextView) itemView.findViewById(R.id.sub_title);
        }
    }
}
