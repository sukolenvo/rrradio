package com.dakare.radiorecord.app.load.section;

import com.dakare.radiorecord.app.R;

public class SuperchartActivity extends AbstractSectionActivity {
    @Override
    protected String getActionbarTitle() {
        return getString(R.string.menu_superchart_text);
    }

    @Override
    protected String getSectionName() {
        return "Superchart";
    }


    @Override
    protected int getMenuContainer() {
        return R.id.menu_superchart_container;
    }
}
