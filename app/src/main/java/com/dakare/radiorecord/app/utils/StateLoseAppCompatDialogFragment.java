package com.dakare.radiorecord.app.utils;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;

public class StateLoseAppCompatDialogFragment extends AppCompatDialogFragment {

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException e) {
            Log.i("StateLoseDialog", "Ignoring after onSaveInstanceState exception", e);
        }
    }
}
