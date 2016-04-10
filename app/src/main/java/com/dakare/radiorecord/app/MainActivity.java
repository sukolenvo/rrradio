package com.dakare.radiorecord.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.service.PlayerService;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, QualityDialog.QualityHandler
{
    private Station station;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView stationsView = (GridView) findViewById(R.id.station_grid);
        stationsView.setAdapter(new StationAdapter(this));
        stationsView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id)
    {
        station = Station.values()[position];
        QualityDialog.getQuality(this, this);
    }

    @Override
    public void onQualitySelected(final String quality)
    {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra(PlayerService.STATION_KEY, station.name());
        serviceIntent.putExtra(PlayerService.QUALITY_KEY, quality);
        startService(serviceIntent);
        startActivity(new Intent(this, PlayerActivity.class));
    }
}
