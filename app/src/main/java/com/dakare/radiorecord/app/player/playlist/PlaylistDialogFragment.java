package com.dakare.radiorecord.app.player.playlist;

import android.app.Dialog;
import android.os.Bundle;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import com.dakare.radiorecord.app.utils.StateLoseAppCompatDialogFragment;

public class PlaylistDialogFragment extends StateLoseAppCompatDialogFragment implements AbstractSelectionAdapter.PermissionProvider {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new PlaylistDialog(getContext(), this);
    }
}
