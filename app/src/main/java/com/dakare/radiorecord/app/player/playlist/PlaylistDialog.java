package com.dakare.radiorecord.app.player.playlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.dakare.radiorecord.app.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;

public class PlaylistDialog extends AbstractDialog implements SharedPreferences.OnSharedPreferenceChangeListener,
                                                              DialogInterface.OnDismissListener {

    private final PlaylistAdapter adapter;

    public PlaylistDialog(final Context context, final AbstractSelectionAdapter.PermissionProvider permissionSupplyer) {
        super(context);
        setContentView(R.layout.dialog_playlist);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        ListView listView = (ListView) findViewById(R.id.playlist);
        listView.setEmptyView(findViewById(R.id.no_results));
        adapter = new PlaylistAdapter(getContext(), permissionSupplyer);
        int lastPosition = PreferenceManager.getInstance(context).getLastPosition();
        adapter.setSelectedPosition(lastPosition);
        adapter.addAll(PreferenceManager.getInstance(context).getLastPlaylist());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (adapter.getSelectedPosition() == position) {
                    context.startService(new Intent(context, PlayerService.class).setAction(NotificationListener.ACTION_PLAY_PAUSE));
                } else {
                    Intent serviceIntent = new Intent(context, PlayerService.class);
                    serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
                    context.startService(serviceIntent);
                    adapter.setSelectedPosition(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        listView.setSelection(lastPosition);
        PreferenceManager.getInstance(context).registerChangeListener(this);
        setOnDismissListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (PreferenceManager.LAST_PLAYLIST_POSITION_KEY.equals(key)) {
            adapter.setSelectedPosition(sharedPreferences.getInt(key, 0));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        PreferenceManager.getInstance(getContext()).unregisterChangeListener(this);
    }
}
