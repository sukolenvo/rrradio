package com.dakare.radiorecord.app.load.selection;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.dakare.radiorecord.app.download.service.FileService;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSelectionAdapter<T extends RecyclerView.ViewHolder, K> extends AbstractLoadAdapter<T, K>
        implements SelectionManager.SelectionItemCallback {
    @Getter
    protected List<K> items = new ArrayList<>();
    private final LayoutInflater inflater;
    protected final Context context;
    protected final SelectionManager selectionManager;
    private final PermissionProvider permissionProvider;

    public AbstractSelectionAdapter(final Context context, final SelectionManager selectionManager, final PermissionProvider permissionProvider) {
        this.context = context;
        this.selectionManager = selectionManager;
        this.permissionProvider = permissionProvider;
        selectionManager.setCallback(this);
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public void setItems(final List<K> items) {
        if (this.items != items || !this.items.containsAll(items)) {
            this.items = items;
            notifyDataSetChanged();
        }
    }

    @Override
    public T onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = inflater.inflate(getLayoutId(), parent, false);
        view.setOnClickListener(selectionManager);
        view.setOnLongClickListener(selectionManager);
        return createHolder(view);
    }

    protected abstract int getLayoutId();

    protected abstract T createHolder(View view);

    @Override
    public void onBindViewHolder(final T holder, final int position) {
        holder.itemView.setTag(position);
        holder.itemView.setSelected(selectionManager.isSelecting() && selectionManager.isSelected(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void playSelected() {
        ArrayList<PlaylistItem> list = getSelectedList();
        if (list.isEmpty()) {
            showEmptySelection();
        } else {
            play(list, 0);
            selectionManager.finishSelection();
        }
    }

    protected void showEmptySelection() {
        Toast.makeText(context, context.getString(R.string.error_empty_selection), Toast.LENGTH_LONG).show();
    }

    public boolean onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadSelected();
            return true;
        }
        return false;
    }

    @Override
    public void downloadSelected() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionProvider.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
        ArrayList<PlaylistItem> downloadList = getSelectedList();
        if (downloadList.isEmpty()) {
            showEmptySelection();
        } else {
            StorageContract.getInstance().bulkInsertDownloadAudio(downloadList);
            context.startService(new Intent(context, FileService.class));
            selectionManager.finishSelection();
        }
    }

    protected ArrayList<PlaylistItem> getSelectedList() {
        ArrayList<PlaylistItem> downloadList = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selectionManager.isSelected(i)) {
                downloadList.add(toPlaylistItem(i));
            }
        }
        return downloadList;
    }

    protected abstract PlaylistItem toPlaylistItem(int position);

    @Override
    public void removeSelected() {

    }

    protected void play(final ArrayList<PlaylistItem> items, final int position) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerService.PLAYLIST_KEY, items);
        intent.putExtra(PlayerService.POSITION_KEY, position);
        context.startService(intent);
        Intent intentActivity = new Intent(context, PlayerActivity.class);
        intentActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intentActivity);
    }

    @Override
    public void onSelectionChanged(final int position) {
        if (position == SelectionManager.POSITION_UNKNOWN) {
            notifyDataSetChanged();
        } else {
            notifyItemChanged(position);
        }
    }

    @Override
    public void onClick(final View v) {
        ArrayList<PlaylistItem> playlist = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            playlist.add(toPlaylistItem(i));
        }
        play(playlist, (Integer) v.getTag());
    }

    public interface PermissionProvider {
        void requestPermissions(final String[] permissions, final int code);
    }
}
