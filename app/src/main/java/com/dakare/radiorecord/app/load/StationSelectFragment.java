package com.dakare.radiorecord.app.load;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dakare.radiorecord.app.GridDecorator;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.StationClickListener;
import com.dakare.radiorecord.app.load.history.HistoryStationSelectAdapter;
import com.dakare.radiorecord.app.station.DynamicStation;

public class StationSelectFragment extends Fragment implements StationClickListener {

    private StationClickListener mediator;

    @Override
    public void onDetach() {
        super.onDetach();
        mediator = null;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mediator = (StationClickListener) context;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_select, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        TypedArray attributes = getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.main_separator_drawable});
        int decoratorId = attributes.getResourceId(0, 0);
        attributes.recycle();
        recyclerView.addItemDecoration(new GridDecorator(
                2, getResources().getInteger(R.integer.stations_columns), getResources().getDrawable(decoratorId)));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.stations_columns)));
        recyclerView.setAdapter(new HistoryStationSelectAdapter(getContext(), this));
        return view;
    }

    @Override
    public void onClick(final DynamicStation station) {
        if (mediator != null) {
            mediator.onClick(station);
        }
    }
}
