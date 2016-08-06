package com.dakare.radiorecord.app.load.section;

import com.dakare.radiorecord.app.R;

public class MegamixActivity extends AbstractSectionActivity {
    @Override
    protected String getActionbarTitle() {
        return getString(R.string.menu_megamix_text);
    }

    @Override
    protected String getSectionName() {
        return "Record Megamix";
    }

    @Override
    protected int getMenuContainer() {
        return R.id.menu_megamix_container;
    }
}
