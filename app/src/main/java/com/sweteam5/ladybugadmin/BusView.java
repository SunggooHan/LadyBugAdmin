package com.sweteam5.ladybugadmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;

public class BusView extends View {
    private BusLineView busLineView;    // Line drawing View instance

    private Paint paint;                // Paint instance of bus drawing
    private Bitmap bus;                 // Original bus image bitmap
    private Bitmap scaledBus;           // Scaled bus image bitmap
    private int locIndex = 0;           // Location index of bus image
    private int size = 0;               // Size of bus image

    private Animation movingEffect = null;  // Moving animation of bus in 'between state'

    public BusView(Context context, BusLineView busLineView) {
        super(context);

        init(context, busLineView);
    }

    public BusView(Context context, @Nullable AttributeSet attrs, BusLineView busLineView) {
        super(context, attrs);

        init(context, busLineView);
    }

    // Initialize the setting data
    public void init(Context context, BusLineView busLineView) {
        this.busLineView = busLineView;

        paint = new Paint();
        bus = BitmapFactory.decodeResource(getResources(), R.drawable.ladybug_bus);
        size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        scaledBus = Bitmap.createScaledBitmap(bus, size, size, true);
    }

    // Update the location of bus image according to location index
    public void updateLocation(int locIndex) {
        this.locIndex = locIndex;   // Change the location index
        invalidate();               // Redraw the canvas (drawing on onDraw method)

        // If location is in 'between state', start the loop animation that is moving between stations
        if(locIndex % 2 == 1) {
            int height = getSectionHeight(locIndex);
            movingEffect = new TranslateAnimation(0, 0, height * 0.3f, height * 0.7f);
            movingEffect.setRepeatCount(Animation.INFINITE);
            movingEffect.setDuration(3000);
            startAnimation(movingEffect);
        }
        // If location is in 'in state', stop the animation
        else {
            stopAnimation();
        }
    }

    // Stop animation (between stations)
    public void stopAnimation() {
        if(movingEffect != null)
            movingEffect.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // The bus image is drawn by calculating the location of the bus according to the location index
        canvas.drawBitmap(scaledBus, busLineView.getPosX() - size / 2, getBusY(locIndex) - size / 2, paint);
    }

    // Get bus location according to location index
    private int getBusY(int locIndex) {
        int height = busLineView.getLineHeight();
        int middleIndex = busLineView.getMiddleIndex();
        int startY = busLineView.getStartY();
        int len = busLineView.getNames().length;
        return busLineView.getY(locIndex / 2, middleIndex, len, height, startY);
    }

    // Get the height of the section between the stations.
    private int getSectionHeight(int locIndex) {
        int height = busLineView.getLineHeight();
        int middleIndex = busLineView.getMiddleIndex();
        int len = busLineView.getNames().length;

        if(locIndex < middleIndex * 2) {
            return height / 2 / middleIndex;
        }
        else {
            return height / 2 / (len - middleIndex - 1);
        }
    }
}
