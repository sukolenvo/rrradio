package com.dakare.radiorecord.app.utils;

import android.util.Log;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

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
