package com.mio.jrdv.wearkout.Activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mio.jrdv.wearkout.CircularProgressDrawable;
import com.mio.jrdv.wearkout.R;
import com.mio.jrdv.wearkout.model.FitnessData;


/**
 * Created by esq00931 on 05/01/2016.
 */
public class WorkutGeneralActivity  extends Activity {

    public static final int MAX_TIME = 8;      // 30s countdown timer
    public static final int START_TIME = 30;    // Countdown from 30 to zero
    private static CircularProgressDrawable mCircularProgressTimer;
    private ImageView mCircularImageView;

    //para los textos:

    private TextView Titulo,description;

    private FitnessData mFitnessData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        To display a custom notification, you first created the activity_workout.xml file to contain the custom layout for your notification.
         */
        setContentView(R.layout.activity_workout_general1);
        // Get a reference of our ImageView layout component to be used
        // to display our circular progress timer.
        mCircularImageView = (ImageView) findViewById(R.id.circledImageViewCancel);
        Titulo=(TextView)findViewById(R.id.title);
        description=(TextView)findViewById(R.id.description);


        //dependiendo del ejercicio que sea tendran un valor u otro!!!!

        //de momento a mano:

        Titulo.setText("FLEXIONES");
        description.setText("Siguiente:Abdominales");


        int startTime = MAX_TIME;
        mFitnessData = FitnessData.getInstance();
        // Get last timer value when starting or resuming a workout
        if (mFitnessData.isRunning() || mFitnessData.isPaused()) {
            startTime = mFitnessData.getLastTime();
        }

        // Create an instance of a drawable circular progress timer
        //  mCircularProgressTimer = new CircularProgressDrawable(startTime,  MAX_TIME, CircularProgressDrawable.Order.DESCENDING );

        //añadido context!!
       // mCircularProgressTimer = new CircularProgressDrawable(startTime,  MAX_TIME, CircularProgressDrawable.Order.DESCENDING ,this);

        //añadido Activity para quitar llmamada static!!
      //  mCircularProgressTimer = new CircularProgressDrawable(startTime,  MAX_TIME, CircularProgressDrawable.Order.DESCENDING ,this);


        // Set a callback to update our circular progress timer
        mCircularProgressTimer.setCallback(mPieDrawableCallback);
        // Set a drawable object for our Imageview
        mCircularImageView.setImageDrawable(mCircularProgressTimer);

        //le decinmos que empiece!!!
        mCircularProgressTimer.start();




        //aañdimos los listener a start y cancel

        final CircledImageView circledImageViewOK =
                (CircledImageView) findViewById(R.id.circledImageViewOK);
        circledImageViewOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "OK clicked",
                        Toast.LENGTH_SHORT).show();
            }
        });




    }
    @Override
    protected void onResume() {
        super.onResume();
        // mCircularProgressTimer.start();

        if (mFitnessData.isRunning()) {
            mCircularProgressTimer.start();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        // Save our last countdown value.
        // (It will be used in onCreate() when resuming a workout
        mFitnessData.setLastTime(mCircularProgressTimer.getValue());
    }


    private Drawable.Callback mPieDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            // Redraw our image with updated progress timer
            mCircularImageView.setImageDrawable(who);
        }
        // Empty placeholder
        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }
        // Empty placeholder
        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };


    public static void RecibirFinTimer(){

        //aqui recibiremo el fin del timer!a1

        Log.e("Recibido","fin de timer en WorkouViewActivity");

    }
}


