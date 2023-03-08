package com.sweteam5.ladybugadmin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BusLineView extends View {

    // Names for bus stops
    private String[] names = {"정문", "교육대학원", "학생회관", "기숙사", "학생회관", "중앙도서관", "예술체육대학", "글로벌센터", "정문"};
    private int posX = 150;         // Position x for line
    private int startY = 50;        // Starting position y for line
    private int marginBottom = 100; // Margin bottom of line
    private int middleIndex = 3;    // Turning station index
    private int lineHeight = 0;     // Height of the line

    Typeface tf; // Font instance (godo_m.ttf)

    public int getPosX() {
        return posX;
    }

    public int getStartY() {
        return startY;
    }

    public int getMiddleIndex() {
        return middleIndex;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public String[] getNames() {
        return names;
    }

    public BusLineView(Context context){
        super(context);
    }

    public BusLineView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // Calculate the height of line
        lineHeight = canvas.getHeight() - marginBottom - startY;

        // Caching the length of the name array
        int length = names.length;

        // Load font resource
        tf = Typeface.createFromAsset(getContext().getAssets(), "godo_m.TTF");

        // Draw line and station marks (first, turning station, last are drawn big, the rest are drawn small)
        drawBaseLine(canvas, posX, startY);
        drawBigStation(canvas, posX, startY, names[0]);
        for(int i = 1; i < middleIndex; i++)
            drawSmallStation(canvas, posX, getY(i, middleIndex, length, lineHeight, startY), names[i]);
        drawBigStation(canvas, posX, getY(middleIndex, middleIndex, length, lineHeight, startY), names[middleIndex]);
        for(int i = middleIndex + 1; i < length - 1; i++)
            drawSmallStation(canvas, posX, getY(i, middleIndex, length, lineHeight, startY), names[i]);
        drawBigStation(canvas, posX, getY(length - 1, middleIndex, length, lineHeight, startY), names[length - 1]);
    }

    // Get position y of the station at the specific index
    public int getY(int index, int middleIndex, int length, int height, int startY) {
        // Calculate the value of y by dividing it up and down n equal to the turning point.
        if(index < middleIndex)
        {
            return (int)((float)index / middleIndex * height / 2) + startY;
        }
        else if(index > middleIndex)
        {
            return (int)((float)(index - middleIndex) / (length - middleIndex - 1) * height / 2)
                    + getY(middleIndex, middleIndex, length, height, startY);
        }
        else
            return height / 2 + startY;
    }

    // Draw the base line on canvas
    private void drawBaseLine(Canvas canvas, int posX, int startY) {
        Paint pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
        pnt.setStrokeWidth(16);
        pnt.setColor(Color.parseColor("#db0b0b"));
        pnt.setStrokeCap(Paint.Cap.ROUND);
        int h = canvas.getHeight();
        canvas.drawLine(posX, startY, posX, h - 100, pnt);
    }

    // Draw the big station with station name
    private void drawBigStation(Canvas canvas, int x, int y, String text){
        Paint pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
        float size = 20;    // Size of the station circle

        // Draw inner circle
        pnt.setStyle(Paint.Style.FILL);
        pnt.setColor(Color.WHITE);
        canvas.drawCircle(x, y, size, pnt);

        // Draw outer stroke circle
        pnt.setStrokeWidth(10);
        pnt.setStyle(Paint.Style.STROKE);
        pnt.setColor(Color.BLACK);
        canvas.drawCircle(x, y, size, pnt);

        // Draw the name of station
        pnt.setStyle(Paint.Style.FILL);
        pnt.setTextSize(40);
        pnt.setTypeface(tf);
        canvas.drawText(text, x + 50, y + size / 2, pnt);
    }

    // Draw the small station with station name
    private void drawSmallStation(Canvas canvas, int x, int y, String text){
        Paint pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
        float size = 15;    // Size of the station circle

        // Draw inner circle
        pnt.setStyle(Paint.Style.FILL);
        pnt.setColor(Color.WHITE);
        canvas.drawCircle(x, y, size, pnt);

        // Draw outer stroke circle
        pnt.setStrokeWidth(6);
        pnt.setStyle(Paint.Style.STROKE);
        pnt.setColor(Color.BLACK);
        canvas.drawCircle(x, y, size, pnt);

        // Draw the name of station
        pnt.setStyle(Paint.Style.FILL);
        pnt.setTextSize(40);
        pnt.setTypeface(tf);
        canvas.drawText(text, x + 50, y + size / 2, pnt);
    }
}
