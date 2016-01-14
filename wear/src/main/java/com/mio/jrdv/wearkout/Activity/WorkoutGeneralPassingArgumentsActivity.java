package com.mio.jrdv.wearkout.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.CircledImageView;
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



/**
 * Created by esq00931 on 05/01/2016.
 */
public class WorkoutGeneralPassingArgumentsActivity extends Activity implements CountDownAnimation.CountDownListener, SensorEventListener {


    //para los gift animados desde al asset folder:

    // private static final String GIFS1_FOLDER= "animated_gif1";
    //private AssetManager mAssets;
    private static final String TAG = "WOGeneral";
    private ImageView GifVew;

    //para poder cerrare al acabar eltimer:
    private static Activity activity;


    private int MAX_TIME = 30;      // 30s countdown timer
    public static final int START_TIME = 30;    // Countdown from 30 to zero
    private static CircularProgressDrawable mCircularProgressTimer;
    private ImageView mCircularImageView;

    //para los textos:

    private TextView Titulo;

    private FitnessData mFitnessData;
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


    //para los valores de SQL

    private String LevelChoosen;
    private int EjercicioTime;
    private int EjercicioDelayTime;

    private CalesteniaDataBaseHelper dbhandler;

    private List<Ejercicio> ejerciciosChoosen;

    private    int NumeroTotalEjercicios;//para saber cuantos ejerciciso segun LEVEL hay en cada serie
    private    int NuemroEjerciciosPendientes;//para saber cuantos quedan aun
    private  int NumeroRepeticiones;//numero de repeticiones elegidas en PREFS
    private  int RepeticioesYaHechas=1;//veces que ya hemos repetido la rutina


    //para saber si ya hemos terminado todos los ejercicios
    private  boolean TUTTOFINITO = false;

    //para el boton de cancel

    private CircledImageView circledImageViewOK;

    //para el cardiac:

    private  SensorManager mSensorManager;
    private   Sensor mHeartRateSensor;




    //no lo uso
/*
    public static void startActivity(Context context, String paramA, String paramB) {
        // Build extras with passed in parameters
        Bundle extras = new Bundle();
        extras.putString(INTENT_KEY_LEVEL_CHOOSEN, paramA);
        extras.putString(INTENT_KEY_TIME_EJERCICIO, paramB);

        // Create and start intent for this activity
        Intent intent = new Intent(context, WorkoutGeneralPassingArgumentsActivity.class);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

*/

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
        LevelChoosen = extras.getString(INTENT_KEY_LEVEL_CHOOSEN);
        EjercicioTime = extras.getInt(INTENT_KEY_TIME_EJERCICIO);
        EjercicioDelayTime = extras.getInt(INTENT_KEY_TIME_DELAY);
        NumeroRepeticiones=extras.getInt(INTENT_KEY_REPETICION_NUMBER);


        //le pasamos el paramB al tiempo

        MAX_TIME = EjercicioTime;


        // Proceed as normal...




        /*
        To display a custom notification, you first created the activity_workout.xml file to contain the custom layout for your notification.
         */
        setContentView(R.layout.activity_workout_general1);
        // Get a reference of our ImageView layout component to be used
        // to display our circular progress timer.
        mCircularImageView = (ImageView) findViewById(R.id.circledImageViewCancel);
        Titulo = (TextView) findViewById(R.id.title);

        GifVew = (ImageView) findViewById(R.id.aniamtedgif_holder1);

        HeartRateText=(TextView)findViewById(R.id.hear_rate);

