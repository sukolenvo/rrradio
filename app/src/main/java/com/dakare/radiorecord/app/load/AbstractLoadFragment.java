package com.dakare.radiorecord.app.load;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.load.loader.CategoryLoadListener;
import com.dakare.radiorecord.app.load.loader.CategoryLoader;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;

public abstract class AbstractLoadFragment<T extends RecyclerView.ViewHolder, K> extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, CategoryLoadListener<K> {

    private View emptyView;
    @Getter(AccessLevel.PROTECTED)
    private volatile boolean destroyed;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_load, null);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setRefreshing(getAdapter().getItemCount() == 0);
        emptyView = view.findViewById(R.id.list_empty_stub);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.list_divider});
        int decoratorId = attributes.getResourceId(0, 0);
        attributes.recycle();
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources().getDrawable(decoratorId), false));
        recyclerView.setAdapter(getAdapter());
        return view;
    }

    protected abstract AbstractLoadAdapter<T, K> getAdapter();

    @Override
    public void onResume() {
        super.onResume();
        if (swipeContainer.isRefreshing()) {
            loadData(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyed = true;
        getCategoryLoader().cancel();
    }

    protected abstract CategoryLoader<K> getCategoryLoader();

    @Override
    public void onRefresh() {
        loadData(true);
    }

    private void loadData(final boolean forceReload) {
        if (forceReload) {
            getCategoryLoader().clearCache();
        }
        getCategoryLoader().load(this);
    }

    protected void onLoaded(List<K> result) {
        getAdapter().setItems(result);
        getAdapter().notifyDataSetChanged();
        getView().findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    @Override
    public void onCategoryLoaded(List<K> result) {

        if (isResumed()) {
            if (result != null && !result.isEmpty()) {
                onLoaded(result);
            } else {
                getView().findViewById(R.id.recycler_view).setVisibility(View.GONE);
                Toast.makeText(RecordApplication.getInstance(), R.string.error_load_category, Toast.LENGTH_LONG).show();
                emptyView.setVisibility(View.VISIBLE);
            }
            swipeContainer.setRefreshing(false);
        }
    }
}
