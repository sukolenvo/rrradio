package com.dakare.radiorecord.app.player.playlist;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;

public class PlaylistDialogFragment extends AppCompatDialogFragment implements AbstractSelectionAdapter.PermissionProvider {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new PlaylistDialog(getContext(), this);
    }
}
