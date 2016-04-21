package com.mio.jrdv.wearkout.Activity;

        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.drawable.Drawable;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Vibrator;
        import android.preference.PreferenceManager;
        import android.support.wearable.view.CircledImageView;
        import android.support.wearable.view.DismissOverlayView;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.view.WindowManager;
        import android.view.animation.AlphaAnimation;
        import android.view.animation.Animation;
        import android.view.animation.AnimationSet;
        import android.view.animation.ScaleAnimation;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.koushikdutta.ion.Ion;
        import com.koushikdutta.ion.builder.AnimateGifMode;
        import com.mio.jrdv.wearkout.CircularProgressDrawable;
        import com.mio.jrdv.wearkout.CountDownAnimation;
        import com.mio.jrdv.wearkout.R;
        import com.mio.jrdv.wearkout.SQL.CalesteniaDataBaseHelper;
        import com.mio.jrdv.wearkout.model.Ejercicio;
        import com.mio.jrdv.wearkout.model.FitnessData;

        import java.util.List;
public class HRMActivity extends Activity implements CountDownAnimation.CountDownListener, SensorEventListener {


//para los gift animados desde al asset folder:


private ImageView GifVew;

//para poder cerrare al acabar eltimer:
private static Activity activity;






//para los textos:

private TextView HeartRateText;



//para los parametros a pasar:


private static final String INTENT_KEY_LEVEL_CHOOSEN = "LEVEL";

private static final String INTENT_KEY_TIME_EJERCICIO = "TIME";

private static final String INTENT_KEY_TIME_DELAY = "TIME_DELAY";
private static final String  INTENT_KEY_REPETICION_NUMBER = "REP_NUMBER";


//para eserar 3 segundos antes de emepzar el timer:

private Thread thread;


//para el countdown animated:

private TextView textCountdown;

//para el boton de cancel

private CircledImageView circledImageViewOK;

//para el cardiac:

private  SensorManager mSensorManager;
private   Sensor mHeartRateSensor;



//para las aimaciones poderlas parar sis e pulsa stop en el delay del repeticion time
//que es 1 ,2,o3 min

private CountDownAnimation countDownAnimation;
//y para que si anulamos el timer de los 1,2,3 min no salga el de delay normal





//para el longclick to exit:
private View.OnLongClickListener mlongListener;

private DismissOverlayView mDismissOverlay;

    //para el valor del HRM
private float HRMValorREAL=50;


@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //para qeu no se apague l apmatlla:
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //para poder cerrarnos desde el static que recibinos el ok del timer


        //onCreate
        activity = this;

        // Extract parameters del intnet y asignarlo a ivars
        Bundle extras = getIntent().getExtras();




        setContentView(R.layout.activity_hrm);
        // Get a reference of our ImageView layout component to be used
        // to display our circular progress timer.


        GifVew = (ImageView) findViewById(R.id.aniamtedgif_hrm);




        //vamos aprobar a poner el longclick to dismiss:


        GifVew.setOnLongClickListener(new View.OnLongClickListener() {
@Override
public boolean onLongClick(View v) {

        if (mlongListener != null) {
        mlongListener.onLongClick(v);
        }
        mDismissOverlay.show();
        return true;
        }
        });


        HeartRateText=(TextView)findViewById(R.id.hear_rate);

