package com.dakare.radiorecord.app.load.history;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.SelectionManager;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.station.DynamicStation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryMusicSelectAdapter extends AbstractSelectionAdapter<HistoryMusicSelectAdapter.ViewHolder, HistoryMusicItem> {
    private List<HistoryMusicItem> items = new ArrayList<HistoryMusicItem>();
    private final DynamicStation station;
    private final PreferenceManager preferenceManager;
    private List<HistoryMusicItem> visibleList = new ArrayList<HistoryMusicItem>();

    public HistoryMusicSelectAdapter(final Context context, final DynamicStation station,
                                     final SelectionManager selectionManager, final PermissionProvider permissionProvider) {
        super(context, selectionManager, permissionProvider);
        this.station = station;
        preferenceManager = PreferenceManager.getInstance(context);
    }

    @Override
    public void setItems(final List<HistoryMusicItem> items) {
        if (this.items != items || !this.items.containsAll(items)) {
            this.items = items;
            onPrefChanged();
        }
    }

    public void onPrefChanged() {
        if (preferenceManager.isHistoryShowAll()) {
            visibleList = items;
        } else {
            visibleList = new ArrayList<>();
            for (HistoryMusicItem item : items) {
                if (item.isVisible()) {
                    visibleList.add(item);
                }
            }
        }
        if (preferenceManager.isHistorySortOld()) {
            Collections.sort(visibleList, new Comparator<HistoryMusicItem>() {
                @Override
                public int compare(final HistoryMusicItem lhs, final HistoryMusicItem rhs) {
                    return lhs.getWhen().compareTo(rhs.getWhen());
                }
            });
        } else {
            Collections.sort(visibleList, new Comparator<HistoryMusicItem>() {
                @Override
                public int compare(final HistoryMusicItem lhs, final HistoryMusicItem rhs) {
                    return rhs.getWhen().compareTo(lhs.getWhen());
                }
            });
        }
        super.setItems(visibleList);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_history_music;
    }

    @Override
    protected ViewHolder createHolder(final View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        HistoryMusicItem item = visibleList.get(position);
        holder.title.setText(item.getSong());
        holder.subTitle.setText(item.getArtist());
        holder.duration.setText(item.getWhen());
    }

    @Override
    protected PlaylistItem toPlaylistItem(final int position) {
        return new PlaylistItem(station, getItems().get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView subTitle;
        private final TextView duration;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            title.setSelected(true);
            subTitle = itemView.findViewById(R.id.sub_title);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