        //registramos el cardiac:
          mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
          mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);


        // y el lsitener del cardiac!!

        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);



        //dependiendo del ejercicio que sea tendran un valor u otro!!!!

        //de momento a mano:

        // Titulo.setText("FLEXIONES");
        // description.setText("Siguiente:Abdominales");

        //los pongo del intent://no mejor desde el SQL en metodo ChooseRightEjercicio

        //Titulo.setText(paramA);

        //del intent tendremos qsacar el LEVEL para conseguir un arraylist de Ejercicios e ir sacando los valores!!

        //iniciamos SQL


        dbhandler = new CalesteniaDataBaseHelper(this);


        //elegimos el que toqca ahora:


        ejerciciosChoosen = dbhandler.getAllEjerciciosLEVEL(LevelChoosen);

        //al prinicipio los  jerecicios ptes=totales:
        NumeroTotalEjercicios = ejerciciosChoosen.size();
        NuemroEjerciciosPendientes = ejerciciosChoosen.size();


        //una vez tenemos nuestro ARRyList elegimos el que toque:


        ChooseRightEjercicio();


        int startTime = MAX_TIME;
        mFitnessData = FitnessData.getInstance();
        // Get last timer value when starting or resuming a workout
        if (mFitnessData.isRunning() || mFitnessData.isPaused()) {
            startTime = mFitnessData.getLastTime();
        }

        // Create an instance of a drawable circular progress timer
        //  mCircularProgressTimer = new CircularProgressDrawable(startTime,  MAX_TIME, CircularProgressDrawable.Order.DESCENDING );

        //añadido context!!
        //añadido Activity para quitar llmamada static!!
        mCircularProgressTimer = new CircularProgressDrawable(startTime, MAX_TIME, CircularProgressDrawable.Order.DESCENDING, this);


        // Set a callback to update our circular progress timer
        mCircularProgressTimer.setCallback(mPieDrawableCallback);
        // Set a drawable object for our Imageview
        mCircularImageView.setImageDrawable(mCircularProgressTimer);


        //le decinmos que empiece!!!


        //otra opcion un countdown animat6ed conlistener:https://github.com/IvanRF/CountDownAnimation

        /* sacado a ametodo StarTCountDounaAnimationMia
        int startCount = 5;
        textCountdown = (TextView) findViewById(R.id.textcountdown);

        CountDownAnimation countDownAnimation = new CountDownAnimation(textCountdown, startCount);
        //elegimnoms una niamacionmas chula:
        Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        countDownAnimation.setAnimation(animationSet);
        //añadimos el listener a nosotros
        countDownAnimation.setCountDownListener(this);
        //y empezamos!!!
        countDownAnimation.start();
*/

        //aañdimos los listener a cancel

         circledImageViewOK = (CircledImageView) findViewById(R.id.circledImageViewOK);






        circledImageViewOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  Toast.makeText(getBaseContext(), "OK clicked",
                //    Toast.LENGTH_SHORT).show();

                //y eso siginifica que pasamos al siguiente

                mCircularProgressTimer.stop();


                //1º ue vibre

                Vibrator vibrator = (Vibrator) WorkoutGeneralPassingArgumentsActivity.this.getSystemService(WorkoutGeneralPassingArgumentsActivity.VIBRATOR_SERVICE);

                long[] vibrationPattern = {0, 500, 50, 300};


                //long[] vibrationPattern = { 0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500};

                //go go power rangers!!!vibrate patterns:
                // https://gearside.com/custom-vibration-patterns-mobile-devices/
                //hay que añadir un 0 al principio!!!

                //  long[] vibrationPattern = {0,150,150,150,150,75,75,150,150,150,150,450};


                //-a1 - don't repeat
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);


                RecibirFinTimerSinStatic();
            }
        });


        //lo deshabilitmos de momento!! solo se habilitara tras el countodown

        circledImageViewOK.setEnabled(false);

