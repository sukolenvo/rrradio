package com.dakare.radiorecord.app.settings;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.dakare.radiorecord.app.AbstractDialog;
import com.dakare.radiorecord.app.MainActivity;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.quality.Quality;
import com.dakare.radiorecord.app.quality.QualityAdapter;
import com.dakare.radiorecord.app.view.theme.Theme;

public class SettingsThemeDialog extends AbstractDialog implements AdapterView.OnItemClickListener {

    private ThemeAdapter adapter;

    public SettingsThemeDialog(final Context context) {
        super(context);
        setContentView(R.layout.dialog_settings_theme);
        ListView listView = (ListView) findViewById(R.id.list);
        adapter = new ThemeAdapter(context);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        PreferenceManager.getInstance(getContext()).setTheme(adapter.getItem(position));
        getContext().startActivity(new Intent(getContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
