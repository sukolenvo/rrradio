package com.dakare.radiorecord.app.load;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dakare.radiorecord.app.ProgressView;
import com.dakare.radiorecord.app.R;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLoadFragment<T extends RecyclerView.ViewHolder, K> extends Fragment implements Runnable
{

    private Handler handler;
    private ProgressView progressView;
    private ArrayList<K> items = new ArrayList<K>();
    @Getter(AccessLevel.PROTECTED)
    private volatile boolean destroyed;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
        {
            items.addAll(restoreItems(savedInstanceState));
        }
        handler = new Handler();
    }

    protected abstract List<K> restoreItems(final Bundle args);

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_load, null);
        progressView = (ProgressView) view.findViewById(R.id.progress_bar);
        progressView.showProgress();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(R.drawable.history_divider), false));
        recyclerView.setAdapter(getAdapter());
        if (items.isEmpty())
        {
            new Thread(this).start();
        } else
        {
            getAdapter().setItems(items);
        }
        return view;
    }

    protected abstract AbstractLoadAdapter<T, K> getAdapter();

    @Override
    public void onResume()
    {
        super.onResume();
        if (!items.isEmpty())
        {
            progressView.hideProgress();
            getView().findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
            getAdapter().setItems(items);
        }
    }

    @Override
    public void run()
    {
        try
        {
            items.addAll(startLoading());
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (isResumed())
                    {
                        progressView.hideProgress();
                        getView().findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
                        getAdapter().setItems(items);
                    }
                }
            });
        } catch (IOException e)
        {
            handler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(getContext(), R.string.error_load_history, Toast.LENGTH_LONG).show();
                    progressView.hideProgress();
                    progressView.showEmptyView();
                }
            });
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        destroyed = true;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (items.size() > 0)
        {
            saveItems(items, outState);
        }
    }

    protected abstract void saveItems(final ArrayList<K> items, final Bundle outState);

    protected void setStatus(final int statusId, final Object ... args)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (getContext() != null)
                {
                    progressView.setStatus(getString(statusId, args));
                }
            }
        });
    }

    protected void sendToast(final String message)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected abstract List<K> startLoading() throws IOException;

}
