package com.dakare.radiorecord.app.load;

import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.load.loader.CategoryLoadListener;
import com.dakare.radiorecord.app.load.loader.CategoryLoader;
import com.dakare.radiorecord.app.load.loader.CategoryResponse;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import lombok.AccessLevel;
import lombok.Getter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class AbstractLoadFragment<T extends RecyclerView.ViewHolder, K> extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, CategoryLoadListener<K> {

    private static final DateFormat DATE_FORMAT = SimpleDateFormat.getDateTimeInstance();

    private View emptyView;
    @Getter(AccessLevel.PROTECTED)
    private volatile boolean destroyed;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_load, null);
        swipeContainer = view.findViewById(R.id.swipe_container);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setRefreshing(getAdapter().getItemCount() == 0);
        emptyView = view.findViewById(R.id.list_empty_stub);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
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
    public void onCategoryLoaded(CategoryResponse<K> result) {
        if (isResumed()) {
            if (!result.getData().isEmpty()) {
                if (result.isCache() && result.getFrom() != null) {
                    Toast.makeText(getContext(),
                            getString(R.string.message_loaded_from_cache, DATE_FORMAT.format(result.getFrom())),
                            Toast.LENGTH_SHORT).show();
                }
                onLoaded(result.getData());
            } else {
                getView().findViewById(R.id.recycler_view).setVisibility(View.GONE);
                Toast.makeText(RecordApplication.getInstance(), R.string.error_load_category, Toast.LENGTH_LONG).show();
                emptyView.setVisibility(View.VISIBLE);
            }
            swipeContainer.setRefreshing(false);
        }
    }
}
