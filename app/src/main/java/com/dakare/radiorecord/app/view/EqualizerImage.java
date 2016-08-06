package com.dakare.radiorecord.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import lombok.AllArgsConstructor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class EqualizerImage extends ImageView {

    private static final int POINT_CLICK_RANGE = 40;

    private int width;
    private int height;
    private EqualizerSettings equalizerSettings;
    private Paint paintText;
    private Paint axisPaint;
    private Paint linePaint;
    private Paint pointPaint;
    private Paint mainLabelText;
    private int left;
    private int top;
    private int leftAxis;
    private int bottomAxis;
    private int yStep;
    private int xStep;
    private List<Label> labels = new ArrayList<>(10);
    private List<Path> triangles = new ArrayList<>(2);
    private NumberFormat numberFormat;
    private int xLableHeight;
    private List<Point> points = new ArrayList<>(5);
    private int editIndex = -1;
    private int topBorder;
    private int lastY;
    private PreferenceManager instance;

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
        paintText = new Paint();
        paintText.setTextSize(getResources().getDimensionPixelSize(R.dimen.eq_view_textsize));
        paintText.setAntiAlias(true);
        paintText.setColor(Color.WHITE);
        mainLabelText = new Paint();
        mainLabelText.setTextSize(getResources().getDimensionPixelSize(R.dimen.eq_view_main_textsize));
        mainLabelText.setAntiAlias(true);
        mainLabelText.setColor(Color.WHITE);
        axisPaint = new Paint();
        axisPaint.setColor(Color.WHITE);
        axisPaint.setStrokeWidth(3);
        axisPaint.setStyle(Paint.Style.FILL);
        axisPaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setAlpha(120);
        linePaint.setStrokeWidth(3);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setAntiAlias(true);
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(1);
        pointPaint = new Paint();
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStrokeWidth(3);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);
        pointPaint.setAlpha(60);
        setClickable(true);
        instance = PreferenceManager.getInstance(getContext());
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawColor(Color.rgb(0x46, 0x46, 0x46));
        drawAxis(canvas);
        drawLabels(canvas);
        drawPoints(canvas);
    }

    private void drawPoints(final Canvas canvas) {
        for (Point point : points) {
            canvas.drawOval(new RectF(point.x - 7, point.y - 7, point.x + 7, point.y + 7), axisPaint);
            canvas.drawOval(new RectF(point.x - 10, point.y - 10, point.x + 10, point.y + 10), pointPaint);
        }
        for (int i = 1; i < points.size(); i++) {
            canvas.drawLine(points.get(i - 1).x, points.get(i - 1).y, points.get(i).x, points.get(i).y, linePaint);
        }
    }

    private void drawAxis(final Canvas canvas) {
        canvas.drawLine(leftAxis, top, leftAxis, bottomAxis, axisPaint);
        canvas.drawLine(leftAxis, bottomAxis, left + width, bottomAxis, axisPaint);
        for (Path triangle : triangles) {
            canvas.drawPath(triangle, axisPaint);
        }
        int smallStep = yStep / 3;
        int smallXStep = xStep / 3;
        for (int i = 1; i < 5; i++) {
            canvas.drawLine(leftAxis, bottomAxis - yStep * i, leftAxis + 15, bottomAxis - yStep * i, axisPaint);
            canvas.drawLine(leftAxis, bottomAxis - yStep * i + smallStep, leftAxis + 7, bottomAxis - yStep * i + smallStep, axisPaint);
            canvas.drawLine(leftAxis, bottomAxis - yStep * i + smallStep * 2, leftAxis + 7, bottomAxis - yStep * i + smallStep * 2, axisPaint);
        }
        int xBaseline = leftAxis + smallXStep;
        for (int i = 0, size = equalizerSettings.getBands().length; i < size; i++) {
            canvas.drawLine(xBaseline + xStep * i, bottomAxis, xBaseline + xStep * i, bottomAxis - 15, axisPaint);
            if (i < size - 1) {
                canvas.drawLine(xBaseline + xStep * i + smallXStep, bottomAxis, xBaseline + xStep * i + smallXStep, bottomAxis - 7, axisPaint);
                canvas.drawLine(xBaseline + xStep * i + smallXStep * 2, bottomAxis, xBaseline + xStep * i + smallXStep * 2, bottomAxis - 7, axisPaint);
            }
        }
        canvas.drawLine(leftAxis, bottomAxis - yStep * 4 - smallStep, leftAxis + 7, bottomAxis - yStep * 4 - smallStep, axisPaint);
    }

    private void drawLabels(final Canvas canvas) {
        for (Label label : labels) {
            canvas.drawText(label.text, label.x, label.y, label.paint);
        }
    }

    public void updateSettings(final EqualizerSettings equalizerSettings) {
        this.equalizerSettings = equalizerSettings;
        invalidateView();
        postInvalidate();
    }

    private void invalidateView() {
        if (equalizerSettings != null && width > 0 && height > 0) {
            labels.clear();
            triangles.clear();
            points.clear();
            //todo: reset everything
            calculateAxis();
            calculateLeftLables();
            calculateBottomLables();
            calculateArrows();
            calculatePoints();
            calculateEqLabel();
        }
    }

    private void calculateAxis() {
        Rect bounds = new Rect();
        paintText.getTextBounds(getLevelString(equalizerSettings.getRange()[0] / 100), 0,
                getLevelString(equalizerSettings.getRange()[0] / 100).length(), bounds);
        int width = bounds.width();
        paintText.getTextBounds(getLevelString(equalizerSettings.getRange()[1] / 100), 0,
                getLevelString(equalizerSettings.getRange()[1] / 100).length(), bounds);
        width = Math.max(width, bounds.width());
        leftAxis = width + 10 + left;
        xLableHeight = 0;
        for (int i = 0, size = equalizerSettings.getBands().length; i < size; i++) {
            String bandString = getBandString(equalizerSettings.getBands()[i]);
            paintText.getTextBounds(bandString, 0, bandString.length(), bounds);
            if (xLableHeight < bounds.height()) {
                xLableHeight = bounds.height();
            }
        }
        bottomAxis = top + this.height - 10 - xLableHeight;
    }

    private String getLevelString(final double level) {
        return numberFormat.format(level) + "dB";
    }

    private void calculateLeftLables() {
        yStep = (int) ((bottomAxis - top) / 4.7);
        int low = equalizerSettings.getRange()[0] / 100;
        int upp = equalizerSettings.getRange()[1] / 100;
        double middle = (upp - low) / 2. + low;
        double subMiddle = (middle - low) / 2. + low;
        double subUpp = (upp - middle) / 2. + middle;
        Rect bounds = new Rect();
        paintText.getTextBounds(getLevelString(low), 0, getLevelString(low).length(), bounds);
        labels.add(new Label(leftAxis - 10 - bounds.width(), bottomAxis + bounds.height() / 2, getLevelString(low), paintText));
        paintText.getTextBounds(getLevelString(subMiddle), 0, getLevelString(subMiddle).length(), bounds);
        labels.add(new Label(leftAxis - 10 - bounds.width(), bottomAxis + bounds.height() / 2 - yStep, getLevelString(subMiddle), paintText));
        paintText.getTextBounds(getLevelString(middle), 0, getLevelString(middle).length(), bounds);
        labels.add(new Label(leftAxis - 10 - bounds.width(), bottomAxis + bounds.height() / 2 - yStep * 2, getLevelString(middle), paintText));
        paintText.getTextBounds(getLevelString(subUpp), 0, getLevelString(subUpp).length(), bounds);
        labels.add(new Label(leftAxis - 10 - bounds.width(), bottomAxis + bounds.height() / 2 - yStep * 3, getLevelString(subUpp), paintText));
        paintText.getTextBounds(getLevelString(upp), 0, getLevelString(upp).length(), bounds);
        labels.add(new Label(leftAxis - 10 - bounds.width(), bottomAxis + bounds.height() / 2 - yStep * 4, getLevelString(upp), paintText));
        topBorder = bottomAxis - yStep * 4;
    }

    private void calculateBottomLables() {
        xStep = (int) ((left + width - leftAxis) / (equalizerSettings.getBands().length - 0.3));
        Rect bounds = new Rect();
        for (int i = 0, size = equalizerSettings.getBands().length; i < size; i++) {
            String bandString = getBandString(equalizerSettings.getBands()[i]);
            paintText.getTextBounds(bandString, 0, bandString.length(), bounds);
            labels.add(new Label(leftAxis  + i * xStep - bounds.width() / 2 + xStep / 3, bottomAxis + 10 + xLableHeight, bandString, paintText));
        }
    }

    private String getBandString(final int band) {
        if (band > 1_000_000) {
            return numberFormat.format(band / 1_000_000.) + "kHz";
        }
        return numberFormat.format(band / 1000.) + "Hz";
    }

    private void calculateArrows() {
        float base = yStep * 0.1f;
        float normal = yStep * 0.25f;
        Path pathTop = new Path();
        pathTop.setFillType(Path.FillType.EVEN_ODD);
        pathTop.moveTo(leftAxis, top - 3);
        pathTop.lineTo(leftAxis + base, top + normal - 3);
        pathTop.lineTo(leftAxis - base, top + normal - 3);
        pathTop.close();
        Path pathLeft = new Path();
        pathLeft.setFillType(Path.FillType.EVEN_ODD);
        pathLeft.moveTo(left + width + 3, bottomAxis);
        pathLeft.lineTo(left + width - normal + 3, bottomAxis + base);
        pathLeft.lineTo(left + width - normal + 3, bottomAxis - base);
        pathLeft.close();
        triangles.add(pathTop);
        triangles.add(pathLeft);
    }

    private void calculatePoints() {
        for (int i = 0; i < equalizerSettings.getLevels().length; i++) {
            int x = leftAxis + xStep / 3 + i * xStep;
            int y = bottomAxis - yStep * (equalizerSettings.getLevels()[i] - equalizerSettings.getRange()[0]) / equalizerSettings.getLevelRange() * 4;
            points.add(new Point(x, y));
        }
    }

    private void calculateEqLabel() {
        Rect rect = new Rect();
        mainLabelText.getTextBounds("Equalizer", 0, "Equalizer".length(), rect);
        labels.add(new Label(width / 2 - rect.width() / 2 + leftAxis / 2, top + rect.height(), "Equalizer", mainLabelText));
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left - getPaddingTop() - getPaddingBottom();
        height = bottom - top - getPaddingLeft() - getPaddingRight();
        this.top = getPaddingTop();
        this.left = getPaddingLeft();
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
                        return true;
                    }
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (editIndex == -1) {
                    return false;
                }
                float correctedY = Math.min(bottomAxis, Math.max(y, topBorder));
                points.get(editIndex).y = (int) correctedY;
                if (editIndex == 0) {
                    invalidate(leftAxis, top, points.get(1).x, bottomAxis);
                } else if (editIndex == points.size() - 1) {
                    invalidate(points.get(editIndex - 1).x, top, left + width, bottomAxis);
                } else  {
                    invalidate(points.get(editIndex - 1).x, top, points.get(editIndex + 1).x, bottomAxis);
                }
                if (Math.abs(lastY - correctedY) > yStep / 5) {
                    equalizerSettings.getLevels()[editIndex] = (int) (equalizerSettings.getRange()[0] + (bottomAxis - correctedY) / yStep / 4 * equalizerSettings.getLevelRange());
                    instance.setEqSettings(equalizerSettings);
                    lastY = (int) correctedY;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                editIndex = -1;
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
