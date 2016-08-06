package com.dakare.radiorecord.app.load.history;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.load.StationSelectFragment;

import java.util.List;

public class HistoryActivity extends MenuActivity implements HistoryFragmentMediator {

    private BreadcrumbManager breadcrumbManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initToolbar();
        getSupportActionBar().setTitle("");
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StationSelectFragment())
                    .addToBackStack(null)
                    .commit();
        }
        breadcrumbManager = new BreadcrumbManager(getMyToolbar(), this);
        if (savedInstanceState != null) {
            breadcrumbManager.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onClick(final Station station) {
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
    public void moveBack(final int level) {
        for (int i = level; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onDateSelected(final Station station, final String date) {
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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            breadcrumbManager.setLevel(getSupportFragmentManager().getBackStackEntryCount() - 1);
        } else {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        breadcrumbManager.saveState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(final View view, final Menu menu) {
        PreferenceManager instance = PreferenceManager.getInstance(this);
        if (instance.isHistoryShowAll()) {
            menu.findItem(R.id.show_items).setVisible(false);
            menu.findItem(R.id.hide_items).setVisible(true);
        } else {
            menu.findItem(R.id.show_items).setVisible(true);
            menu.findItem(R.id.hide_items).setVisible(false);
        }
        if (instance.isHistorySortOld()) {
            menu.findItem(R.id.sort_up).setVisible(true);
            menu.findItem(R.id.sort_down).setVisible(false);
        } else {
            menu.findItem(R.id.sort_up).setVisible(false);
            menu.findItem(R.id.sort_down).setVisible(true);
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_items:
                PreferenceManager.getInstance(this).setHistoryShowAll(true);
                break;
            case R.id.hide_items:
                PreferenceManager.getInstance(this).setHistoryShowAll(false);
                break;
            case R.id.sort_up:
                PreferenceManager.getInstance(this).setHistorySortOld(false);
                break;
            case R.id.sort_down:
                PreferenceManager.getInstance(this).setHistorySortOld(true);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof HistoryMusicSelectFragment) {
                ((HistoryMusicSelectFragment) fragment).onPreferenceChanged();
            }
        }
        return false;
    }

    @Override
    protected int getMenuContainer() {
        return R.id.menu_history_container;
    }
}
