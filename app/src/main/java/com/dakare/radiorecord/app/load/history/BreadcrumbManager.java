package com.dakare.radiorecord.app.load.history;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;

public class BreadcrumbManager implements View.OnClickListener {
    private static final String LEVEL_2_KEY = "breadcrumb_level_2";
    private static final String LEVEL_3_KEY = "breadcrumb_level_3";

    private final Context context;
    private final View level2Container;
    private final View level3Container;
    private final TextView viewLevel2;
    private final TextView viewLevel3;
    private final HistoryFragmentMediator historyFragmentMediator;

    public BreadcrumbManager(final Toolbar toolbar, final HistoryFragmentMediator historyFragmentMediator) {
        this.context = toolbar.getContext();
        this.historyFragmentMediator = historyFragmentMediator;
        View container = LayoutInflater.from(context).inflate(R.layout.breadcrumb, null);
        level2Container = container.findViewById(R.id.breadcrumb_level_2_container);
        level3Container = container.findViewById(R.id.breadcrumb_level_3_container);
        container.findViewById(R.id.breadcrumb_level_1).setOnClickListener(this);
        viewLevel2 = container.findViewById(R.id.breadcrumb_level_2);
        viewLevel2.setOnClickListener(this);
        viewLevel3 = container.findViewById(R.id.breadcrumb_level_3);
        toolbar.addView(container);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.breadcrumb_level_1:
                setLevel(1);
                historyFragmentMediator.moveBack(1);
                break;
            case R.id.breadcrumb_level_2:
                setLevel(2);
                historyFragmentMediator.moveBack(2);
                break;
            default:
                throw new AssertionError("Unknown view");
        }
    }

    public void onSelectLevel2(final String title) {
        level2Container.setVisibility(View.VISIBLE);
        viewLevel2.setText(title);
    }

    public void onSelectLevel3(final String title) {
        level3Container.setVisibility(View.VISIBLE);
        viewLevel3.setText(title);
    }

    public void setLevel(final int level) {
        switch (level) {
            case 3:
                level3Container.setVisibility(View.VISIBLE);
                level2Container.setVisibility(View.VISIBLE);
                break;
            case 2:
                level3Container.setVisibility(View.GONE);
                level2Container.setVisibility(View.VISIBLE);
                break;
            case 1:
                level3Container.setVisibility(View.GONE);
                level2Container.setVisibility(View.GONE);
                break;
            default:
                throw new AssertionError("Invalid level " + level);
        }
    }

    public void saveState(final Bundle outState) {
        if (level2Container.getVisibility() == View.VISIBLE) {
            outState.putString(LEVEL_2_KEY, viewLevel2.getText().toString());
            if (level3Container.getVisibility() == View.VISIBLE) {
                outState.putString(LEVEL_3_KEY, viewLevel3.getText().toString());
            }
        }
    }

    public void restoreState(final Bundle state) {
        if (state.containsKey(LEVEL_2_KEY)) {
            level2Container.setVisibility(View.VISIBLE);
            viewLevel2.setText(state.getString(LEVEL_2_KEY));
            if (state.containsKey(LEVEL_3_KEY)) {
                level3Container.setVisibility(View.VISIBLE);
                viewLevel3.setText(state.getString(LEVEL_3_KEY));
            }
        }
    }
}
