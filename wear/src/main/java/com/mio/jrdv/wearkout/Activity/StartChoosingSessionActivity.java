package com.mio.jrdv.wearkout.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.mio.jrdv.wearkout.R;
import com.mio.jrdv.wearkout.SQL.CalesteniaDataBaseHelper;
import com.mio.jrdv.wearkout.model.Ejercicio;

import java.util.List;


/**
 * Created by esq00931 on 05/01/2016.
 */
public class StartChoosingSessionActivity extends Activity {


    private static final int ID_START_EASY = 0;
    private static final int ID_START_MEDIUM = 1;
    private static final int ID_START_HARD = 2;
    private static final int ID_START_SETTINGS = 3;


    //pasamos en el intent el LEVEL y los timepos de ejerciocis y delay


    private static final String INTENT_KEY_LEVEL_CHOOSEN = "LEVEL";

    private static final String INTENT_KEY_TIME_EJERCICIO = "TIME";

    private static final String INTENT_KEY_TIME_DELAY = "TIME_DELAY";
    private static final String INTENT_KEY_REPETICION_NUMBER = "REP_NUMBER";

    //para el longpress y que se pueda salkir de la pp

    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;


    //para los valores recueprados de las prefs

    int ejercicio_time, ejercicio_delay, nuemro_repes;
    private String idiomaElegidoEnPrefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mDismissOverlay = (DismissOverlayView)
                findViewById(R.id.dismiss_overlay);

        mDismissOverlay.setIntroText(R.string.long_press_intro);

        mDismissOverlay.showIntroIfNecessary();


        //recuepermos valores àra los intent!!!


        /*
        Pasar de String a int

        Utilizamos el método parseInt de la clase Integer.
             String cadena = "1234";
            int numero = 0;
             numero = Integer.parseInt(cadena)
         */


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


        boolean aready_choosen_prefs = preferences.getBoolean("pref_choosen", false);
        if (!aready_choosen_prefs) {

            //si aun no se eligio nunca los settings nos manda a ellos del tiron:

            startActivity(new Intent(StartChoosingSessionActivity.this,
                    MyPreferenceActivity.class));
            this.finish();
        }


        ejercicio_time = Integer.parseInt(preferences.getString("ejercicio_time", String.valueOf(40)));//  //si no existen pongo los defaults values!!!
        ejercicio_delay = Integer.parseInt(preferences.getString("delay_time", String.valueOf(40)));//si no existen pongo los defaults values!!!
        nuemro_repes = Integer.parseInt(preferences.getString("numero_repeticiones", String.valueOf(2)));//  //si no existen pongo los defaults values!!!
        idiomaElegidoEnPrefs = preferences.getString("language", "en"); //si no existen pongo los defaults values!!!


        Log.e("Valore del intent", " delay:  " + ejercicio_delay + " time ejercicio: " + ejercicio_time + " num repes: " + nuemro_repes + " idioma: " + idiomaElegidoEnPrefs);




        /*
        //   programatically: funcioan igual
        mDismissOverlay = new DismissOverlayView(this);
        FrameLayout parentFrame = (FrameLayout) findViewById(R.id.parentframe);



        // Add DismissOverlayView to cover the whole screen

        parentFrame.addView(mDismissOverlay,new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
*/

