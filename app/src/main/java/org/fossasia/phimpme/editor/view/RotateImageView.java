package org.fossasia.phimpme.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.fossasia.phimpme.editor.utils.PaintUtil;

public class RotateImageView extends View {
    private Rect srcRect;
    private RectF dstRect;
    private Rect maxRect;// The maximum limit rectangle

    private Bitmap bitmap;
    private Matrix matrix = new Matrix();// Aiding rectangular

    private float scale;//scaling ratio
    private int rotateAngle;

    private RectF wrapRect = new RectF();// Picture surrounded by rectangles
    private Paint bottomPaint;
    private RectF originImageRect;

    public RotateImageView(Context context) {
        super(context);
        init(context);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        srcRect = new Rect();
        dstRect = new RectF();
        maxRect = new Rect();
        bottomPaint = PaintUtil.newBackgroundPaint(context);
        originImageRect = new RectF();
    }

    public void addBit(Bitmap bit, RectF imageRect) {
        bitmap = bit;
        srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
        dstRect = imageRect;

        originImageRect.set(0, 0, bit.getWidth(), bit.getHeight());
        this.invalidate();
    }

    public void rotateImage(int angle) {
        rotateAngle = angle;
        this.invalidate();
    }

    public void reset() {
        rotateAngle = 0;
        scale = 1;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (bitmap == null)
            return;
        maxRect.set(0, 0, (int) originImageRect.width(), (int) originImageRect.height());// The maximum bounding rectangle
        calculateWrapBox();
        scale = 1;
        if (wrapRect.width() > getWidth()) {
            scale = getWidth() / wrapRect.width();
        }

        canvas.save();
        canvas.scale(scale, scale, getWidth() >> 1,
                getHeight() >> 1);
        calculateWrapBox();
        scale = 1;
        canvas.save();
        canvas.drawRect(wrapRect, bottomPaint);
        canvas.rotate(rotateAngle, getWidth() >> 1,
                getHeight() >> 1);
        canvas.drawBitmap(bitmap, srcRect, dstRect, null);
        canvas.restore();
    }

    private void calculateWrapBox() {
        wrapRect.set(dstRect);
        matrix.reset();// Reset matrix is ​​a unit matrix
        int centerX = (getWidth() / 2);
        int centerY = (getHeight() / 2);
        matrix.postRotate(rotateAngle, centerX, centerY);// After the rotation angle
        matrix.mapRect(wrapRect);
    }

    public RectF getImageNewRect() {
        Matrix m = new Matrix();
        m.postRotate(this.rotateAngle, originImageRect.centerX(),
                originImageRect.centerY());
        m.mapRect(originImageRect);
        return originImageRect;
    }

    public synchronized float getScale() {
        return scale;
    }

    public synchronized int getRotateAngle() {
        return rotateAngle;
    }
}