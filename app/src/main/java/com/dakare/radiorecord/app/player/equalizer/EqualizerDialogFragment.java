package com.dakare.radiorecord.app.player.equalizer;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.utils.StateLoseAppCompatDialogFragment;

public class EqualizerDialogFragment extends StateLoseAppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            return new EqualizerDialog(getContext());
        } catch (RuntimeException e) {
            Toast.makeText(getContext(), R.string.audio_effect_error, Toast.LENGTH_LONG).show();
            PreferenceManager.getInstance(getContext()).setEqSettings(false);
        }
        dismiss();
        return super.onCreateDialog(savedInstanceState);
    }
}