        // Set up a GridViewPager with two CircledImageViews, one to start the game
        // and another to provide sample game information.
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        final CircledImageAdapter pagerAdapter = new CircledImageAdapter(this);
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);


        pagerAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch ((int) v.getTag()) {
                    case ID_START_EASY:
                        // startActivity(new Intent(StartChoosingSessionActivity.this,
                        //     WorkutGeneralActivity.class));

                        //TODO ESTOS PARAMETROS DEBERA COGERLOS DE LAS PREF!!
                        // Build extras with passed in parameters
                        Bundle extras = new Bundle();
                        extras.putString(INTENT_KEY_LEVEL_CHOOSEN, "EASY");
                        extras.putInt(INTENT_KEY_TIME_EJERCICIO, ejercicio_time);
                        extras.putInt(INTENT_KEY_TIME_DELAY, ejercicio_delay);
                        extras.putInt(INTENT_KEY_REPETICION_NUMBER, nuemro_repes);

                        // Create and start intent for this activity
                        Intent intent = new Intent(StartChoosingSessionActivity.this, WorkoutGeneralPassingArgumentsActivity.class);
                        intent.putExtras(extras);
                        StartChoosingSessionActivity.this.startActivity(intent);


                        finish();
                        break;
                    case ID_START_MEDIUM:
                        /*
                        startActivity(new Intent(StartChoosingSessionActivity.this,
                                WorkutGeneralActivity.class));*/
                        // Build extras with passed in parameters
                        Bundle extras2 = new Bundle();
                        extras2.putString(INTENT_KEY_LEVEL_CHOOSEN, "Sentadillas");
                        extras2.putString(INTENT_KEY_TIME_EJERCICIO, "Piraguismo");

                        // Create and start intent for this activity
                        Intent intent2 = new Intent(StartChoosingSessionActivity.this, WorkoutGeneralPassingArgumentsActivity.class);
                        intent2.putExtras(extras2);
                        StartChoosingSessionActivity.this.startActivity(intent2);


                        finish();
                        break;

                    case ID_START_HARD:
                        startActivity(new Intent(StartChoosingSessionActivity.this,
                                WorkutGeneralActivity.class));
                        finish();
                        break;


                    case ID_START_SETTINGS:
                        startActivity(new Intent(StartChoosingSessionActivity.this,
                                MyPreferenceActivity.class));
                        finish();
                        break;
                }
            }
        });
        pager.setAdapter(pagerAdapter);


        //ahora creamios la SQL con los valores oroginales y le damos unos vlaores por defecto a las shared pref de EXERCICE_TIME,EXERCICE_DELAY y EXERCICIE_NUMERO_REPETICIONES


        initializeSQL();


        initializePREFS();


    }


    private void initializeSQL() {

        CalesteniaDataBaseHelper dbhandler = new CalesteniaDataBaseHelper(this);

        if (dbhandler.getEjerciciosCount() > 1) {

            Log.d("tabla ya creadas ", "  ..");


            //probamos a filter por LEVEL:OK


            /*
            // Reading all contacts
            Log.d("Reading: ", "Reading all contacts..");
            List<Ejercicio> ejerciciosPRO = dbhandler.getAllEjerciciosLEVEL("PRO");

            for (Ejercicio cn : ejerciciosPRO) {
                String log = "Id: "+cn.get_id()+" ,Name: " + cn.get_name() + " ,GifName: " + cn.get_Gif_name() + " ,NameEnglish: "+cn.get_name_english() + " LeveL: "+cn.get_level();

                // Writing Contacts to log
                Log.d("Ejercicio LEVEL PRO:", log);
                /*
                ok:

            D/Ejercicio LEVEL PRO:: Id: 7 ,Name: Flexiones ,GifName: high_plank_shoulder_touches ,NameEnglish: flections LeveL: PRO
            D/Ejercicio LEVEL PRO:: Id: 8 ,Name: Abdominales ,GifName: android_er.gif ,NameEnglish: abd LeveL: PRO
            D/Ejercicio LEVEL PRO:: Id: 9 ,Name: Abdmoniales superiores ,GifName: jump_tuck_side_plank ,NameEnglish: Superior Abs LeveL: PRO

            }
              */

            return;
        }

        /**
         * CRUD Operations
         * */
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");

        //los EASY:
        dbhandler.addEjercicio(new Ejercicio("Flexiones", "high_plank_shoulder_touches", "flections", "EASY"));
        dbhandler.addEjercicio(new Ejercicio("Abdominales", "android_er.gif", "abd", "EASY"));
        dbhandler.addEjercicio(new Ejercicio("Abdmoniales superiores", "jump_tuck_side_plank", "Superior Abs", "EASY"));


        //los MID:
        dbhandler.addEjercicio(new Ejercicio("Flexiones", "high_plank_shoulder_touches", "flections", "MID"));
        dbhandler.addEjercicio(new Ejercicio("Abdominales", "android_er.gif", "abd", "MID"));
        dbhandler.addEjercicio(new Ejercicio("Abdmoniales superiores", "jump_tuck_side_plank", "Superior Abs", "MID"));

        //los PRO:
        dbhandler.addEjercicio(new Ejercicio("Flexiones", "high_plank_shoulder_touches", "flections", "PRO"));
        dbhandler.addEjercicio(new Ejercicio("Abdominales", "android_er.gif", "abd", "PRO"));
        dbhandler.addEjercicio(new Ejercicio("Abdmoniales superiores", "jump_tuck_side_plank", "Superior Abs", "PRO"));


        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Ejercicio> ejercicios = dbhandler.getAllEjercicios();

        for (Ejercicio cn : ejercicios) {
            String log = "Id: " + cn.get_id() + " ,Name: " + cn.get_name() + " ,GifName: " + cn.get_Gif_name() + " ,NameEnglish: " + cn.get_name_english() + " LeveL: " + cn.get_level();

            // Writing Contacts to log
            Log.d("Ejercicio: ", log);
        }

    }

    private void initializePREFS() {
    }

    public class CircledImageAdapter extends GridPagerAdapter {
        private final Context mContext;
        private View.OnClickListener mListener;

        private View.OnLongClickListener mlongListener;

        public CircledImageAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return 4;
        }

        @Override
        public int getCurrentColumnForRow(int row, int currentColumn) {
            return currentColumn;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int row, int col) {
            final View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.gridviewpager_item, viewGroup, false);
            final CircledImageView circleView =
                    (CircledImageView) view.findViewById(R.id.circled_image);
            final TextView txtLabel = (TextView) view.findViewById(R.id.label);

            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            String idiomapref=preferences.getString("language","es");


            switch (col) {
                case ID_START_EASY: {
                    // Set a tag used to launch correct activity
                    circleView.setTag(ID_START_EASY);
                    circleView.setImageResource(R.drawable.ic_reset);

                    if (idiomapref.equals("es")) {


                        txtLabel.setText("Novato");

                    } else {
                        txtLabel.setText("Easy");

                    }

                    }
                    break;
                    case ID_START_MEDIUM: {
                        // Set a tag used to launch correct activity
                        circleView.setTag(ID_START_MEDIUM);
                        circleView.setImageResource(R.drawable.ic_play);

                        if (idiomapref.equals("es")) {


                            txtLabel.setText("Normal");

                        } else {
                            txtLabel.setText("Medium");

                        }
                    }
                    break;
                    case ID_START_HARD: {
                        // Set a tag used to launch correct activity
                        circleView.setTag(ID_START_HARD);
                        circleView.setImageResource(R.drawable.ic_bicep);
                        if (idiomapref.equals("es")) {


                            txtLabel.setText("Calestesico");

                        } else {
                            txtLabel.setText("PRO");

                        }

                    }
                    break;

                    case ID_START_SETTINGS: {
                        // Set a tag used to launch correct activity
                        circleView.setTag(ID_START_SETTINGS);
                        circleView.setImageResource(R.drawable.ic_pause);

                        if (idiomapref.equals("es")) {


                            txtLabel.setText("Ajustes");

                        } else {
                            txtLabel.setText("Settings");

                        }

                    }
                    break;
                }
                circleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onClick(v);
                        }


                    }


                });

                circleView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if (mlongListener != null) {
                            mlongListener.onLongClick(v);
                        }
                        mDismissOverlay.show();
                        return true;
                    }
                });
                viewGroup.addView(view);
                return view;
            }

            @Override
            public void destroyItem (ViewGroup viewGroup,int i, int i1, Object o){
                viewGroup.removeView((View) o);

            }

            @Override
            public boolean isViewFromObject (View view, Object o){
                return view.equals(o);
            }

        public void setOnClickListener(View.OnClickListener listener) {
            mListener = listener;
        }

        public void setOnLongClickListener(View.OnLongClickListener Longlistener) {
            mlongListener = Longlistener;


        }


    }


}

