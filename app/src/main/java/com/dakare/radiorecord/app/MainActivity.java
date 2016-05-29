package com.dakare.radiorecord.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MenuActivity implements StationClickListener, QualityDialog.QualityHandler
{
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        setTitle(R.string.menu_main_text);
        RecyclerView stationsView = (RecyclerView) findViewById(R.id.station_grid);
        stationsView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.stations_columns)));
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        stationsView.setAdapter(mRecyclerViewDragDropManager.createWrappedAdapter(new StationAdapter(this, this)));
        mRecyclerViewDragDropManager.attachRecyclerView(stationsView);
    }

    @Override
    public void onClick(final Station station)
    {
        PreferenceManager.getInstance(this).setLastStation(station);
        QualityDialog.getQuality(this, this);
    }

    @Override
    public void onQualitySelected(final Quality quality)
    {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        List<Station> stations = PreferenceManager.getInstance(this).getStations();
        ArrayList<PlaylistItem> items = new ArrayList<>(stations.size());
        for (Station station : stations)
        {
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
    protected void onPause()
    {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }
}
