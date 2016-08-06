package com.dakare.radiorecord.app.load.selection;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import lombok.Setter;

@Setter
public abstract class AbstractSelectionCallback implements ActionMode.Callback {

    protected SelectionManager selectionManager;
    protected SelectionManager.SelectionItemCallback selectionItemCallback;

    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(final ActionMode mode) {
        selectionManager.onCancelSelection();
    }
}
