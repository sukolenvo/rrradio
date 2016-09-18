package com.dakare.radiorecord.app.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class LandingRotatingBackgroundView extends View
{
    private static final long a = SystemClock.elapsedRealtime();
    private final int[] b = { -5895002, -16720419, -27136, -65536, 0, 0, 0, 0 };
    private final short[] c = { 0, 1, 2, 0, 2, 3 };
    private final float[] d = { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F };
    private final int[] e = new int[2];
    private final Matrix f = new Matrix();
    private final Paint g = new Paint(2);
    private View h;
    private Bitmap i;

    public LandingRotatingBackgroundView(Context paramContext)
    {
        super(paramContext);
        a();
    }

    public LandingRotatingBackgroundView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        a();
    }

    public LandingRotatingBackgroundView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        a();
    }

    private void a()
    {
        Bitmap localBitmap = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        Canvas localCanvas = new Canvas(localBitmap);
        localCanvas.scale(localCanvas.getWidth(), localCanvas.getHeight());
        localCanvas.drawVertices(Canvas.VertexMode.TRIANGLES, this.d.length, this.d, 0, null, 0, this.b, 0, this.c, 0, 6, this.g);
        a(localBitmap, 25, Math.max(Runtime.getRuntime().availableProcessors(), 1));
        this.i = localBitmap;
    }

    public Bitmap a(Bitmap paramBitmap, float paramFloat, int paramInt)
    {
        paramBitmap = Bitmap.createScaledBitmap(paramBitmap, Math.round(paramBitmap.getWidth() * paramFloat), Math.round(paramBitmap.getHeight() * paramFloat), false);
        return paramBitmap;
    }

    public void onDraw(Canvas paramCanvas)
    {
        if (h == null) {
            h = (View) getParent();
        }
        getLocationInWindow(this.e);
        int j = this.e[1];
        if (this.h != null)
            this.h.getLocationInWindow(this.e);
        for (j = this.e[1] + this.h.getHeight() - j; j <= 0; j = getHeight())
            return;
        int k = getWidth() / 2;
        j = j / 2 * 3;
        float f1 = (float)Math.sqrt(j * j + k * k);
        float f2 = 2.0F * f1 / this.i.getWidth();
        float f3 = k;
        float f4 = j;
        float f5 = (float)(SystemClock.elapsedRealtime() - a) % 37000.0F * 360.0F / 37000.0F;
        float f6 = this.i.getWidth() / 2;
        this.f.reset();
        this.f.preRotate(f5, f6, f6);
        this.f.postScale(f2, f2);
        this.f.postTranslate(f3 - f1, f4 - f1);
        paramCanvas.drawBitmap(this.i, this.f, this.g);
        invalidate();
    }
}