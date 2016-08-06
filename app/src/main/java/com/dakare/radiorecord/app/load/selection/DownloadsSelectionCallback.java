package com.dakare.radiorecord.app.load.selection;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import com.dakare.radiorecord.app.R;
import lombok.Setter;

public class DownloadsSelectionCallback extends AbstractSelectionCallback {

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        mode.getMenuInflater().inflate(R.menu.downloads_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove:
                selectionItemCallback.removeSelected();
                return true;
            case R.id.select_all:
                selectionManager.selectAll();
                return true;
            case R.id.select_none:
                selectionManager.clearSelection();
                return true;
            case R.id.play:
                selectionItemCallback.playSelected();
                return true;
            default:
                return false;
        }
    }

}
