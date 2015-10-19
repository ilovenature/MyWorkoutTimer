package com.xlab.myworkouttimer.Utils;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by user on 2015/10/6.
 */
public class CDTimerManager {
    private final String TAG = getClass().getSimpleName();
    private CountDownTimer currentTimer;
    private long currentTime;
    private onTimerManagerListener onTimerManagerListener;

    public CDTimerManager(Context context){
        onTimerManagerListener = (CDTimerManager.onTimerManagerListener) context;
        Log.i(TAG, "create timer manager");
    }

    public void setCurrentTime(long currenttime) {
        Log.i(TAG, "set current time: " + currenttime);
        currentTime = currenttime * 1000 + 100;
    }

    public void startTimer() {
        Log.i(TAG, "start timer");
        currentTimer = getCountDownTimer(currentTime);
        currentTimer.start();
    }

    public void pauseTimer() {
        currentTimer.cancel();
    }

    private CountDownTimer getCountDownTimer(long time) {
        CountDownTimer timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                currentTime = millisUntilFinished;
                onTimerManagerListener.onTick(millisUntilFinished);
                Log.i(TAG, "current time: " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                currentTime = 0;
                onTimerManagerListener.onFinish();

            }
        };
        return timer;
    }

    public interface onTimerManagerListener{
        public void onTick(long time);
        public void onFinish();
    }
}
