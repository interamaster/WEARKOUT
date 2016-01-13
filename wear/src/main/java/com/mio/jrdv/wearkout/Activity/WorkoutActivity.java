package com.mio.jrdv.wearkout.Activity;

import android.app.Activity;
import android.os.Bundle;

import com.mio.jrdv.wearkout.NotificationBuilder;
import com.mio.jrdv.wearkout.model.FitnessData;


public class WorkoutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationBuilder.update(this , FitnessData.getInstance().getState());
        finish();
    }
}
