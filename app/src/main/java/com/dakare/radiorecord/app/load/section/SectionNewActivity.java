package com.dakare.radiorecord.app.load.section;

import com.dakare.radiorecord.app.R;

public class SectionNewActivity extends AbstractSectionActivity
{
    @Override
    protected String getActionbarTitle()
    {
        return getString(R.string.menu_new_text);
    }

    @Override
    protected String getSectionName()
    {
        return "Свежаки";
    }
}
