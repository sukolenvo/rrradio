package com.dakare.radiorecord.app.load.selection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.View;
import com.dakare.radiorecord.app.R;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class SelectionManager implements View.OnLongClickListener, View.OnClickListener {
    public static final int POSITION_UNKNOWN = -1;
    private static final String SELECTING_KEY = "selecting_key";
    private static final String SELECTED_ARRAY_KEY = "selected_key";

    @Getter
    private boolean selecting;
    private SelectionItemCallback callback;
    private final AppCompatActivity activity;
    private final SparseBooleanArray selectedArray = new SparseBooleanArray();
    private ActionMode actionMode;
    private final String title;
    private final AbstractSelectionCallback actionModeCallback;

    public SelectionManager(final AppCompatActivity activity, final AbstractSelectionCallback actionModeCallback) {
        this.activity = activity;
        this.actionModeCallback = actionModeCallback;
        actionModeCallback.setSelectionManager(this);
        title = activity.getString(R.string.selection_title);
    }

    public void setCallback(final SelectionItemCallback callback) {
        this.callback = callback;
        actionModeCallback.setSelectionItemCallback(callback);
    }

    @Override
    public boolean onLongClick(View v) {
        if (selecting) {
            return false;
        } else {
            actionMode = activity.startSupportActionMode(actionModeCallback);
            int position = (Integer) v.getTag();
            selectedArray.put(position, true);
            callback.onSelectionChanged(position);
            updateTitle();
            return selecting = true;
        }
    }

    public void updateTitle() {
        int count = 0;
        for (int i = 0; i < selectedArray.size(); i++) {
            if (selectedArray.valueAt(i)) {
                count++;
            }
        }
        actionMode.setTitle(String.format(title, count, callback.getItemCount()));
    }

    @Override
    public void onClick(final View v) {
        if (selecting) {
            int position = (int) v.getTag();
            selectedArray.put(position, !selectedArray.get(position));
            callback.onSelectionChanged(position);
            updateTitle();
        } else {
            callback.onClick(v);
        }
    }

    public boolean isSelected(final int position) {
        return selectedArray.get(position);
    }

    public void saveState(final Bundle outState) {
        outState.putBoolean(SELECTING_KEY, selecting);
        ArrayList<Integer> values = new ArrayList<>(selectedArray.size());
        for (int i = 0; i < selectedArray.size(); i++) {
            if (selectedArray.valueAt(i)) {
                values.add(selectedArray.keyAt(i));
            }
        }
        outState.putIntegerArrayList(SELECTED_ARRAY_KEY, values);
    }

    public void restoreState(final Bundle state) {
        selecting = state.getBoolean(SELECTING_KEY);
        for (int position : state.getIntegerArrayList(SELECTED_ARRAY_KEY)) {
            selectedArray.put(position, true);
        }
        if (selecting && actionMode == null) {
            actionMode = activity.startSupportActionMode(actionModeCallback);
            updateTitle();
        }
    }


    public void selectAll() {
        for (int i = 0; i < callback.getItemCount(); i++) {
            selectedArray.put(i, true);
        }
        callback.onSelectionChanged(POSITION_UNKNOWN);
        updateTitle();
    }

    public void clearSelection() {
        selectedArray.clear();
        callback.onSelectionChanged(POSITION_UNKNOWN);
        updateTitle();
    }

    public void finishSelection() {
        selecting = false;
        selectedArray.clear();
        actionMode.finish();
    }

    public void onCancelSelection() {
        selecting = false;
        selectedArray.clear();
        callback.onSelectionChanged(POSITION_UNKNOWN);
    }

    public interface SelectionItemCallback extends View.OnClickListener {

        void onSelectionChanged(int position);

        int getItemCount();

        void playSelected();

        void downloadSelected();

        void removeSelected();
    }
}
