package com.mio.jrdv.wearkout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mio.jrdv.wearkout.model.FitnessData;


/**
 * Created by esq00931 on 04/01/2016.
 * To listen for tapped actions, you created an ActionReceiver class.
 * This class enables your application to receive intents sent by the notification manager when an action
 * has been tapped.
 * It essentially enables your application to handle events.
 * When an intent is received, the onReceive() method is called; hence, you need to override this.
 * For each received action, you updated the workout state to allow the NotificationBuilder to
 * determine the next action to build.
 */
public class ActionReceiver extends BroadcastReceiver {

    /*
    To inform the activity with the action that was tapped, you created four implicit intents.
     You added a string value in the constructor to indicate the intent action to perform.
      This value tells Android to filter for intents that match our string value
      (which you will implement using an IntentFilter in the manifest file).
     */

    public static final String ACTION_START = "jrdv.com.mio.customnotification.ACTION_START";
    public static final String ACTION_PAUSE =  "jrdv.com.mio.customnotification.ACTION_PAUSE";
    public static final String ACTION_RESUME = "jrdv.com.mio.customnotification.ACTION_RESUME";
    public static final String ACTION_RESET =  "jrdv.com.mio.customnotification.ACTION_RESET";

    public static final Intent START_INTENT = new Intent(ACTION_START);
    public static final Intent PAUSE_INTENT = new Intent(ACTION_PAUSE);
    public static final Intent RESUME_INTENT = new Intent(ACTION_RESUME);
    public static final Intent RESET_INTENT = new Intent(ACTION_RESET);


    //creo otro pàra saber que ha cabado el timer!!!
    public static final String ACTION_FINISHED = "jrdv.com.mio.customnotification.ACTION_FINISHED";
    public static final Intent FINISHED_INTENT = new Intent(ACTION_FINISHED);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_PAUSE)) {
            FitnessData.getInstance().setState(FitnessData.WorkoutState.PAUSED);
        }
        if (intent.getAction().equals(ACTION_START) ||
                intent.getAction().equals(ACTION_RESUME)) {
            FitnessData.getInstance().setState(FitnessData.WorkoutState.RUNNING);
        }
        if (intent.getAction().equals(ACTION_RESET)) {
            FitnessData.getInstance().setState(FitnessData.WorkoutState.STOPPED);
        }

        //creo otro pàra saber que ha cabado el timer!!!
        if (intent.getAction().equals(ACTION_FINISHED)) {
            FitnessData.getInstance().setState(FitnessData.WorkoutState.FINISHED);
        }
        NotificationBuilder.update(context, FitnessData.getInstance().getState());
    }
}

