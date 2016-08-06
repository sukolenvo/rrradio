package com.dakare.radiorecord.app.download;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.dakare.radiorecord.app.download.service.DownloadItem;
import com.dakare.radiorecord.app.download.service.DownloadsSort;
import com.dakare.radiorecord.app.download.service.FileServiceClient;
import com.dakare.radiorecord.app.download.service.FileServiceHelper;
import com.dakare.radiorecord.app.download.service.message.FileMessage;
import com.dakare.radiorecord.app.download.service.message.RemoveFileResponse;
import com.dakare.radiorecord.app.download.service.message.UpdateFileMessage;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.load.selection.DownloadsSelectionCallback;
import com.dakare.radiorecord.app.load.selection.SelectionManager;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;

import java.util.*;

public class DownloadsActivity extends MenuActivity implements FileServiceClient.PlayerMessageHandler, FileServiceHelper.ServiceBindListener, AbstractSelectionAdapter.PermissionProvider {

    private RecyclerView recyclerView;
    private View emptyView;
    private DownloadsAdapter downloadsAdapter;
    private final FileServiceHelper helper = new FileServiceHelper();
    private SelectionManager selectionManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        initToolbar();
        setTitle(R.string.menu_downloads_text);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.history_divider), false));
        selectionManager = new SelectionManager(this, new DownloadsSelectionCallback());
        downloadsAdapter = new DownloadsAdapter(this, selectionManager, null);
        recyclerView.setAdapter(downloadsAdapter);
        emptyView = findViewById(R.id.list_empty_stub);
        if (savedInstanceState != null) {
            selectionManager.restoreState(savedInstanceState);
        }
    }

    private void updateEmptyView() {
        if (downloadsAdapter.getItems().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getMenuContainer() {
        return R.id.menu_downloads_container;
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.getServiceClient().setPlayerMessageHandler(this);
        helper.bindService(this, this);
        downloadsAdapter.getItems().clear();
        downloadsAdapter.setItems(getContent());
        downloadsAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private List<DownloadItem> getContent() {
        List<DownloadItem> items = new ArrayList<>();
        Cursor cursor = StorageContract.getInstance().getAllAudio();
        if (cursor.moveToFirst()) {
            do {
                items.add(new DownloadItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.getServiceClient().setPlayerMessageHandler(null);
        helper.unbindService(this);
    }

    @Override
    public void onServiceConnected() {
        //Nothing to do
    }

    @Override
    public void onServiceDisconnected() {
        //Nothing to do
    }

    @Override
    public void onMessage(final FileMessage playerMessage) {
        switch (playerMessage.getMessageType()) {
            case UPDATE_ITEM:
                UpdateFileMessage msg = (UpdateFileMessage) playerMessage;
                for (int i = 0; i < downloadsAdapter.getItems().size(); i++) {
                    DownloadItem downloadItem = downloadsAdapter.getItems().get(i);
                    if (downloadItem.getId() == msg.getId()) {
                        downloadItem.setSize(msg.getSize());
                        downloadItem.setTotalSize(msg.getTotal());
                        if (msg.getStatus() != null) {
                            downloadItem.setStatus(msg.getStatus());
                        }
                        downloadsAdapter.notifyItemChanged(i);
                        return;
                    }
                }
                break;
            case REMOVE_RESPONSE:
                RemoveFileResponse response = (RemoveFileResponse) playerMessage;
                for (Integer id : response.getIds()) {
                    Iterator<DownloadItem> iterator = downloadsAdapter.getItems().iterator();
                    while (iterator.hasNext()) {
                        DownloadItem downloadItem = iterator.next();
                        if (downloadItem.getId() == id) {
                            iterator.remove();
                            break;
                        }
                    }
                }
                downloadsAdapter.notifyDataSetChanged();
                if (downloadsAdapter.getItems().isEmpty()) {
                    updateEmptyView();
                }
                break;
            default:
                Log.e("DownloadsActivity", "Failed to process message" + playerMessage.getMessageType());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.downloads_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_abc:
                PreferenceManager.getInstance(this).setDownloadsSort(DownloadsSort.NAME_ASC);
                Collections.sort(downloadsAdapter.getItems(), new Comparator<DownloadItem>() {
                    @Override
                    public int compare(DownloadItem a, DownloadItem b) {
                        return a.getTitle().compareTo(b.getTitle());
                    }
                });
                break;
            case R.id.sort_cba:
                PreferenceManager.getInstance(this).setDownloadsSort(DownloadsSort.NAME_DESC);
                Collections.sort(downloadsAdapter.getItems(), new Comparator<DownloadItem>() {
                    @Override
                    public int compare(DownloadItem a, DownloadItem b) {
                        return b.getTitle().compareTo(a.getTitle());
                    }
                });
                break;
            case R.id.sort_down:
                PreferenceManager.getInstance(this).setDownloadsSort(DownloadsSort.CREATED_ASC);
                Collections.sort(downloadsAdapter.getItems(), new Comparator<DownloadItem>() {
                    @Override
                    public int compare(DownloadItem a, DownloadItem b) {
                        return (int) (a.getSaved() - b.getSaved());
                    }
                });
                break;
            case R.id.sort_up:
                PreferenceManager.getInstance(this).setDownloadsSort(DownloadsSort.CREATED_DESC);
                Collections.sort(downloadsAdapter.getItems(), new Comparator<DownloadItem>() {
                    @Override
                    public int compare(DownloadItem a, DownloadItem b) {
                        return (int) (b.getSaved() - a.getSaved());
                    }
                });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadsAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionManager.saveState(outState);
    }
}
