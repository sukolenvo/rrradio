package com.dakare.radiorecord.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.dakare.radiorecord.app.R;
import lombok.Getter;

public class DownloadImageView extends ImageView {

    private static final float FILLING = 0.9f;

    @Getter
    private int progress = 0;
    private RectF borders = new RectF();
    private Paint paintCommon;
    private Paint paintProgress = new Paint();
    private Paint paintText = new Paint();
    private Rect textBounds = new Rect();
    private int primaryColor;

    public DownloadImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DownloadImageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DownloadImageView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DownloadImageView, 0, 0);
        int secondaryColor;
        try {
            primaryColor = ta.getColor(R.styleable.DownloadImageView_primaryColor, 0);
            secondaryColor = ta.getColor(R.styleable.DownloadImageView_secondaryColor, 0);
        } finally {
            ta.recycle();
        }
        paintProgress.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.progress_stroke));
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setColor(primaryColor);
        paintProgress.setAntiAlias(true);
        paintCommon = new Paint(paintProgress);
        paintCommon.setColor(secondaryColor);
        paintText.setTextSize(getResources().getDimensionPixelSize(R.dimen.progress_textsize));
        paintText.setColor(paintProgress.getColor());
        paintText.setAntiAlias(true);
        paintText.getTextBounds(getStatus(), 0, getStatus().length(), textBounds);
    }

    private String getStatus() {
        return progress + "%";
    }

    public void setProgress(final int progress) {
        if (this.progress != progress) {
            this.progress = progress;
            paintText.getTextBounds(getStatus(), 0, getStatus().length(), textBounds);
            invalidate();
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawArc(borders, 0, 360, false, paintCommon);
        canvas.drawArc(borders, -90, 360 * progress / 100.f, false, paintProgress);
        canvas.drawText(getStatus(), borders.centerX() - textBounds.width() / 2, borders.centerY() + textBounds.height() / 2, paintText);
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;
        int height = bottom - top;
        float diagonal = Math.max(width, height) * FILLING;
        float topB = (height - diagonal) / 2;
        float leftB = (width - diagonal) / 2;
        borders.set(leftB, topB, width - leftB, height - topB);
    }
}
