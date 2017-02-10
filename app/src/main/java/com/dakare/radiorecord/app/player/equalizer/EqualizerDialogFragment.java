package com.dakare.radiorecord.app.player.equalizer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;

public class EqualizerDialogFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            return new EqualizerDialog(getContext());
        } catch (RuntimeException e) {
            Crashlytics.logException(e);
            Toast.makeText(getContext(), R.string.audio_effect_error, Toast.LENGTH_LONG).show();
            PreferenceManager.getInstance(getContext()).setEqSettings(false);
        }
        dismiss();
        return super.onCreateDialog(savedInstanceState);
    }
}
