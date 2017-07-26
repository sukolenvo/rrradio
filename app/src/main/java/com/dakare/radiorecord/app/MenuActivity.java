package com.dakare.radiorecord.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.dakare.radiorecord.app.download.DownloadsActivity;
import com.dakare.radiorecord.app.iap.IapActivity;
import com.dakare.radiorecord.app.load.history.HistoryActivity;
import com.dakare.radiorecord.app.load.section.HrustalevActivity;
import com.dakare.radiorecord.app.load.section.MegamixActivity;
import com.dakare.radiorecord.app.load.section.SectionNewActivity;
import com.dakare.radiorecord.app.load.section.SuperchartActivity;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.settings.SettingsActivity;
import com.dakare.radiorecord.app.load.top.TopsActivity;
import com.dakare.radiorecord.app.view.theme.ThemeActivity;
import lombok.Getter;

public class MenuActivity extends ThemeActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawer;
    @Getter
    private Toolbar myToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("Open activity")
                    .putContentType(getClass().getSimpleName())
                    .putContentId(getClass().getSimpleName())
                    .putCustomAttribute("theme", PreferenceManager.getInstance(this).getTheme().name())
                    .putCustomAttribute("ads", PreferenceManager.getInstance(this).getShowAd() + ""));
        }
    }

    protected void initToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(mDrawerToggle);
        int container = getMenuContainer();
        if (getMenuContainer() != 0) {
            findViewById(container).setSelected(true);
        }
    }

    protected int getMenuContainer() {
        return 0;
    }

    protected void setTitle(final String title) {
        myToolbar.setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSettings(final View view) {
        startActivity(new Intent(this, SettingsActivity.class));
        closeMenu();
    }

    public void sendFeedback(final View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        }
    }

    public void mainActivity(final View view) {
        if (!(this instanceof MainActivity)) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void historyActivity(final View view) {
        if (!(this instanceof HistoryActivity)) {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void topsActivity(final View view) {
        if (!(this instanceof TopsActivity)) {
            Intent intent = new Intent(this, TopsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void downloadsActivity(final View view) {
        if (!(this instanceof DownloadsActivity)) {
            Intent intent = new Intent(this, DownloadsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void playerActivity(final View view) {
        if (!(this instanceof PlayerActivity)) {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void sectionNewActivity(final View view) {
        if (!(this instanceof SectionNewActivity)) {
            Intent intent = new Intent(this, SectionNewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void superchartActivity(final View view) {
        if (!(this instanceof SuperchartActivity)) {
            Intent intent = new Intent(this, SuperchartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void megamixActivity(final View view) {
        if (!(this instanceof MegamixActivity)) {
            Intent intent = new Intent(this, MegamixActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    public void hrustalevActivity(final View view) {
        if (!(this instanceof HrustalevActivity)) {
            Intent intent = new Intent(this, HrustalevActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        closeMenu();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void closeMenu() {
        drawer.closeDrawer(Gravity.LEFT);
    }
}
