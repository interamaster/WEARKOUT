package com.mio.jrdv.wearkout;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.mio.jrdv.wearkout.Activity.WorkoutViewActivity;
import com.mio.jrdv.wearkout.model.FitnessData;


/**
 * Created by esq00931 on 04/01/2016.
 * In the NotificationBuilder.java file, you programmatically created
 * actions by specifying an icon, a title and a PendingIntent object based on the workout state.
 * The PendingIntent object will be used to send a broadcast at a later time when the user
 * taps on the corresponding action.
 */
public class NotificationBuilder {
    public static final int NOTIFICATION_ID = 0;


    private static final int ID_START = 1;
    private static final int ID_PAUSE = 2;
    private static final int ID_RESUME = 3;
    private static final int ID_RESET = 4;


    //creo otro pàra saber que ha cabado el timer!!!
    private static final int ID_FINISHED = 5;



    private final Context mContext;
    public NotificationBuilder(Context context) {

        this.mContext = context;
    }
    public Notification buildNotification(FitnessData.WorkoutState workoutState) {
        // Set up our notification contents and icon
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                .setContentTitle("EJERCICIO a1")
                .setContentText("Flexiones!!!")
                .setSmallIcon(R.drawable.ic_bicep);
        // Create an intent that will be launched inside of an activity view,
        // that is, WorkoutActivity.class to display the custom notification.
        Intent workoutIntent = new Intent(mContext, WorkoutViewActivity.class);
        // The intent needs to be packaged into a pending intent so that the
        // notification service can fire it on our behalf.


        /*
         /* Create two intents (workoutPendingIntent y runPendingIntent)
          that will be fired when the user taps on the
        *  corresponding notification action. The intent needs to be packaged
        *  into a pending intent so that the notification service can fire it on
        *  our behalf.
        *
        *  Note: Since we are using multiple distinct PendingIntent objects
        *  (i.e. NotificationActivity.class) in this example, we need to specify a
        *  unique RequestCode integer in getActivity(). In this case, we will
        *  use 0 and a1 respectively.
        */

        PendingIntent workoutPendingIntent =
                PendingIntent.getActivity(mContext, 0, workoutIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        // Build custom actions
        String actionStr = "";
        int icon = 0;
        //The PendingIntent object will be used to send a broadcast at a later time
        // when the user
       // taps on the corresponding action.

        //osea que le pendingIntent(runPendingIntent)sera el llamadao al pulsar algunos de los botones
        //stop /resume/play y a parte el de reset!!
        // y desde aqui lo que hago es crear el inntent correpsondiente!!!

        PendingIntent runPendingIntent = null;


        switch (workoutState)
        {
            case STOPPED:
                icon = R.drawable.ic_play;
                actionStr = "Start";

                //public static PendingIntent getBroadcast (Context context, int requestCode, Intent intent, int flags)
               // Added in API level a1
               // Retrieve a PendingIntent that will perform a broadcast,
                runPendingIntent = PendingIntent.getBroadcast(
                        mContext, ID_START, ActionReceiver.START_INTENT,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                break;
            case RUNNING:
                icon = R.drawable.ic_pause;
                actionStr = "Pause";
                runPendingIntent = PendingIntent.getBroadcast(
                        mContext, ID_PAUSE, ActionReceiver.PAUSE_INTENT,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            case PAUSED:
                icon = R.drawable.ic_play;
                actionStr = "Resume";
                runPendingIntent = PendingIntent.getBroadcast(
                        mContext, ID_RESUME, ActionReceiver.RESUME_INTENT,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                break;


            case FINISHED:
                icon = R.drawable.ic_play;
                actionStr = "Start";
                runPendingIntent = PendingIntent.getBroadcast(
                        mContext, ID_FINISHED, ActionReceiver.START_INTENT,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                break;


        }
        // Reset timer intent
        //In the same manner, you also created a PendingIntent to monitor when the user taps the Reset action:
        PendingIntent resetPendingIntent = PendingIntent.getBroadcast(
                mContext, ID_RESET, ActionReceiver.RESET_INTENT,
                PendingIntent.FLAG_UPDATE_CURRENT);


        // Set up background, and custom card size, and set an intent to launch
        // inside an activity view when displaying this notification
        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setBackground(BitmapFactory.decodeResource(
                                mContext.getResources(), R.drawable.pushup_background))
                        .setCustomSizePreset(Notification.WearableExtender.SIZE_XSMALL)

                        /*
                        Finally, to display two actions for your notification, you passed each PendingIntent object
                        to separate addAction() methods. Actions are displayed first by the notification system
                        by swiping left when the notification appears.

                         */
                        .addAction(new NotificationCompat.Action.Builder(icon,
                                actionStr, runPendingIntent).build())
                        .addAction(new NotificationCompat.Action.Builder(
                                R.drawable.ic_reset, "Reset",
                                resetPendingIntent).build())
                        .setDisplayIntent(workoutPendingIntent);
        // Add wearable-specific features to our builder
        builder.extend(wearableExtender);
        return builder.build();
    }
    public static void update(Context context, FitnessData.WorkoutState state) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationBuilder builder = new NotificationBuilder(context);

        //desde aqui mismo se crea el builder  de la notificacion con los 2 actions: uno para pause/play/resume y
        //el otro ara reset!!!
        Notification notification = builder.buildNotification(state);


        // Get an instance of the NotificationManagerCompat service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        // Build the notification and notify it using notification manager.
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}


