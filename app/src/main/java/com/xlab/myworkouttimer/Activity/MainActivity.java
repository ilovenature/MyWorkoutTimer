package com.xlab.myworkouttimer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xlab.myworkouttimer.R;
import com.xlab.myworkouttimer.Service.TimerService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 此專案利用ResultReceiver達成service傳送訊息到activity的功能
 * 利用ResultReceiver將在service中的Binder傳送回activity，activity利用Binder達成activity傳送訊息到service的功能
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    //ResultCode
    public static final int GETBINDER = 0;
    public static final int GETTIME = 1;
    public static final int GETFINISH = 2;
    public static final int GETMESSAGE = 3;
    public static final int GETCURRENTSETS = 4;

    private final int NUMBEROFLIGHT = 6;
    private int restTime = 10;
    private int readyTime = 10;
    private int workoutTime = 10;
    private int numberOfSets = 10;


    private TextView timerTitle;
    private TextView timerMessage;
    private ImageView light01, light02, light03, light04, light05, light06;
    private List<ImageView> lights = new ArrayList<ImageView>();

    private TimerResultReceiver timerResultReceiver;
    private TimerService.TimerServiceBinder timerServiceBinder;

    private Toolbar toolbar;

    private boolean isStart = false;
    private boolean isPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerTitle = (TextView) findViewById(R.id.timer_title);
        timerMessage = (TextView) findViewById(R.id.timer_message);
        light01 = (ImageView) findViewById(R.id.light_01);
        light02 = (ImageView) findViewById(R.id.light_02);
        light03 = (ImageView) findViewById(R.id.light_03);
        light04 = (ImageView) findViewById(R.id.light_04);
        light05 = (ImageView) findViewById(R.id.light_05);
        light06 = (ImageView) findViewById(R.id.light_06);
        lights.add(light01);
        lights.add(light02);
        lights.add(light03);
        lights.add(light04);
        lights.add(light05);
        lights.add(light06);

        timerResultReceiver = new TimerResultReceiver(null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null){
            setSupportActionBar(toolbar);
        }
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_settings) {
                    Log.i(TAG, "menu_setting");
                    if (isStart){
                        timerServiceBinder.setStatus(TimerService.FINISH);
                    }

                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
//                    startActivityForResult(intent, RESULT_OK);

                    isStart = false;
                    isPause = false;
                    timerTitle.setText(R.string.timer_title);
                    timerMessage.setText(R.string.timer_message);
                    initialLightsDiplay();

                }
                return false;
            }
        });



   }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        SharedPreferences settings = getSharedPreferences(SettingActivity.SETTING_PREFERENCE, 0);
        restTime = settings.getInt(SettingActivity.REST_TIME, 5);
        readyTime = settings.getInt(SettingActivity.READY_TIME, 10);
        workoutTime = settings.getInt(SettingActivity.WORKOUT_TIME, 10);
        numberOfSets = settings.getInt(SettingActivity.NUMBER_OF_SETS, 3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult");
//        switch (requestCode){
//            case RESULT_OK:
//
//                Intent intent = new Intent(MainActivity.this, TimerService.class);
//                stopService(intent);
//                isStart = false;
//                isPause = false;
//                timerTitle.setText(R.string.timer_title);
//                timerMessage.setText(R.string.timer_message);
//                break;
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i(TAG, "pause timer");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onTimerClick(View view){
        Log.i(TAG, "onTimerClick");
        if (isStart){       //運動已開始
            if (isPause){   //運動已開始且已暫停，並要求開始
                isPause = false;
                timerServiceBinder.setStatus(TimerService.START);
            }else{          //運動已開始，並要求暫停
                isPause = true;
                timerServiceBinder.setStatus(TimerService.PAUSE);
            }
        }else{              //運動未開始，並要求開始
            isStart = true;
            Intent intent = new Intent(this, TimerService.class);
            intent.putExtra("result_receiver", timerResultReceiver);
            startService(intent);
            timerMessage.setText("Ready");
        }

    }

    class TimerResultReceiver extends ResultReceiver{
        public TimerResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            Log.i(TAG, "resultCode: " + resultCode);
            switch (resultCode){
                case GETBINDER:
                    timerServiceBinder = (TimerService.TimerServiceBinder) resultData.getBinder("service_binder");
                    break;
                case GETTIME:
                    long ms = resultData.getLong("millis_time");
                    String text = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                            TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
                    timerTitle.setText(text);
                    Log.i(TAG, "time: " + ms);
                    Log.i(TAG, "time: " + text);
                    break;
                case GETFINISH:
                    Intent intent = new Intent(MainActivity.this, TimerService.class);
                    stopService(intent);
                    isStart = false;
                    isPause = false;
                    break;
                case GETMESSAGE:
                    timerMessage.setText(resultData.getString("timer_message"));
                    break;
                case GETCURRENTSETS:
                    int currentSets = resultData.getInt("current_set");
                    setCurrentSetsDisplay(currentSets);
                    break;
            }

        }
    }

    private void setCurrentSetsDisplay(int currentsets){
        for (int i = 0 ; i < lights.size() ; i++){
            if (i <= (currentsets-1)%NUMBEROFLIGHT ){
                lights.get(i).setBackgroundColor(Color.WHITE);
            }else{
                lights.get(i).setBackgroundResource(R.drawable.light_on_circle);
            }
        }
    }

    private void initialLightsDiplay(){
        for (ImageView iv : lights){
            iv.setBackgroundResource(R.drawable.light_on_circle);
        }
    }
}
