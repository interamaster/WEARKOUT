package com.mio.jrdv.wearkout.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mio.jrdv.wearkout.R;

import preference.WearPreferenceActivity;

/**
 * Created by joseramondelgado on 13/01/16.
 */
public class MyPreferenceActivity extends WearPreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.layout.preference);





    }


    public void salirclick(View view) {

        //salimos

        // Create and start intent for this activity
        Intent intent = new Intent(this, StartChoosingSessionActivity.class);

        this.startActivity(intent);

        //y nos cerramos


        this.finish();
    }
}
