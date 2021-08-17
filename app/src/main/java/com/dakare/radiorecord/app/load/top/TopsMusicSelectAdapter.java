package com.dakare.radiorecord.app.load.top;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.SelectionManager;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.station.DynamicStation;

public class TopsMusicSelectAdapter extends AbstractSelectionAdapter<TopsMusicSelectAdapter.ViewHolder, TopsMusicItem> {
    private final DynamicStation station;

    public TopsMusicSelectAdapter(final Context context, final DynamicStation station,
                                  final SelectionManager selectionManager, final PermissionProvider permissionProvider) {
        super(context, selectionManager, permissionProvider);
        this.station = station;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_tops_music;
    }

    @Override
    protected ViewHolder createHolder(final View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        TopsMusicItem item = items.get(position);
        holder.title.setText(item.getSong());
        holder.subTitle.setText(item.getArtist());
    }

    @Override
    protected PlaylistItem toPlaylistItem(final int position) {
        return new PlaylistItem(station, items.get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView subTitle;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            title.setSelected(true);
            subTitle = itemView.findViewById(R.id.sub_title);
        }
    }
}
