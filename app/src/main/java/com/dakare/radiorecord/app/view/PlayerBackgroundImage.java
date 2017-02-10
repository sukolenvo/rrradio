package com.dakare.radiorecord.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.dakare.radiorecord.app.R;

public class PlayerBackgroundImage extends ImageView {
    private static final float BLUR_SCALE = 0.25f;
    private static final int BLUR_RADIUS = 4;

    public PlayerBackgroundImage(Context context) {
        super(context);
    }

    public PlayerBackgroundImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInnerPadding(context, attrs);
    }

    public PlayerBackgroundImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInnerPadding(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayerBackgroundImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInnerPadding(context, attrs);
    }

    private int width;
    private int height;
    private float imageXYRate;
    private float innerHorizontal = 24.f;
    private float innerVertical = 24.f;
    private int dim;

    private void initInnerPadding(final Context context, final AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PlayerBackgroundImage, 0, 0);
        try {
            innerHorizontal = innerVertical = ta.getDimension(R.styleable.PlayerBackgroundImage_innerMargin, 24.f);
            float horizontal = ta.getDimension(R.styleable.PlayerBackgroundImage_innerMarginHorizontal, -1.f);
            if (horizontal > 0) {
                innerHorizontal = horizontal;
            }
            float vertical = ta.getDimension(R.styleable.PlayerBackgroundImage_innerMarginVertical, -1.f);
            if (vertical > 0) {
                innerVertical = vertical;
            }
            dim = ta.getInteger(R.styleable.PlayerBackgroundImage_dim, 80);
        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        if (width != 0 && height != 0) {
            if (getDrawable() != null) {
                Bitmap bitmap = drawableToBitmap(getDrawable());
                putInBackground(canvas, bitmap);
                canvas.drawARGB(dim, 256, 256, 256);
                putInCenter(canvas, bitmap);
            }
        }
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void putInCenter(final Canvas canvas, final Bitmap bitmap) {
        float defaultXYRate = (float) bitmap.getWidth() / bitmap.getHeight();
        Rect destination;
        float width = this.width - 2 * innerHorizontal;
        float height = this.height - 2 * innerVertical;
        float imageXYRate = width / height;
        if (imageXYRate > defaultXYRate) {
            int resultWidth = (int) (height * defaultXYRate);
            destination = new Rect((int) (innerHorizontal + (width - resultWidth) / 2), (int) (innerVertical),
                    (int) (innerHorizontal + (width + resultWidth) / 2), (int) (height + innerVertical));
        } else {
            int resultHeight = (int) (width / defaultXYRate);
            destination = new Rect((int) (innerHorizontal), (int) (innerVertical + (height - resultHeight) / 2),
                    (int) (width + innerHorizontal), (int) (innerVertical + (height + resultHeight) / 2));
        }
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), destination, null);
    }

    private void putInBackground(final Canvas canvas, final Bitmap bitmap) {
        float defaultXYRate = (float) bitmap.getWidth() / bitmap.getHeight();
        Rect destination;
        if (imageXYRate > defaultXYRate) {
            int resultHeight = (int) (bitmap.getHeight() * defaultXYRate / imageXYRate);
            destination = new Rect(0, (bitmap.getHeight() - resultHeight) / 2, bitmap.getWidth(), (bitmap.getHeight() - resultHeight) / 2 + resultHeight);
        } else {
            int resultWidth = (int) (bitmap.getWidth() * imageXYRate / defaultXYRate);
            destination = new Rect((bitmap.getWidth() - resultWidth) / 2, 0, (bitmap.getWidth() - resultWidth) / 2 + resultWidth, bitmap.getHeight());
        }

        Bitmap fastblur = fastblur(bitmap, BLUR_SCALE, BLUR_RADIUS, destination);
        canvas.drawBitmap(fastblur, new Rect(0, 0, fastblur.getWidth(), fastblur.getHeight()), new Rect(0, 0, width, height), null);
    }

    public Bitmap fastblur(final Bitmap sentBitmap, final float scale, final int radius, final Rect area) {
        Bitmap result = Bitmap.createBitmap(sentBitmap, area.left, area.top, area.width(), area.height());
        int width = Math.round(result.getWidth() * scale);
        int height = Math.round(result.getHeight() * scale);
        result = Bitmap.createScaledBitmap(result, width, height, false);
        Bitmap bitmap = result.copy(result.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left;
        height = bottom - top;
        imageXYRate = (float) width / height;
    }
}
