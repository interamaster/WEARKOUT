package com.mio.jrdv.wearkout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.os.Vibrator;

import com.mio.jrdv.wearkout.Activity.WorkoutGeneralPassingArgumentsActivity;
import com.mio.jrdv.wearkout.Activity.WorkutGeneralActivity;


/**
 * Created by esq00931 on 04/01/2016.
 */
public class CircularProgressDrawable extends Drawable implements Animatable {

    static public enum Order {
        ASCENDING,
        DESCENDING
    }

    private static final long FRAME_DURATION = 1000; // a1 sec interval
    private static final float START_ANGLE = 270f;
    private Paint mPaint;
    private boolean mIsRunning;
    private int mMaxValue;
    private Order mOrder;
    private int mValue;
    private float mSweepAngle;

    //para sber cuando acaba:

    private  Context mcontext;
    private WorkoutGeneralPassingArgumentsActivity mActivity;

    //para poder Avisar al alaCtuvity

    private WorkutGeneralActivity workoutViewActivityParaLlamar;
    private WorkoutGeneralPassingArgumentsActivity workoutViewActivityParaLlamar2;




/*
    public CircularProgressDrawable(int currentValue, int maxValue, Order order, Context context) {
        super();
        mValue = currentValue;
        mMaxValue = maxValue;
        mOrder = order;
        mPaint = new Paint();
        this.mcontext=context;

    }
*/

    //otro creador conla xctivity

    public CircularProgressDrawable(int currentValue, int maxValue, Order order, WorkoutGeneralPassingArgumentsActivity activity) {
        super();
        mValue = currentValue;
        mMaxValue = maxValue;
        mOrder = order;
        mPaint = new Paint();
        this.mActivity=activity;

    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        final RectF oval = new RectF(bounds);
        float x, y, radius;
        // Figure out where to draw things.
        // (Just center everything within our current layout's bounds)
        x = y = radius = bounds.bottom / 2.0f;
        // Draw our circle
        mPaint.setColor(Color.parseColor("#a3daf1"));
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radius, mPaint);
        // Overlap with a pie bar
        mPaint.setColor(Color.parseColor("#44b7e4"));
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(oval, START_ANGLE, mSweepAngle, true, mPaint);
        // Set text in the center of our circle
        mPaint.setStrokeWidth(0);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setTextSize(30);
        canvas.drawText(""+mValue+"s", x, y+(mPaint.getTextSize()/3), mPaint);
    }
    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
    // Empty placeholder
    @Override
    public void setAlpha(int arg0) {
    }
    // Empty placeholder
    @Override
    public void setColorFilter(ColorFilter arg0) {
    }
    @Override
    public boolean isRunning() {
        return mIsRunning;
    }
    @Override
    public void start() {
        if (!isRunning()) {
            mIsRunning = true;
            // Schedule an action every second to update our drawable
            scheduleSelf(mRefreshRunnable, SystemClock.uptimeMillis() + FRAME_DURATION);
            // Calling this will invoke our callback to have our drawable redrawn
            invalidateSelf();
        }
    }
    @Override
    public void stop() {
        if (isRunning()) {
            unscheduleSelf(mRefreshRunnable);
            mValue = (mOrder == Order.ASCENDING ? 0 : mMaxValue);
            mIsRunning = false;

        }
    }
    public int getValue() {
        return mValue;
    }
    private final Runnable mRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            mValue = (mOrder == Order.ASCENDING) ? mValue + 1 : mValue - 1;
           /* if (mValue < 0){


                mValue = 0;


            }*/
            // Figure out next angle for drawArc()
            mSweepAngle = (360.0f * mValue) / (float)mMaxValue;
            // Have we reached the end?
            if ((mOrder == Order.ASCENDING && mValue > mMaxValue) ||
                    (mOrder == Order.DESCENDING && mValue <
                            0)) {


                mValue = 0;


                //1º ue vibre
                //asi funciona pasando el context:
               // Vibrator vibrator = (Vibrator) mcontext.getSystemService(mcontext.VIBRATOR_SERVICE);

                //asi funciona pasando la activity:
                Vibrator vibrator = (Vibrator) mActivity.getSystemService(mActivity.VIBRATOR_SERVICE);



               // long[] vibrationPattern = {0, 500, 50, 300};


               //long[] vibrationPattern = { 0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500};

                //go go power rangers!!!vibrate patterns:
               // https://gearside.com/custom-vibration-patterns-mobile-devices/
                //hay que añadir un 0 al principio!!!

                long[] vibrationPattern = {0,150,150,150,150,75,75,150,150,150,150,450};


                //-a1 - don't repeat
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);



              //  workoutViewActivityParaLlamar.RecibirFinTimer();//asi es null la activity!!

                //asi funciona pasando el context:
              //workoutViewActivityParaLlamar2.RecibirFinTimer(mcontext);//ahora es una activity nueva no una notifiacion


                //pero sigu siendo null..a menos que defina el metodo com static!!!!manda cojones

           //   workoutViewActivityParaLlamar2=new WorkoutGeneralPassingArgumentsActivity();
            //    workoutViewActivityParaLlamar2.RecibirFinTimerSinStatic();



                //asi funciona pasando la activity:
                mActivity.RecibirFinTimerSinStatic();



                //TODO y aqui es donde sabemos que ha acabado!!



                // NotificationBuilder.update(mcontext ,FitnessData.WorkoutState.FINISHED);




                // Yes, unschedule our Runnable to stop drawing.
                stop();
            }
            else {
                // Otherwise redraw using new sweep angle
                scheduleSelf(mRefreshRunnable, SystemClock.uptimeMillis() +
                        FRAME_DURATION);
                // Invoke our callback to have our drawable redrawn
                invalidateSelf();
            }
        }
    };
}

