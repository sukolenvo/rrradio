package com.dakare.radiorecord.app.load.top;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.StationClickListener;
import com.dakare.radiorecord.app.load.StationSelectFragment;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;

import java.util.ArrayList;
import java.util.List;

public class TopsActivity extends MenuActivity implements TopsFragmentMediator
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initToolbar();
        getSupportActionBar().setTitle(getString(R.string.menu_top_text));
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StationSelectFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onClick(final Station station)
    {
        Fragment fragment = new TopsMusicFragment();
        Bundle args = new Bundle();
        args.putString(TopsMusicFragment.STATION_KEY, station.name());
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed()
    {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
        {
            getSupportFragmentManager().popBackStack();
        } else
        {
            finish();
        }
    }

    @Override
    public void onItemsSelected(final List<TopsMusicItem> items, final int position, final Station station)
    {
        Intent intent = new Intent(TopsActivity.this, PlayerService.class);
        ArrayList<PlaylistItem> playlist = new ArrayList<PlaylistItem>(items.size());
        for (TopsMusicItem item : items)
        {
            playlist.add(new PlaylistItem(station, item));
        }
        intent.putExtra(PlayerService.PLAYLIST_KEY, playlist);
        intent.putExtra(PlayerService.POSITION_KEY, position);
        startService(intent);
        Intent intentActivity = new Intent(this, PlayerActivity.class);
        intentActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentActivity);
    }

    @Override
    protected int getMenuContainer()
    {
        return R.id.menu_top_container;
    }
}
