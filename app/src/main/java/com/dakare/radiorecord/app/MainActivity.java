package com.dakare.radiorecord.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityDialog;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

public class MainActivity extends Activity implements StationAdapter.StationClickListener, QualityDialog.QualityHandler
{
    private Station station;
    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView stationsView = (RecyclerView) findViewById(R.id.station_grid);
        stationsView.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.stations_columns)));
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        //TODO: check shadow
//        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable(
//                (NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));
        // Start dragging after long press
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(750);
        stationsView.setAdapter(mRecyclerViewDragDropManager.createWrappedAdapter(new StationAdapter(this, this)));
        mRecyclerViewDragDropManager.attachRecyclerView(stationsView);
    }

    @Override
    public void onClick(final Station station)
    {
        this.station = station;
        QualityDialog.getQuality(this, this);
    }

    @Override
    public void onQualitySelected(final Quality quality)
    {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra(PlayerService.STATION_KEY, station.name());
        serviceIntent.putExtra(PlayerService.QUALITY_KEY, quality.name());
        startService(serviceIntent);
        startActivity(new Intent(this, PlayerActivity.class));
    }

    @Override
    protected void onPause()
    {
        mRecyclerViewDragDropManager.cancelDrag();
        super.onPause();
    }
}
