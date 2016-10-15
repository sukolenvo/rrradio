package com.dakare.radiorecord.app.load.selection;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.load.AbstractLoadFragment;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractSelectionFragment<T extends RecyclerView.ViewHolder, K> extends AbstractLoadFragment<T, K>
        implements AbstractSelectionAdapter.PermissionProvider {

    @Getter(AccessLevel.PROTECTED)
    private SelectionManager selectionManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectionManager = new SelectionManager((AppCompatActivity) getActivity(), new SelectionCallback());
    }

    @Override
    protected abstract AbstractSelectionAdapter<T, K> getAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null) {
            selectionManager.restoreState(savedInstanceState);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        selectionManager.saveState(outState);
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (getActivity() != null && getAdapter().getItemCount() > 0 && PreferenceManager.getInstance(getContext()).showLoadHint()) {
            showHint();
        }
    }

    private void showHint() {
        final View image = getActivity().findViewById(R.id.hint);
        image.setVisibility(View.VISIBLE);
        image.setBackgroundResource(R.drawable.hint_load);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setVisibility(View.GONE);
                PreferenceManager.getInstance(getContext()).hideLoadHint();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (!getAdapter().onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getAdapter().getItemCount() > 0 && PreferenceManager.getInstance(getContext()).showLoadHint()) {
            showHint();
        }
    }
}
