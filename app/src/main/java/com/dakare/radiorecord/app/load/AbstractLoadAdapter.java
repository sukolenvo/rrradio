package com.dakare.radiorecord.app.load;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class AbstractLoadAdapter<T extends RecyclerView.ViewHolder, K> extends RecyclerView.Adapter<T> {

    public abstract void setItems(List<K> items);
}
