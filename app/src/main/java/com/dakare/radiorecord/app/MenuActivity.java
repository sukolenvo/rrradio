package com.dakare.radiorecord.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import com.dakare.radiorecord.app.settings.SettingsActivity;

public class MenuActivity extends AppCompatActivity
{
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawer;

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSettings(final View view)
    {
        startActivity(new Intent(this, SettingsActivity.class));
        closeMenu();
    }

    public void sendFeedback(final View view)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        try
        {
            startActivity(intent);
        } catch (ActivityNotFoundException e)
        {
            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        }
    }


    @Override
    public void onBackPressed()
    {
        if (drawer.isDrawerOpen(Gravity.LEFT))
        {
            closeMenu();
        } else
        {
            super.onBackPressed();
        }
    }

    private void closeMenu()
    {
        drawer.closeDrawer(Gravity.LEFT);
    }
}
