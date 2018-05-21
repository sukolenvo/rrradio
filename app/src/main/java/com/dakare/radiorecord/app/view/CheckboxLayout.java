package com.dakare.radiorecord.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.dakare.radiorecord.app.R;
import lombok.Getter;

public class CheckboxLayout extends FrameLayout implements Checkable, View.OnClickListener {
    @Getter
    private boolean checked;
    private CheckBox checkBox;
    private TextView text;

    public CheckboxLayout(final Context context) {
        super(context);
        init();
    }

    public CheckboxLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
        setText(attrs, 0, 0);
    }

    @TargetApi(11)
    public CheckboxLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setText(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public CheckboxLayout(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        setText(attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_checkbox, this);
        checkBox = findViewById(R.id.checkbox);
        text = findViewById(R.id.text);
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
            checkBox.setChecked(checked);
        }
    }

    @Override
    public void toggle() {
        checked ^= true;
        checkBox.setChecked(checked);
    }

    @Override
    public void onClick(View v) {
        toggle();
    }
}
