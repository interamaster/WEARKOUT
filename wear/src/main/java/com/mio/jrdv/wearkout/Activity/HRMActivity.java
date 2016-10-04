package com.mio.jrdv.wearkout.Activity;

        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
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
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.koushikdutta.ion.Ion;
        import com.koushikdutta.ion.builder.AnimateGifMode;
        import com.mio.jrdv.wearkout.CountDownAnimation;
        import com.mio.jrdv.wearkout.R;

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

    //para el color del gif animado del HRM(verde/amarillo/rojo
    //0=verde
    //1=amarillo
    //2=rojo

private int HRMLevel=0;



    //para las pref de edad, freq_reposo y sexo

    private  int freq_reposo;
    private  int edad;
    private  String sexo;//el idioma elegido

//para el valor a usar de fre segun edad/sexo y freq_reposo(freqMAX)
    private double freqMAX;
    private int freqMAX70;
    private int freqMAX80;

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



    //recuepramos de las pref los valres de freq_reposo y genero para calcular valor del HRM!!!

    final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

    freq_reposo=Integer.parseInt(preferences.getString("freq_reposo",null));
    sexo=preferences.getString("genero","man");
    edad=Integer.parseInt(preferences.getString("edad", String.valueOf(45)));


    //a partir de eso valores sacamos la freqMAX con la formula:
    /*
    Fc máxima = 220-edad
    70% Fc de trabajo según Karvonen = (Fc máxima-Fc reposo)·0,7 + Fc reposo

    Intensidad muy ligera: 50-60%, útil para trabajos de recuperación, calentamiento y vuelta a la calma.

Intensidad ligera: 60-70%, zona para el trabajo base de la condición física, muy recomendable para personas que se inician en el deporte

Intensidad moderada: 70-80%, intervalo en el que ya se persigue un objetivo de mejora en rendimiento y se trabaja la eficiencia del corazón
 (utilización de menos energía para la realización de un esfuerzo)

Intensidad dura:80-90%, este ya es un escalón donde la fatiga aparece de manera manifiesta
 El objetivo es ganar rendimiento y poder trabajar a alta intensidad a lo largo del tiempo.


Intensidad máxima: 90-100%, es el máximo esfuerzo que pueden tolerar nuestros órganos y músculos, se trata de un entrenamiento anaeróbico
que debido a su dureza sólo se puede aplicar en breves periodos de tiempo (menos de 5 minutos).


     */

        freqMAX=220-edad;

   // 70% Fc de trabajo según Karvonen = (Fc máxima-Fc reposo)·0,7 + Fc reposo

        freqMAX70=(int)((freqMAX-freq_reposo)*0.7 )+freq_reposo;

     Log.e("70% freqMAX", String.valueOf(freqMAX70));


    // 80% Fc de trabajo según Karvonen = (Fc máxima-Fc reposo)·0,8 + Fc reposo

    freqMAX80 =(int)((freqMAX-freq_reposo)*0.8 )+freq_reposo;

    Log.e("80% freqMAX", String.valueOf(freqMAX80));


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


    int previousHRMValue=HRMLevel;


    //de momento cada seg para probar en simulador aumenta en 1 la freq

   // HRMValorREAL++;//TODO lo quito pra probar enreal




    //vamos a hacer que actualize su valor cada seg!!!

    countDownAnimation.setStartCount((int)HRMValorREAL);
    countDownAnimation.start();
    //esto nos notificara cada segundon para hacer que vibre en elDELAY

    if(countDownAnimation.getmTag()==2 && (int)HRMValorREAL>=freqMAX70 &&(int)HRMValorREAL<freqMAX80 ) {//TODO poenr valor real freq optima

        HRMLevel=1;



        //color VERDE esta en entre le 70 y 80% de la freqMAX

        textCountdown.setTextColor(Color.GREEN);
        //1º que vibre solo si el HRM es > XXX



        Vibrator vibrator = (Vibrator) HRMActivity.this.getSystemService(WorkoutGeneralPassingArgumentsActivity.VIBRATOR_SERVICE);


        //go go power rangers!!!vibrate patterns:
        // https://gearside.com/custom-vibration-patterns-mobile-devices/
        //hay que añadir un 0 al principio!!!

        long[] vibrationPattern = {0, 20};


        //-1 = don't repeat
        final int indexInPatternToRepeat = -1;
        //de momentyo quitp vibracion: vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        //Log.e("Vibrado", "1 seg");



    }
        else if((int)HRMValorREAL>=freqMAX80){//TODO poner valor real de freq max

        //nivel maximo

        HRMLevel=2;




        //color ROJO esta a ams del 80%

        textCountdown.setTextColor(Color.RED);

        //que vibre mas seguido....


        Vibrator vibrator = (Vibrator) HRMActivity.this.getSystemService(WorkoutGeneralPassingArgumentsActivity.VIBRATOR_SERVICE);


        //go go power rangers!!!vibrate patterns:
        // https://gearside.com/custom-vibration-patterns-mobile-devices/
        //hay que añadir un 0 al principio!!!


        // Start without a delay
    // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] vibrationPattern = {0, 20,440,20,440};


        //-1 = don't repeat
        final int indexInPatternToRepeat = -1;
         vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        //Log.e("Vibrado", "1 seg");

    }

    else {
        //nivel normal

        HRMLevel=0;

        //que  NOOOO  vibre



        //color GRIS esta en bajo nivel

        textCountdown.setTextColor(Color.GRAY);


    }




    //si ha cambiao e HRMlevel cambiamos el gif animado


    if (previousHRMValue != HRMLevel){


        switch (HRMLevel) {
            case 0:
                //normal verde
                //poenmos el gif de heart beat

                Ion.with(GifVew)
                        .error(R.drawable.ic_pause)
                        .fitXY()
                        .animateGif(AnimateGifMode.ANIMATE)
                        // .load("android.resource://[packagename]" + R.drawable.optinscreen_map)
                        .load("android.resource://com.mio.jrdv.wearkout/" + R.drawable.normal_heart_rate1);



                break;
            case 1:
                //alto amarillo

                //poenmos el gif de heart beat

                Ion.with(GifVew)
                        .error(R.drawable.ic_pause)
                        .fitXY()
                        .animateGif(AnimateGifMode.ANIMATE)
                        // .load("android.resource://[packagename]" + R.drawable.optinscreen_map)
                        .load("android.resource://com.mio.jrdv.wearkout/" + R.drawable.yellow_heart_rate1);



                break;
            case 2:
                //maximo rojo


                //poenmos el gif de heart beat

                Ion.with(GifVew)
                        .error(R.drawable.ic_pause)
                        .fitXY()
                        .animateGif(AnimateGifMode.ANIMATE)
                        // .load("android.resource://[packagename]" + R.drawable.optinscreen_map)
                        .load("android.resource://com.mio.jrdv.wearkout/" + R.drawable.red_heart_rate1);




                break;}

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
