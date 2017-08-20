package com.dakare.radiorecord.app.download;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.database.table.DownloadAudioTable;
import com.dakare.radiorecord.app.download.service.DownloadItem;
import com.dakare.radiorecord.app.download.service.FileService;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.SelectionManager;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.view.DownloadImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DownloadsAdapter extends AbstractSelectionAdapter<DownloadsAdapter.ViewHolder, DownloadItem> {

    private final SimpleDateFormat format;

    public DownloadsAdapter(final Context context, final SelectionManager selectionManager, final PermissionProvider permissionProvider) {
        super(context, selectionManager, permissionProvider);
        format = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_download_music;
    }

    @Override
    protected ViewHolder createHolder(final View view) {
        return new ViewHolder(view);
    }

    @Override
    protected PlaylistItem toPlaylistItem(final int position) {
        return new PlaylistItem(items.get(position));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        DownloadItem item = items.get(position);
        if (item.getTotalSize() > 0) {
            holder.image.setProgress((int) (item.getSize() * 100 / item.getTotalSize()));
            holder.sizeView.setText(String.format("%s / %s", humanReadableByteCount(item.getSize(), false), humanReadableByteCount(item.getTotalSize(), false)));
        } else {
            holder.image.setProgress(item.getStatus() == DownloadAudioTable.Status.DOWNLOADED ? 100 : 0);
            holder.sizeView.setText(humanReadableByteCount(item.getSize(), false));
        }
        holder.titleView.setText(String.format("%s - %s", item.getTitle(), item.getSubtitle()));
        holder.createdView.setText(format.format(new Date(item.getSaved())));
        holder.statusView.setText(item.getStatus().getMessageId());
    }

    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    protected ArrayList<PlaylistItem> getSelectedList() {
        boolean skipped = false;
        ArrayList<PlaylistItem> downloadList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            DownloadAudioTable.Status status = items.get(i).getStatus();
            if (status == DownloadAudioTable.Status.DOWNLOADED || status == DownloadAudioTable.Status.DOWNLOADING) {
                if (selectionManager.isSelected(i)) {
                    downloadList.add(toPlaylistItem(i));
                }
            } else {
                skipped = true;
            }
        }
        if (skipped) {
            Toast.makeText(context, R.string.error_not_downloaded, Toast.LENGTH_LONG).show();
        }
        return downloadList;
    }

    @Override
    public void downloadSelected() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSelected() {
        ArrayList<Integer> selectedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selectionManager.isSelected(i)) {
                selectedItems.add((int) items.get(i).getId());
            }
        }
        if (selectedItems.isEmpty()) {
            showEmptySelection();
        } else {
            Intent service = new Intent(context, FileService.class);
            service.putExtra(FileService.IDS_KEY, selectedItems);
            context.startService(service);
            selectionManager.finishSelection();
        }
    }

    @Override
    public void onClick(final View v) {
        boolean skipped = false;
        int selectedPosition = (Integer) v.getTag();
        ArrayList<PlaylistItem> playlist = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            DownloadAudioTable.Status status = items.get(i).getStatus();
            if (status == DownloadAudioTable.Status.DOWNLOADED || status == DownloadAudioTable.Status.DOWNLOADING) {
                playlist.add(toPlaylistItem(i));
            } else {
                skipped = true;
            }
            if (i == selectedPosition) {
                selectedPosition = Math.max(0, playlist.size() - 1);
            }
        }
        if (skipped) {
            Toast.makeText(context, R.string.error_not_downloaded, Toast.LENGTH_LONG).show();
        }
        if (!playlist.isEmpty()) {
            play(playlist, selectedPosition);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final DownloadImageView image;
        private final TextView titleView;
        private final TextView sizeView;
        private final TextView createdView;
        private final TextView statusView;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (DownloadImageView) itemView.findViewById(R.id.download_image);
            titleView = (TextView) itemView.findViewById(R.id.title);
            titleView.setSelected(true);
            sizeView = (TextView) itemView.findViewById(R.id.size);
            createdView = (TextView) itemView.findViewById(R.id.saved);
            statusView = (TextView) itemView.findViewById(R.id.status);
        }
    }
}
