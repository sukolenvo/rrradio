package com.dakare.radiorecord.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

public class MainActivity extends MenuActivity implements StationAdapter.StationClickListener, QualityDialog.QualityHandler
{
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        RecyclerView stationsView = (RecyclerView) findViewById(R.id.station_grid);
        stationsView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.stations_columns)));
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        stationsView.setAdapter(mRecyclerViewDragDropManager.createWrappedAdapter(new StationAdapter(this, this)));
        mRecyclerViewDragDropManager.attachRecyclerView(stationsView);
        hideMainMenuButton();
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
        serviceIntent.putExtra(PlayerService.STATION_KEY, PreferenceManager.getInstance(this).getLastStation().name());
        serviceIntent.putExtra(PlayerService.QUALITY_KEY, quality.name());
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
