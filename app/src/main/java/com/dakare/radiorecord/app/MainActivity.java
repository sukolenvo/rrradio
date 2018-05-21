package com.dakare.radiorecord.app;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.dakare.radiorecord.app.ads.AdUtils;
import com.dakare.radiorecord.app.download.service.FileService;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityDialog;
import com.dakare.radiorecord.app.settings.SettingsThemeDialog;
import com.google.android.gms.ads.AdView;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MenuActivity implements StationClickListener, QualityDialog.QualityHandler {
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        setTitle(R.string.menu_main_text);
        RecyclerView stationsView = findViewById(R.id.station_grid);
        stationsView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.stations_columns)));
        //TODO: fix this!
        TypedArray attributes = getTheme().obtainStyledAttributes(new int[] {R.attr.main_separator_drawable});
        int decoratorId = attributes.getResourceId(0, 0);
        attributes.recycle();
        stationsView.addItemDecoration(new GridDecorator(
                2, getResources().getInteger(R.integer.stations_columns), getResources().getDrawable(decoratorId)));
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        stationsView.setAdapter(mRecyclerViewDragDropManager.createWrappedAdapter(new StationAdapter(this, this)));
        mRecyclerViewDragDropManager.attachRecyclerView(stationsView);
        startService(new Intent(this, FileService.class));
        if (PreferenceManager.getInstance(this).showMainHint()) {
            final View image = findViewById(R.id.hint);
            image.setBackgroundResource(R.drawable.hint_sort);
            image.setVisibility(View.VISIBLE);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    image.setVisibility(View.GONE);
                    PreferenceManager.getInstance(MainActivity.this).hideMainHint();
                }
            });
        }
        if (PreferenceManager.getInstance(this).getThemePrompt()) {
            PreferenceManager.getInstance(this).setThemePrompt(false);
            new SettingsThemeDialog(this).show();
        }
        AdUtils.showAd((AdView) findViewById(R.id.adView));
        List<PlaylistItem> lastPlaylist = PreferenceManager.getInstance(this).getLastPlaylist();
        int lastPosition = PreferenceManager.getInstance(this).getLastPosition();
        if (lastPlaylist.size() > 1 && lastPosition < lastPlaylist.size() && lastPlaylist.get(lastPosition).isLive()) {
            new ResumePlaybackDialog(this,
                    lastPlaylist.get(lastPosition).getTitle() + " - "
                            + lastPlaylist.get(lastPosition).getSubtitle()).show();
        }
    }

    @Override
    public void onClick(final Station station) {
        PreferenceManager.getInstance(this).setLastStation(station);
        QualityDialog.getQuality(this, this);
    }

    @Override
    public void onQualitySelected(final Quality quality) {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        List<Station> stations = PreferenceManager.getInstance(this).getStations();
        ArrayList<PlaylistItem> items = new ArrayList<>(stations.size());
        for (Station station : stations) {
            items.add(new PlaylistItem(station, quality));
        }
        serviceIntent.putExtra(PlayerService.PLAYLIST_KEY, items);
        serviceIntent.putExtra(PlayerService.POSITION_KEY, stations.indexOf(PreferenceManager.getInstance(this).getLastStation()));
        startService(serviceIntent);
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }

    @Override
    protected int getMenuContainer() {
        return R.id.menu_main_container;
    }
}