/*

        //vamos a ver los assets!!
        mAssets=this.getAssets();


        String [] Gifs_names = new String[0];
        try {
            Gifs_names=mAssets.list(GIFS1_FOLDER);
            Log.i(TAG,"encontrados :"+Gifs_names.length +" gifs");//funciona ok

            for (int i=0;i<Gifs_names.length;i++){

                Log.i(TAG,"que son :"+Gifs_names[i]);//funciona ok
                /*
                I/WOGeneral: encontrados :2 gifs
01-07 14:21:45.668 30047-30047/jrdv.mio.com.customnotification I/WOGeneral: que son :high_plank_shoulder_touches.gif
01-07 14:21:45.668 30047-30047/jrdv.mio.com.customnotification I/WOGeneral: que son :Jump Tuck  Side Plank

            }



        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"No encuentro ningunn GIF:");
        }


        //con la libreia Ion lo coloco aver que tal
        //TODO elegir el que hay que poner segun el ejercicio que toque


            /*
            http://stackoverflow.com/questions/3660209/display-animated-gif
            Ion.with(imgView)
            .error(R.drawable.default_image)
            .animateGif(AnimateGifMode.ANIMATE)
            .load("file:///android_asset/animated.gif");

    GifVew=(ImageView)findViewById(R.id.aniamtedgif_holder1);

        String GifPath="file://android_assets/animated_gif1/"+Gifs_names[0];

        String GifPath2="http://cloudyco.com/SCRATCH/SnakeSkull2.1.gif";
/*
        Ion.with(GifVew)
                .error(R.drawable.ic_pause)
                .animateGif(AnimateGifMode.ANIMATE)
                .load(GifPath);*///esto no consigo que funcione.....NPI


        /*
        //lo ponemos dentro del  StartNormalEjercico
        //lo cogemos el gif  del drawable folder!!!!

        GifVew = (ImageView) findViewById(R.id.aniamtedgif_holder1);
        String imageUri = "drawable:///" + R.drawable.a1;
        Log.e(TAG, "drawable file" + imageUri);


        Ion.with(GifVew)
                .error(R.drawable.ic_pause)
                .fitXY()
                .animateGif(AnimateGifMode.ANIMATE)
                // .load("android.resource://[packagename]" + R.drawable.optinscreen_map)
                .load("android.resource://jrdv.mio.com.customnotification/" + R.drawable.high_plank_shoulder_touches)
        ;
*/

    }

    //////////////////////////////////////////////////////////////////Elegir Ejercicio segun SQL///////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private  void ChooseRightEjercicio() {


        for (Ejercicio cn : ejerciciosChoosen) {
            String log = "Id: " + cn.get_id() + " ,Name: " + cn.get_name() + " ,GifName: " + cn.get_Gif_name() + " ,NameEnglish: " + cn.get_name_english() + " LeveL: " + cn.get_level();

            // Writing Contacts to log
            Log.d("Ejercicio LEVEL " + LevelChoosen + ": ", log);


        }

        //aqui tenemos una List con los ejercicios!!!
        //el numeor total de ejercicios de array se guarada en NumeroTotalEjercicios

        int EjercicioActual = NumeroTotalEjercicios - NuemroEjerciciosPendientes;
        //esto dara :4-4=0
        //4-3=1
        //4-2=2
        //4-1=3


        //Ejercicio EjercicioAhora=ejerciciosChoosen.get(0);

        if (EjercicioActual < NumeroTotalEjercicios) {

            Ejercicio EjercicioAhora = ejerciciosChoosen.get(EjercicioActual);

            StartNormalEjercico(EjercicioAhora);

            //restamos 1  alos ptes

            NuemroEjerciciosPendientes = NuemroEjerciciosPendientes - 1;

        }


    }

    //////////////////////////////////////////////////////////////////EMPEZAR Ejercicio segun SQL///////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void StartNormalEjercico(Ejercicio ejerciocioQueToca) {


        //ocultamos el timner y el cancel botn
        //saldran en cuanto em,pieze el ejercicio(acabe animacion!!)


        mCircularImageView.setVisibility(View.INVISIBLE);

        //y el cancel

        if (circledImageViewOK!=null) {
            circledImageViewOK.setVisibility(View.INVISIBLE);
        }

        //recibimos esto Para hacer ejercicio:

        String log = "Id: " + ejerciocioQueToca.get_id() + " ,Name: " + ejerciocioQueToca.get_name() + " ,GifName: "
                + ejerciocioQueToca.get_Gif_name() + " ,NameEnglish: " + ejerciocioQueToca.get_name_english() + " LeveL: "
                + ejerciocioQueToca.get_level();

        // Writing Contacts to log
        Log.d("TOCA " + LevelChoosen + ": ", log);


        // y una vez tenemos el Ejercicio Correcto empezamos:

        //colocamos los TEXTOs y el GIF correcto:

        Titulo.setText(ejerciocioQueToca.get_name());

        //lo cogemos el gif  del drawable folder!!!!

        //GifVew = (ImageView) findViewById(R.id.aniamtedgif_holder1);

        String mDrawableName = ejerciocioQueToca.get_Gif_name();
        int resID = getResources().getIdentifier(mDrawableName , "drawable", getPackageName());
        String GifPath = "android.resource://com.mio.jrdv.wearkout/"+resID;


        Ion.with(GifVew)
                .error(R.drawable.ic_pause)
                .fitXY()
                .animateGif(AnimateGifMode.ANIMATE)
                // .load("android.resource://[packagename]" + R.drawable.optinscreen_map)
                //.load("android.resource://jrdv.mio.com.customnotification/" + R.drawable.high_plank_shoulder_touches);
                //.load("android.resource://jrdv.mio.com.customnotification/R.drawable." + ejerciocioQueToca.get_Gif_name() + ".gif");
                .load(GifPath);


        StarTCountDounaAnimationMia();


        //cuando acabe debria empezar un delay o acabar la actitvoty si ya termino!!

    }

    private void StarTCountDounaAnimationMia() {

        //lo deshabilitmos de momento!! solo se habilitara tras el countodown

        if (circledImageViewOK!=null){

            circledImageViewOK.setEnabled(false);
            Log.e("Anulado ", "CLICK EN CANCEL");

        }



        //le decinmos que empiece!!!


        //otra opcion un countdown animat6ed conlistener:https://github.com/IvanRF/CountDownAnimation

        int startCount = 5;
        textCountdown = (TextView) findViewById(R.id.textcountdown);
        //para le tag:
        int tagNormal=1;

        CountDownAnimation countDownAnimation = new CountDownAnimation(textCountdown, startCount,tagNormal);
        //elegimnoms una niamacionmas chula:
        Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        countDownAnimation.setAnimation(animationSet);

        //añadimos el listener a nosotros
        countDownAnimation.setCountDownListener(this);
        //y empezamos!!!
        countDownAnimation.start();


    }


    //////////////////////////////////////////////////////////////////EMPEZAR DELAY Ejercicio  ///////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private void StartDelayanimation() {

        //lo deshabilitmos de momento!! solo se habilitara tras el countodown.

        circledImageViewOK.setEnabled(false);
        //y lo ocuyltamos

        circledImageViewOK.setVisibility(View.INVISIBLE);

        //mostramos el heratRate:

        HeartRateText.setVisibility(View.VISIBLE);


        //poenmos el gif de heart beat

        Ion.with(GifVew)
                .error(R.drawable.ic_pause)
                .fitXY()
                .animateGif(AnimateGifMode.ANIMATE)
                // .load("android.resource://[packagename]" + R.drawable.optinscreen_map)
                 .load("android.resource://com.mio.jrdv.wearkout/" + R.drawable.normal_heart_rate1);









        //ocultmao le progressbarview

        mCircularImageView.setVisibility(View.INVISIBLE);



        //otra opcion un countdown animat6ed conlistener:https://github.com/IvanRF/CountDownAnimation

        int startCount = EjercicioDelayTime;
       // textCountdown = (TextView) findViewById(R.id.textcountdown);
        //disminuimos el tamaño del delay y la posicion

        textCountdown.setTextSize(65);


        textCountdown.setGravity(Gravity.CENTER | Gravity.TOP);
        //para le tag:
        int tagDelay=2;

        CountDownAnimation countDownAnimation = new CountDownAnimation(textCountdown, startCount,tagDelay);

        //añadimos el listener a nosotros en el listener le diremos que si es tag=2 vaya al ChooseRightEjercico para que acabe!!
        countDownAnimation.setCountDownListener(this);
        //y empezamos!!!
        countDownAnimation.start();


    }


