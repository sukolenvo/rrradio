package com.dakare.radiorecord.app.player.playlist;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.dakare.radiorecord.app.AbstractDialog;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.settings.SettingsActivity;

public class PlaylistDialog extends AbstractDialog {

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
        final PlaylistAdapter adapter = new PlaylistAdapter(getContext(), permissionSupplyer);
        adapter.setPosition(PreferenceManager.getInstance(context).getLastPosition());
        adapter.addAll(PreferenceManager.getInstance(context).getLastPlaylist());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                Intent serviceIntent = new Intent(context, PlayerService.class);
                serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
                context.startService(serviceIntent);
                adapter.setPosition(position);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
