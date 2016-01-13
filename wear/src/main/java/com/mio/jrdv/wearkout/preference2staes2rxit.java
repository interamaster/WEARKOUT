package com.mio.jrdv.wearkout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

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

    }
}
