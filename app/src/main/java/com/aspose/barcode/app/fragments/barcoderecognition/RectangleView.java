package com.aspose.barcode.app.fragments.barcoderecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class RectangleView extends View
{
    private final Rect drawableRect;
    private final Paint paint;

    public RectangleView(Context context, int viewWidth, int viewHeight)
    {
        super(context);
        this.drawableRect = calcFocusRectangle(viewWidth, viewHeight);
        paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
    }

    private static Rect calcFocusRectangle(int viewWidth, int viewHeight)
    {

        int rectangleWidth = 0;
        int rectangleLeft = 0;
        int rectangleTop = 0;
        if(viewWidth < viewHeight)
        {
            rectangleWidth = (int) (viewWidth * 0.8);
            rectangleLeft = (int) (viewWidth * 0.1);
            rectangleTop = (viewHeight - rectangleWidth) / 2;
        }
        else
        {
            rectangleWidth = (int)(viewHeight * 0.8);
            rectangleLeft = (viewHeight - rectangleWidth) / 2;
            rectangleTop = (int) (viewWidth * 0.1);
        }

        return new Rect(rectangleLeft, rectangleTop, (rectangleLeft + rectangleWidth), (rectangleTop + rectangleWidth));
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawRect(drawableRect, paint);
    }

    public Rect getDrawableRect()
    {
        return drawableRect;
    }
}
