package com.dakare.radiorecord.app.load.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import com.dakare.radiorecord.app.load.AbstractLoadFragment;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractHistoryMediatorFragment<T extends RecyclerView.ViewHolder, K> extends AbstractLoadFragment<T, K> {

    @Getter(AccessLevel.PROTECTED)
    private HistoryFragmentMediator mediator;

    @Override
    public void onDetach() {
        super.onDetach();
        mediator = null;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mediator = (HistoryFragmentMediator) context;
    }
}
