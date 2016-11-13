package com.dakare.radiorecord.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import lombok.AllArgsConstructor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EqualizerImage extends ImageView {

    private static final int POINT_CLICK_RANGE = 40;

    private int width;
    private int height;
    private int left;
    private int top;
    private float fieldTop;
    private float fieldLeft;
    private EqualizerSettings equalizerSettings;
    private PreferenceManager instance;
    private final Paint backgroundPaint;
    private final Paint backgroundDarkerPaint;
    private int editIndex = -1;
    private DecimalFormat numberFormat;
    private Paint paintText;
    private Paint gridPaint;
    private int lastY;
    private Paint linePaint;
    private Paint pointPaint;
    private Paint pointMainPaint;
    private List<Label> labels = new ArrayList<>(10);
    private List<Point> points = new ArrayList<>(5);
    private float xStep;
    private float yStep;

    public EqualizerImage(Context context) {
        super(context);
    }

    public EqualizerImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualizerImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EqualizerImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(0x18, 0x90, 0xf3));
        backgroundDarkerPaint = new Paint();
        backgroundDarkerPaint.setColor(Color.rgb(0x11, 0x66, 0xd7));
        paintText = new Paint();
        paintText.setTextSize(getResources().getDimensionPixelSize(R.dimen.eq_view_textsize));
        paintText.setAntiAlias(true);
        paintText.setColor(Color.BLACK);
        gridPaint = new Paint();
        gridPaint.setColor(Color.rgb(0x01, 0xd5, 0xf2));
        gridPaint.setStrokeWidth(3);
        gridPaint.setStyle(Paint.Style.FILL);
        gridPaint.setAntiAlias(true);
        linePaint = new Paint();
        int lineColor = Color.WHITE;
        linePaint.setColor(lineColor);
        linePaint.setAlpha(120);
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
        numberFormat = new DecimalFormat("+0;-0");
        numberFormat.setMaximumFractionDigits(1);
        pointPaint = new Paint();
        pointPaint.setColor(lineColor);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
        pointPaint.setAlpha(60);
        pointMainPaint = new Paint();
        pointMainPaint.setColor(lineColor);
        pointMainPaint.setStrokeWidth(5);
        pointMainPaint.setStyle(Paint.Style.FILL);
        pointMainPaint.setAntiAlias(true);
        setClickable(true);
        instance = PreferenceManager.getInstance(getContext());
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawRect(fieldLeft, fieldTop, left + width, top + height, backgroundPaint);
        if (points.isEmpty()) {
            return;
        }
        Path path = new Path();
        path.reset();
        path.moveTo(fieldLeft, points.get(0).y);
        for (Point point : points) {
            path.lineTo(point.x, point.y);
        }
        path.lineTo(left + width, points.get(points.size() - 1).y);
        path.lineTo(left + width, top + height);
        path.lineTo(fieldLeft, top + height);
        canvas.drawPath(path, backgroundDarkerPaint);
        drawGrid(canvas);
        drawLabels(canvas);
        drawPoints(canvas);
    }

    private void drawGrid(final Canvas canvas) {
        for (int i = 2; i < 6; i++) {
            canvas.drawLine(left + xStep * i, fieldTop, left + xStep * i, top + height, gridPaint);
        }
        for (int i = 2; i < 7; i++) {
            canvas.drawLine(fieldLeft, top + yStep * i, left + width, top + yStep * i, gridPaint);
        }
    }

    private void drawPoints(final Canvas canvas) {
        for (Point point : points) {
            canvas.drawOval(new RectF(point.x - 10, point.y - 10, point.x + 10, point.y + 10), pointMainPaint);
            canvas.drawOval(new RectF(point.x - 28, point.y - 28, point.x + 28, point.y + 28), pointPaint);
        }
        for (int i = 1; i < points.size(); i++) {
            canvas.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y, pointMainPaint);
        }
    }

    private void drawLabels(final Canvas canvas) {
        for (Label label : labels) {
            canvas.drawText(label.text, label.x, label.y, label.paint);
        }
    }

    public void updateSettings(final EqualizerSettings equalizerSettings) {
        this.equalizerSettings = equalizerSettings;
        invalidateView();
        invalidate();
    }

    public void updateBands(final Equalizer equalizer) {
        for (int i = 0; i < equalizer.getNumberOfBands(); i++) {
            equalizerSettings.getLevels()[i] = equalizer.getBandLevel((short) i);
        }
        equalizerSettings.setPreset(equalizer.getPresetName(equalizer.getCurrentPreset()));
        instance.setEqSettings(equalizerSettings);
        invalidateView();
        invalidate();
    }

    private void invalidateView() {
        if (equalizerSettings != null && width > 0 && height > 0) {
            labels.clear();
            points.clear();
            calculateLables();
            calculatePoints();
        }
    }

    private void calculateLables() {
        Rect bounds = new Rect();
        List<String> tests = Arrays.asList("BASS", "LOW", "MID", "UPPER", "HIGH");
        for (int i = 0; i < 5; i++) {
            String bandString = tests.get(i);
            paintText.getTextBounds(bandString, 0, bandString.length(), bounds);
            labels.add(new Label((int) (left + (i + 1.5) * xStep - bounds.width() / 2), (int) (top + yStep / 2), bandString, paintText));
        }
        int low = equalizerSettings.getRange()[0] / 100;
        int upp = equalizerSettings.getRange()[1] / 100;
        float step = (upp - low) / 6.f;
        for (int i = 0; i < 7; i++) {
            String levelString = getLevelString(upp - step * i);
            paintText.getTextBounds(levelString, 0, levelString.length(), bounds);
            labels.add(new Label((int) (left + xStep / 2 - bounds.width() / 2), (int) (fieldTop + yStep * i + bounds.height() / 2), levelString, paintText));
        }
    }

    private String getLevelString(final double level) {
        return numberFormat.format(level);
    }

    private void calculatePoints() {
        for (int i = 0; i < equalizerSettings.getLevels().length; i++) {
            int x = (int) (left + (1.5 + i) * xStep);
            int y = (int) (top + height - yStep * (equalizerSettings.getLevels()[i] - equalizerSettings.getRange()[0]) / equalizerSettings.getLevelRange() * 6);
            points.add(new Point(x, y));
        }
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left - getPaddingTop() - getPaddingBottom();
        height = bottom - top - getPaddingLeft() - getPaddingRight();
        xStep = width / 6.f;
        yStep = height / 7.f;
        this.top = getPaddingTop();
        this.left = getPaddingLeft();
        fieldTop = this.top + yStep;
        fieldLeft = this.left + xStep;
        invalidateView();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (Point point : points) {
                    if (Math.abs(point.x - x) < POINT_CLICK_RANGE &&
                            Math.abs(point.y - y) < POINT_CLICK_RANGE &&
                            range(x, y, point.x, point.y) < POINT_CLICK_RANGE) {
                        editIndex = points.indexOf(point);
                        lastY = point.y;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    }
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (editIndex == -1) {
                    return false;
                }
                float correctedY = Math.min(top + height, Math.max(y, fieldTop));
                points.get(editIndex).y = (int) correctedY;
                if (editIndex == 0) {
                    invalidate((int) fieldLeft, top, points.get(1).x, top + height);
                } else if (editIndex == points.size() - 1) {
                    invalidate(points.get(editIndex - 1).x, top, left + width, top + height);
                } else  {
                    invalidate(points.get(editIndex - 1).x, top, points.get(editIndex + 1).x, top + height);
                }
                if (Math.abs(lastY - correctedY) > yStep / 5) {
                    equalizerSettings.getLevels()[editIndex] = (int) (equalizerSettings.getRange()[0] + (top + height - correctedY) / yStep / 6 * equalizerSettings.getLevelRange());
                    instance.setEqSettings(equalizerSettings);
                    lastY = (int) correctedY;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                editIndex = -1;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
                //Nothing to do
        }
        return true;
    }

    private double range(final float x1, final float y1, final float x2, final float y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    @AllArgsConstructor(suppressConstructorProperties = true)
    private static class Label {
        private int x;
        private int y;
        private String text;
        private Paint paint;
    }
}
