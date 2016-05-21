package com.dakare.radiorecord.app.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;

public class HistoryActivity extends MenuActivity implements HistoryFragmentMediator
{

    private BreadcrumbManager breadcrumbManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initToolbar();
        getSupportActionBar().setTitle("");
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StationSelectFragment())
                    .addToBackStack(null)
                    .commit();
        }
        breadcrumbManager = new BreadcrumbManager(getMyToolbar(), this);
        if (savedInstanceState != null)
        {
            breadcrumbManager.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onStationSelected(final Station station)
    {
        breadcrumbManager.onSelectLevel2(station.getName());
        Fragment fragment = new HistoryDateSelectFragment();
        Bundle args = new Bundle();
        args.putString(HistoryDateSelectFragment.STATION_KEY, station.name());
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void moveBack(final int level)
    {
        for (int i = level; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
        {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDateSelected(final Station station, final String date)
    {
        breadcrumbManager.onSelectLevel3(date);
        Fragment fragment = new HistoryMusicSelectFragment();
        Bundle args = new Bundle();
        args.putString(HistoryMusicSelectFragment.STATION_KEY, station.name());
        args.putString(HistoryMusicSelectFragment.DATE_KEY, date);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
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
            breadcrumbManager.setLevel(getSupportFragmentManager().getBackStackEntryCount() - 1);
        } else
        {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        breadcrumbManager.saveState(outState);
    }
}
