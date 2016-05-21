package com.dakare.radiorecord.app.history;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class AbstractHistoryLoadAdapter<T extends RecyclerView.ViewHolder, K> extends RecyclerView.Adapter<T>
{

    public abstract void setItems(List<K> items);
}
