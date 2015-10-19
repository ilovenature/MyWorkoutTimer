package com.xlab.myworkouttimer.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.xlab.myworkouttimer.Activity.MainActivity;
import com.xlab.myworkouttimer.Activity.SettingActivity;
import com.xlab.myworkouttimer.Utils.CDTimerManager;

public class TimerService extends Service implements CDTimerManager.onTimerManagerListener {
    private final String TAG = getClass().getSimpleName();
    // Timer Status
    public static final int START = 0;
    public static final int PAUSE = 1;
    public static final int FINISH = 2;

    // workout status
    private final int READY = 0;
    private final int REST = 1;
    private final int WORKOUT = 2;
    private int statusOfWorkout = 0;

    private CountDownTimer readyTimer;
    private CountDownTimer restTimer;
    private CountDownTimer workoutTimer;
    private long readyTime;
    private long restTime;
    private long workoutTime;

    private int numberOfSets;
    private int currentSet = 0;

    private ResultReceiver resultReceiver;
    private TimerServiceBinder timerServiceBinder = new TimerServiceBinder();


    private CDTimerManager timerManager;
    public TimerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = getSharedPreferences(SettingActivity.SETTING_PREFERENCE, 0);

        readyTime = settings.getInt(SettingActivity.READY_TIME, 10);
//        readyTimer = getReadyTimer(readyTime * 1000 + 100);

        restTime = settings.getInt(SettingActivity.REST_TIME, 5);
//        restTimer = getRestTimer(restTime * 1000 + 100);

        workoutTime = settings.getInt(SettingActivity.WORKOUT_TIME, 10);
//        workoutTimer = getWorkoutTimer(workoutTime * 1000 + 100);

        numberOfSets = settings.getInt(SettingActivity.NUMBER_OF_SETS, 3);

//        readyTimer.start();

        timerManager = new CDTimerManager(this);
        timerManager.setCurrentTime(readyTime);
        timerManager.startTimer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultReceiver = intent.getParcelableExtra("result_receiver");
        Bundle bundle = new Bundle();
        bundle.putBinder("service_binder", timerServiceBinder);
        resultReceiver.send(MainActivity.GETBINDER, bundle);
        return super.onStartCommand(intent, flags, startId);
    }

//    private CountDownTimer getReadyTimer(long time) {
//        CountDownTimer countDownTimer = new CountDownTimer(time, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                readyTime = millisUntilFinished;
//                Bundle bundle = new Bundle();
//                bundle.putLong("millis_time", millisUntilFinished);
//                resultReceiver.send(MainActivity.GETTIME, bundle);
//            }
//
//            @Override
//            public void onFinish() {
//                readyTime = 0;
//                Bundle bundle = new Bundle();
//                bundle.putLong("millis_time", 0);
//                resultReceiver.send(MainActivity.GETTIME, bundle);
//
//                status = 1;
//            }
//
//
//        };
//        return countDownTimer;
//    }
//
//    private CountDownTimer getRestTimer(long time) {
//        CountDownTimer countDownTimer = new CountDownTimer(time, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//        return countDownTimer;
//    }
//
//    private CountDownTimer getWorkoutTimer(long time) {
//        CountDownTimer countDownTimer = new CountDownTimer(time, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//        return countDownTimer;
//    }


    @Override
    public void onTick(long time) {
        Bundle bundle = new Bundle();
        bundle.putLong("millis_time", time);
        resultReceiver.send(MainActivity.GETTIME, bundle);
    }

    @Override
    public void onFinish() {
        Bundle bundle = new Bundle();
        bundle.putLong("millis_time", 0);
        resultReceiver.send(MainActivity.GETTIME, bundle);
        switch (statusOfWorkout){
            case READY:
                timerManager.setCurrentTime(workoutTime);
                timerManager.startTimer();
                statusOfWorkout = WORKOUT;
                bundle.putString("timer_message", "workout");
                break;
            case REST:
                timerManager.setCurrentTime(workoutTime);
                timerManager.startTimer();
                statusOfWorkout = WORKOUT;
                bundle.putString("timer_message", "workout");
                break;
            case WORKOUT:
                currentSet++;
                if (currentSet < numberOfSets){     //表示未達循環次數
                    timerManager.setCurrentTime(restTime);
                    timerManager.startTimer();
                    statusOfWorkout = REST;
                    bundle.putString("timer_message", "rest");
                }else{                              //表示已達循環次數，運動結束
                    resultReceiver.send(MainActivity.GETFINISH, null);
                }
                bundle.putInt("current_set", currentSet);
                resultReceiver.send(MainActivity.GETCURRENTSETS, bundle);
                break;
        }
        resultReceiver.send(MainActivity.GETMESSAGE, bundle);
    }

    /**
     * 接收來自activity的訊息
     */
    public class TimerServiceBinder extends Binder implements TimerServiceInterface {
        @Override
        public void setStatus(int status) {
            Log.i(TAG, "status:" + status);
            switch (status) {
                case START:
                    timerManager.startTimer();
                    break;
                case PAUSE:
                    timerManager.pauseTimer();
                    break;
                case FINISH:
                    timerManager.pauseTimer();
                    stopSelf();
                    break;
            }
        }
    }

    public interface TimerServiceInterface {
        public void setStatus(int status);
    }

}
