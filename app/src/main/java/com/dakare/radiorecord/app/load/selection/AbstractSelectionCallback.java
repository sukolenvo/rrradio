package com.dakare.radiorecord.app.load.selection;

import android.view.Menu;

import androidx.appcompat.view.ActionMode;

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
