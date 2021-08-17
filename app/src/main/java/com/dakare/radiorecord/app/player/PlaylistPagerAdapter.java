package com.dakare.radiorecord.app.player;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerService;

import java.util.List;

public class PlaylistPagerAdapter extends FragmentPagerAdapter implements SharedPreferences.OnSharedPreferenceChangeListener,
        ViewPager.OnPageChangeListener {

    private List<PlaylistItem> lastPlaylist;
    private final ViewPager viewPager;
    private final Context context;
    private final PreferenceManager instance;

    public PlaylistPagerAdapter(FragmentManager fm, ViewPager viewPager) {
        super(fm);
        context = RecordApplication.getInstance();
        instance = PreferenceManager.getInstance(context);
        lastPlaylist = instance.getLastPlaylist();
        instance.registerChangeListener(this);
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public int getCount() {
        return lastPlaylist.size();
    }

    @Override
    public Fragment getItem(int position) {
        PlaylistItem item = lastPlaylist.get(position);
        Bundle arguments = new Bundle();
        arguments.putParcelable(PlaylistItemFragment.PLAYLIST_ITEM, item);
        arguments.putInt(PlaylistItemFragment.POSITION, position);
        PlaylistItemFragment playlistItemFragment = new PlaylistItemFragment();
        playlistItemFragment.setArguments(arguments);
        return playlistItemFragment;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceManager.LAST_PLAYLIST_POSITION_KEY.equals(key)) {
            viewPager.setCurrentItem(sharedPreferences.getInt(key, 0));
        } else if (PreferenceManager.LAST_PLAYLIST_KEY.equals(key)) {
            lastPlaylist = instance.getLastPlaylist();
            notifyDataSetChanged();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (instance.getLastPosition() != position) {
            Intent serviceIntent = new Intent(context, PlayerService.class);
            serviceIntent.putExtra(PlayerService.POSITION_KEY, position);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
