package com.dakare.radiorecord.app.load.selection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.load.AbstractLoadAdapter;
import com.dakare.radiorecord.app.load.AbstractLoadFragment;
import com.dakare.radiorecord.app.load.section.SectionAdapter;
import com.dakare.radiorecord.app.load.section.SectionMusicItem;
import lombok.AccessLevel;
import lombok.Getter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSelectionFragment<T extends RecyclerView.ViewHolder, K> extends AbstractLoadFragment<T, K> implements AbstractSelectionAdapter.PermissionProvider
{
    @Getter(AccessLevel.PROTECTED)
    private SelectionManager selectionManager;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        selectionManager = new SelectionManager((AppCompatActivity) getActivity(), new SelectionCallback());
    }

    @Override
    protected abstract AbstractSelectionAdapter<T, K> getAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (savedInstanceState != null)
        {
            selectionManager.restoreState(savedInstanceState);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        selectionManager.saveState(outState);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults)
    {
        if (!getAdapter().onRequestPermissionsResult(requestCode, permissions, grantResults))
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