        //registramos el cardiac:
        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);


        // y el lsitener del cardiac!!

        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);



        //aañdimos los listener a cancel

        circledImageViewOK = (CircledImageView) findViewById(R.id.stopHRM);






        circledImageViewOK.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {


        //1º ue vibre

        Vibrator vibrator = (Vibrator) HRMActivity.this.getSystemService(WorkoutGeneralPassingArgumentsActivity.VIBRATOR_SERVICE);

        long[] vibrationPattern = {0, 500, 50, 300};


final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);


        //si se pulsa en stop hrm acabamos del tiron!!!
       //el listener del sensor se quitara desde onstop

           //como estamos en un click lo hacemos desde otro metodo

    FinHRM();


          //y nos cerramos


       // activity.finish();

        }
        });


        //lo deshabilitmos de momento!! solo se habilitara tras el countodown

        circledImageViewOK.setEnabled(true);

        //para el textcountdown
    textCountdown = (TextView) findViewById(R.id.texthrmanimated);

        //para el longclick to exit!!1

        mDismissOverlay = (DismissOverlayView)
        findViewById(R.id.dismiss_overlay);

        mDismissOverlay.setIntroText(R.string.long_press_intro);

        mDismissOverlay.showIntroIfNecessary();


    //y empezamos el cardio!!!

    StartHRMAnimation();

        }

    private void FinHRM() {

        // Create and start intent for this activity
        Intent intent = new Intent(this, StartChoosingSessionActivity.class);

        this.startActivity(intent);

        //y nos cerramos


        activity.finish();

    }

//////////////////////////////////////////////////////////////////Elegir Ejercicio segun SQL///////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////




//////////////////////////////////////////////////////////////////EMPEZAR DELAY Ejercicio  ///////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////


private void StartHRMAnimation() {



        //poenmos el gif de heart beat

        Ion.with(GifVew)
        .error(R.drawable.ic_pause)
        .fitXY()
        .animateGif(AnimateGifMode.ANIMATE)
        // .load("android.resource://[packagename]" + R.drawable.optinscreen_map)
        .load("android.resource://com.mio.jrdv.wearkout/" + R.drawable.normal_heart_rate1);






        //disminuimos el tamaño del HRM animated y la posicion

        textCountdown.setTextSize(65);


        textCountdown.setGravity(Gravity.CENTER | Gravity.TOP);
        //para le tag:
        int tagDelay=2;

        countDownAnimation = new CountDownAnimation(textCountdown, (int)HRMValorREAL,tagDelay);

        //añadimos el listener a nosotros en el listener le diremos que si es tag=2 vaya al ChooseRightEjercico para que acabe!!
        countDownAnimation.setCountDownListener(this);
        //y empezamos!!!
        countDownAnimation.start();



        }



//////////////////////////////////////////////////////////////////listener del cpountdown///////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void onCountDownEnd(CountDownAnimation animation) {
//no aplica pero es un must

}

@Override
public void onCountDown1Segundo(CountDownAnimation countDownAnimation) {



    //vamos a hacer que actualize su valor cada seg!!!

    countDownAnimation.setStartCount((int)HRMValorREAL);

        //esto nos notificara cada segundon para hacer que vibre en elDELAY

        if(countDownAnimation.getmTag()==2 && (int)HRMValorREAL>99) {

        //1º que vibre solo si el HRM es > XXX



        Vibrator vibrator = (Vibrator) HRMActivity.this.getSystemService(WorkoutGeneralPassingArgumentsActivity.VIBRATOR_SERVICE);


        //go go power rangers!!!vibrate patterns:
        // https://gearside.com/custom-vibration-patterns-mobile-devices/
        //hay que añadir un 0 al principio!!!

        long[] vibrationPattern = {0, 100};


//-1 = don't repeat
final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        //Log.e("Vibrado", "1 seg");

        }


        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////


@Override
protected void onResume() {
        super.onResume();
        // mCircularProgressTimer.start();


        }


@Override
protected void onPause() {
        super.onPause();

}

@Override
protected void onStop() {
        super.onStop();

        //al salir quitamos el listener para ahorrar bateria!!!

        mSensorManager.unregisterListener(this);

        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////del cardiac listener!!////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Override
public void onSensorChanged(SensorEvent event) {

      //  HeartRateText.setText((int) event.values[0]+" bpm");

    //le damos el valor del HRM a la ivar ya el countdown lo animara

    HRMValorREAL=event.values[0];

        }

@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
