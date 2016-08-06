package com.dakare.radiorecord.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import lombok.Getter;

public class CheckboxLayout extends LinearLayout implements Checkable, View.OnClickListener {
    @Getter
    private boolean checked;
    private ImageView imageView;
    private TextView text;

    public CheckboxLayout(Context context) {
        super(context);
        init();
    }

    public CheckboxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setText(attrs, 0, 0);
    }

    @TargetApi(11)
    public CheckboxLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setText(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public CheckboxLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        setText(attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int halfPadding = getResources().getDimensionPixelOffset(R.dimen.half_padding);
        setPadding(getResources().getDimensionPixelOffset(R.dimen.one_and_half_padding), halfPadding, halfPadding, halfPadding);
        imageView = new ImageView(getContext());
        int iconSize = getResources().getDimensionPixelSize(R.dimen.selector_icon_size);
        LayoutParams iconParams = new LayoutParams(iconSize, iconSize);
        iconParams.gravity = Gravity.CENTER_VERTICAL;
        imageView.setLayoutParams(iconParams);
        imageView.setImageResource(checked ? R.drawable.checked : R.drawable.unchecked);
        addView(imageView);
        text = new TextView(getContext());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        text.setLayoutParams(params);
        text.setTextSize(14.f);
        text.setPadding(halfPadding, 0, 0, 0);
        addView(text);
        setOnClickListener(this);

    }

    private void setText(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.dakare, defStyleAttr, defStyleRes);
        text.setText(array.getString(R.styleable.dakare_text));
    }


    @Override
    public void setChecked(boolean checked) {
        if (this.checked ^ checked) {
            this.checked = checked;
            imageView.setImageResource(checked ? R.drawable.checked : R.drawable.unchecked);
        }
    }

    @Override
    public void toggle() {
        checked ^= true;
        imageView.setImageResource(checked ? R.drawable.checked : R.drawable.unchecked);
    }

    @Override
    public void onClick(View v) {
        toggle();
    }
}
