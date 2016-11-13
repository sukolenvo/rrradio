package com.dakare.radiorecord.app.player.equalizer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;

public class EqualizerDialogFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new EqualizerDialog(getContext());
    }
}
