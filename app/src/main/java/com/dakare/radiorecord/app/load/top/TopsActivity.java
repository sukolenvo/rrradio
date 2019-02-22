package com.dakare.radiorecord.app.load.top;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.StationClickListener;
import com.dakare.radiorecord.app.load.StationSelectFragment;
import com.dakare.radiorecord.app.station.AbstractStation;

public class TopsActivity extends MenuActivity implements StationClickListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initToolbar();
        getSupportActionBar().setTitle(getString(R.string.menu_top_text));
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StationSelectFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onClick(final AbstractStation station) {
        Fragment fragment = new TopsMusicFragment();
        Bundle args = new Bundle();
        args.putString(TopsMusicFragment.STATION_KEY, station.serialize());
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected int getMenuContainer() {
        return R.id.menu_top_container;
    }
}