//////////////////////////////////////////////////////////////////listener del cpountdown///////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCountDownEnd(CountDownAnimation animation) {

        //lo habilitmos de momento!! solo se habilitara tras el countodown

        circledImageViewOK.setEnabled(true);





        if (animation.getmTag()==1){
            //es un tag 1 es una aniamcionde countdown  deinicio ejercios 5 segs:


            //cuando acabe la aniumacion del timer:

            //1º que vibre

            Vibrator vibrator = (Vibrator) WorkoutGeneralPassingArgumentsActivity.this.getSystemService(WorkoutGeneralPassingArgumentsActivity.VIBRATOR_SERVICE);


            //go go power rangers!!!vibrate patterns:
            // https://gearside.com/custom-vibration-patterns-mobile-devices/
            //hay que añadir un 0 al principio!!!

            long[] vibrationPattern = {0, 150, 150, 150, 150, 75, 75, 150, 150, 150, 150, 450};


            //-1 = don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

            //2º)le decinmos que empiece!!!
            mCircularProgressTimer.start();

            //volovemos a mostrat la  progressbarview

            mCircularImageView.setVisibility(View.VISIBLE);

            //y el cancel

            circledImageViewOK.setVisibility(View.VISIBLE);

        }

        if(animation.getmTag()==2){

            //finito delay ..siguiente ejercicio


            //volovemos a mostrat la  progressbarview

            mCircularImageView.setVisibility(View.VISIBLE);

            //y el cancel

            circledImageViewOK.setVisibility(View.VISIBLE);


            //y volvemos a poner el tamaño del countdown

            //disminuimos el tamaño del delay:
            textCountdown.setTextSize(130);
            //y la gravity

            textCountdown.setGravity(Gravity.CENTER);

            //ocultamo el heratRate:

            HeartRateText.setVisibility(View.INVISIBLE);


            ChooseRightEjercicio();
        }



    }

    @Override
    public void onCountDown1Segundo(CountDownAnimation countDownAnimation) {

        //esto nos notificara cada segundon para hacer que vibre en elDELAY

        if(countDownAnimation.getmTag()==2) {

            //1º que vibre

            Vibrator vibrator = (Vibrator) WorkoutGeneralPassingArgumentsActivity.this.getSystemService(WorkoutGeneralPassingArgumentsActivity.VIBRATOR_SERVICE);


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

    @Override
    protected void onStop() {
        super.onStop();

        //al salir quitamos el listener para ahorrar bateria!!!

        mSensorManager.unregisterListener(this);

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

/*

//no lo uso  mas STATIC es un FOLLON!!!

    public static void RecibirFinTimer(Context mcontext) {

        //aqui recibiremo el fin del timer!a1

        Log.e("Recibido", "fin de timer en WorkouViewActivity");

        //Si quedan ejercicos ptes

        if(NuemroEjerciciosPendientes>=1){

            //


        }




        //aqui habria que decidir si realmente se acabaron
        //TODOS los EJRECICIOS  o no


        if (TUTTOFINITO) {


            // Create and start intent for this activity
            Intent intent = new Intent(mcontext, StartChoosingSessionActivity.class);

            mcontext.startActivity(intent);

            //y nos cerramos


            activity.finish();

        } else {

            //aun no hemos acabado!!!


        }


    }

*/
    public   void RecibirFinTimerSinStatic(){
        Log.e("Recibido", "fin de timer en WorkouViewActivity sin STATIC!!!");



        //Si quedan ejercicos ptes

        if(NuemroEjerciciosPendientes>=1){

            //
          //  ChooseRightEjercicio();//lompasamos al listener del countdown

           // en vez de eso que inicie el timer de DELAY TIME con cardiac gif y medidicon pulso

            StartDelayanimation();


        }

        else {
            TUTTOFINITO=true;




        }




        if (TUTTOFINITO) {


            //aqui chequeamos el nuemro de repeticiones!!!

            if (NumeroRepeticiones==RepeticioesYaHechas){
                // Create and start intent for this activity
                Intent intent = new Intent(this, StartChoosingSessionActivity.class);

                this.startActivity(intent);

                //y nos cerramos


                activity.finish();


            }

            //si aun no...

            else {

                //volvemos a dcir que etan pendientes

                NuemroEjerciciosPendientes = ejerciciosChoosen.size();

                TUTTOFINITO=false;
                RepeticioesYaHechas=RepeticioesYaHechas+1;

            }


        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////del cardiac listener!!////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onSensorChanged(SensorEvent event) {

        HeartRateText.setText((int) event.values[0]+" bpm");


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
