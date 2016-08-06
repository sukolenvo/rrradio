package com.dakare.radiorecord.app.load.section;

import com.dakare.radiorecord.app.R;

public class SectionNewActivity extends AbstractSectionActivity {
    @Override
    protected String getActionbarTitle() {
        return getString(R.string.menu_new_text);
    }

    @Override
    protected String getSectionName() {
        return "Свежаки";
    }


    @Override
    protected int getMenuContainer() {
        return R.id.menu_new_container;
    }
}
