package com.dakare.radiorecord.app.load.section;

import com.dakare.radiorecord.app.R;

public class HrustalevActivity extends AbstractSectionActivity {
    @Override
    protected String getActionbarTitle() {
        return getString(R.string.menu_hrustalev_text);
    }

    @Override
    protected String getSectionName() {
        return "Кремов и Хрусталев";
    }

    @Override
    protected int getMenuContainer() {
        return R.id.menu_megamix_container;
    }
}
