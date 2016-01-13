package com.mio.jrdv.wearkout.model;

/**
 * Created by esq00931 on 04/01/2016.
 *  This class will be used to persist data, notably the three workout states
 *  (STOPPED, RUNNING and PAUSED), to save the last timer value when the main
 *  activity pauses or to retrieve it upon a resume.
 */
public class FitnessData {
    public enum WorkoutState {
        STOPPED,
        RUNNING,
        PAUSED,

        //creo otro pàra saber que ha cabado el timer!!!
        FINISHED};

    private static FitnessData mInstance;
    private static WorkoutState mWorkoutState;
    private static int mLastTime;
// Get a Singleton instance
// Get a Singleton instance
public static FitnessData getInstance() {
    if (mInstance == null) {
        mWorkoutState = WorkoutState.STOPPED;
        mInstance = new FitnessData();
    }
    return mInstance;
}
    public void setState(WorkoutState state) {
        mWorkoutState = state;
    }
    public WorkoutState getState() {
        return mWorkoutState;
    }
    public void setLastTime(int value) {
        mLastTime = value;
    }
    public int getLastTime() {
        return mLastTime;
    }
    public boolean isStopped() {
        return (mWorkoutState == WorkoutState.STOPPED);
    }
    public boolean isPaused() {
        return (mWorkoutState == WorkoutState.PAUSED);
    }
    public boolean isRunning() {
        return (mWorkoutState == WorkoutState.RUNNING);
    }



    //creo otro pàra saber que ha cabado el timer!!!
    public boolean isFinished() {
        return (mWorkoutState == WorkoutState.FINISHED);
    }

}
