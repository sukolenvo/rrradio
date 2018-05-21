package com.dakare.radiorecord.app;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Progress displayer.
 */
public class ProgressView extends RelativeLayout {

    private ProgressBar progressBar;
    private View loadStatusView;
    private RelativeLayout emptyView;
    private TextView status;
    private final LayoutInflater lInflater;

    /**
     * Constructor.
     *
     * @param context Activity context.
     */
    public ProgressView(final Context context) {
        super(context);
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initialize();
    }

    /**
     * Constructor.
     *
     * @param context Activity context.
     * @param attrs   Attributes.
     */
    public ProgressView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initialize();
    }

    /**
     * Constructor.
     *
     * @param context  Activity context.
     * @param attrs    Attributes.
     * @param defStyle Theme.
     */
    public ProgressView(final Context context, final AttributeSet attrs,
                        final int defStyle) {
        super(context, attrs, defStyle);
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initialize();
    }

    /**
     * Displays progress view.
     */
    public void showProgress() {
        loadStatusView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.INVISIBLE);
    }

    /**
     * Hides progress view.
     */
    public void hideProgress() {
        loadStatusView.setVisibility(View.INVISIBLE);
        status.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Set status of progress.
     *
     * @param text text of status.
     */
    public void setStatus(final String text) {
        status.setText(text);
    }

    /**
     * Displays empty stub.
     */
    public void showEmptyView() {
        loadStatusView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        status.setVisibility(View.INVISIBLE);
    }

    public void hideEmptyView() {
        emptyView.setVisibility(View.INVISIBLE);
    }

    /**
     * Change empty view to custom TextView.
     *
     * @param text text of TextView
     */
    public void setEmptyViewText(final String text) {
        emptyView.findViewById(R.id.stub_image).setVisibility(View.GONE);
        emptyView.findViewById(R.id.empty_view_text).setVisibility(View.GONE);
        TextView textView = emptyView
                .findViewById(R.id.custom_empty_view_text);
        textView.setText(text);
        textView.setVisibility(View.VISIBLE);
    }

    private void initialize() {
        loadStatusView = lInflater.inflate(R.layout.view_progress, this);
        emptyView = loadStatusView
                .findViewById(R.id.list_empty_stub);
        progressBar = loadStatusView
                .findViewById(R.id.loading_progress);
        status = loadStatusView
                .findViewById(R.id.view_status_message);
    }
}
