package com.xlab.myworkouttimer.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xlab.myworkouttimer.R;

public class SettingActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    public static final String SETTING_PREFERENCE = "Setting_Preference";
    public static final String REST_TIME = "rest_time";
    public static final String READY_TIME = "ready_time";
    public static final String WORKOUT_TIME = "workout_time";
    public static final String NUMBER_OF_SETS = "number_of_sets";

    private SharedPreferences settings;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        settings = getSharedPreferences(SETTING_PREFERENCE, 0);

    }

    @Override
    protected void onStop() {
        super.onStop();
//        setResult(RESULT_OK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void setRestTime(View view){
        Log.i(TAG, "setRestTime");
        getEditTimeDialog("Rest Time", REST_TIME).show();
    }
    public void setReadyTime(View view) {
        Log.i(TAG, "setReadyTime");
        getEditTimeDialog("Ready Time", READY_TIME).show();
    }
    public void setWorkoutTime(View view){
        Log.i(TAG, "setWorkoutTime");
        getEditTimeDialog("Workout Time", WORKOUT_TIME).show();
    }
    public void setNumberOfSets(View view){
        Log.i(TAG, "setNumberTime");
        getEditSetsDialog("Number of sets", NUMBER_OF_SETS).show();
    }

    private AlertDialog getEditTimeDialog(String title, final String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_settime, null);
        final EditText editTime = (EditText) view.findViewById(R.id.dialog_input);
        editTime.setHint(R.string.hint_time);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);
        builder.setView(view)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String time = editTime.getText().toString();
                Log.i(TAG, "Time: " + time);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(key, Integer.valueOf(time));
                editor.commit();
            }
        })
                .setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    private AlertDialog getEditSetsDialog(String title, final String key){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_settime, null);
        final EditText editSets = (EditText) view.findViewById(R.id.dialog_input);
        editSets.setHint(R.string.hint_number_sets);
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_title);
        dialogTitle.setText(title);
        builder.setView(view)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sets = editSets.getText().toString();
                        Log.i(TAG, "Sets: " + sets);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt(key, Integer.valueOf(sets));
                        editor.commit();
                    }
                })
                .setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }
}
