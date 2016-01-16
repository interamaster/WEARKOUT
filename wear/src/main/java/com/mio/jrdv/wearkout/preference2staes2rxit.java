package com.mio.jrdv.wearkout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

import com.mio.jrdv.wearkout.Activity.StartChoosingSessionActivity;

import preference.WearTwoStatePreference;

/**
 * Created by joseramondelgado on 14/01/16.
 */
public class preference2staes2rxit  extends WearTwoStatePreference{


    public preference2staes2rxit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public void onPreferenceClick(@NonNull final Context context) {


        Log.e("desde el pref","pulsado en 2 states");






        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //ponemos el valord e pref ya choosen a yes!!!
        preferences.edit().putBoolean("pref_choosen",true).apply();

        final boolean currentState = preferences.getBoolean("pref_choosen", false);
        Log.e("desde el pref","el boolena pref_choosen es: "+currentState);

        final String valor_idioma=preferences.getString("language",null);
        if (valor_idioma==null){
            //ponemos el valord e pref  !!!
            preferences.edit().putString("language","en").apply();

        }


        Log.e("desde el pref","el string del idioma es: "+valor_idioma);


        //son todos Stringd no int!!!
        final String valor_delay=preferences.getString("delay_time",null);

        if (valor_delay==null){
            //ponemos el valord e pref  !!!
            preferences.edit().putString("delay_time","40").apply();

        }
        Log.e("desde el pref","el string del dealy es: "+valor_delay);


        final String valor_ejercioco_time=preferences.getString("ejercicio_time",null);
        if (valor_ejercioco_time==null){
            //ponemos el valord e pref  !!!
            preferences.edit().putString("ejercicio_time","40").apply();

        }

        Log.e("desde el pref","el string del tiepo ejercico es: "+valor_ejercioco_time);

        final String valor_num_repes=preferences.getString("numero_repeticiones",null);

        if (valor_num_repes==null){
            //ponemos el valord e pref  !!!
            preferences.edit().putString("numero_repeticiones","2").apply();

        }
        Log.e("desde el pref","el string delnumeor de repes es: "+valor_num_repes);

        final String valor_repeticion_time=preferences.getString("tiempo_entre_repeticiones",null);
        if (valor_repeticion_time==null){
            //ponemos el valord e pref  !!!
            preferences.edit().putString("tiempo_entre_repeticiones","120").apply();

        }

        Log.e("desde el pref","el string del timep estre repes es: "+valor_repeticion_time);




        // Create and start intent for this activity
        Intent intent = new Intent(context, StartChoosingSessionActivity.class);

        context.startActivity(intent);

        //y nos cerramos

        ((Activity)context).finish();
       // context.finish();

    }
}
