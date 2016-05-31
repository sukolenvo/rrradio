package com.dakare.radiorecord.app.load.section;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.dakare.radiorecord.app.MenuActivity;
import com.dakare.radiorecord.app.R;

public abstract class AbstractSectionActivity extends MenuActivity
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initToolbar();
        getSupportActionBar().setTitle(getActionbarTitle());
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            Fragment fragment = new SectionFragment();
            Bundle args = new Bundle();
            args.putString(SectionFragment.CATEGORY_KEY, getSectionName());
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    protected abstract String getActionbarTitle();

    protected abstract String getSectionName();

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
