package com.dtdcreator.intervaltimer;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final String LOOPS_LEFT = "loops_left";
    public static final String STARTING_LOOPS = "starting_loops";
    public static final String CURRENT_TIMER_INDEX = "current_timer_index";
    public static final String TIMERS_STARTED = "timers_started";
    public static final String TIMERS_RUNNING = "timers_running";
    public static final String TIME_LEFT_IN_MILLISECONDS = "time_left_in_milliseconds";
    public static final String TIMER_USER_INPUT_TIME = "timer_user_input_time";
    public static final String BREAK_USER_INPUT_TIME = "break_user_input_time";
    public static final String TIMER_USER_HOURS = "timer_user_hours";
    public static final String TIMER_USER_MINUTES = "timer_user_minutes";
    public static final String TIMER_USER_SECONDS = "timer_user_seconds";
    public static final String BREAK_USER_HOURS = "break_user_hours";
    public static final String BREAK_USER_MINUTES = "break_user_minutes";
    public static final String BREAK_USER_SECONDS = "break_user_seconds";
    public static final String CONTROL_BUTTON_STRING = "control_button_string";
    public static final String CONTROL_BUTTON_STATE = "control_button_state";

    int loopsLeft;
    int startingLoops;
    int currentTimerIndex;
    boolean timersStarted = false;
    boolean timersRunning = false;

    EditText loopEditText;
    TextView loopTextView;

    EditText secondsEditText;
    EditText minutesEditText;
    EditText hoursEditText;

    EditText breakSecondsEditText;
    EditText breakMinutesEditText;
    EditText breakHoursEditText;

    Button controlButton;
    Button resetButton;

    MediaPlayer timerEndRingtone;
    MediaPlayer breakEndRingtone;
    MediaPlayer endRingtone;

    ArrayList<Timer> timerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerEndRingtone = MediaPlayer.create(getBaseContext(), R.raw.timer_end);
        breakEndRingtone = MediaPlayer.create(getBaseContext(), R.raw.break_end);
        endRingtone = MediaPlayer.create(getBaseContext(), R.raw.end_ringtone);

        controlButton = findViewById(R.id.control_button);
        setControlButtonEnabled(false);

        resetButton = findViewById(R.id.reset_button);
        setResetButtonEnabled(false);
        final TextView resetTextView = findViewById(R.id.reset_text_view);

        secondsEditText = findViewById(R.id.seconds_edit_text);
        minutesEditText = findViewById(R.id.minutes_edit_text);
        hoursEditText = findViewById(R.id.hours_edit_text);
        secondsEditText.addTextChangedListener(editTextWatcher);
        minutesEditText.addTextChangedListener(editTextWatcher);
        hoursEditText.addTextChangedListener(editTextWatcher);

        breakSecondsEditText = findViewById(R.id.break_seconds_edit_text);
        breakMinutesEditText = findViewById(R.id.break_minutes_edit_text);
        breakHoursEditText = findViewById(R.id.break_hours_edit_text);
        breakSecondsEditText.addTextChangedListener(editTextWatcher);
        breakMinutesEditText.addTextChangedListener(editTextWatcher);
        breakHoursEditText.addTextChangedListener(editTextWatcher);

        loopTextView = findViewById(R.id.loop_text_view);
        loopEditText = findViewById(R.id.loop_edit_text);

        final Timer timer = new Timer(secondsEditText, minutesEditText, hoursEditText, controlButton, this);
        timerList.add(timer);

        final Timer breakTimer = new Timer(breakSecondsEditText, breakMinutesEditText, breakHoursEditText, controlButton, this);
        timerList.add(breakTimer);

        timerList.trimToSize();

        resetButton.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                int i;
                for (i = 0; i < timerList.size(); i ++){
                    timerList.get(i).resetTimer();
                }
                controlButton.setText(R.string.start_string);
                timersRunning = false;
                timersStarted = false;
                loopEditText.setText(String.valueOf(startingLoops));
                loopTextView.setText(R.string.loop_string);
                setFocus(true);
                return false;
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTextView.setVisibility(View.VISIBLE);
                resetTextView.postDelayed(new Runnable() {
                    public void run() {
                        resetTextView.setVisibility(View.GONE);
                    }
                }, 5000);
            }
        });

        controlButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!timersStarted){
                    int i;
                    for(i = 0; i < timerList.size(); i ++){
                        timerList.get(i).saveUserTimer();
                        Log.d(TAG, "onClick: timerList size = " + timerList.size());
                    }
                    startTimers();
                    setResetButtonEnabled(false);
                    controlButton.setText(R.string.pause_string);
                    loopTextView.setText(R.string.loops_left_running_string);
                    setFocus(false);
                    if(!loopEditText.getText().toString().matches("")) {
                        startingLoops = Integer.parseInt(loopEditText.getText().toString());
                    } else {
                        startingLoops = 0;
                    }
                    if (startingLoops > 0){
                        loopsLeft = startingLoops - 1;
                    } else {
                        loopsLeft = startingLoops;
                    }
                    loopEditText.setText(String.valueOf(loopsLeft));
                } else if(!timersRunning){
                    timerList.get(currentTimerIndex).resumeTimer();
                    setResetButtonEnabled(false);
                    timersRunning = true;
                    controlButton.setText(R.string.pause_string);
                    setFocus(false);
                } else {
                    timerList.get(currentTimerIndex).pauseTimer();
                    setResetButtonEnabled(true);
                    timersRunning = false;
                    controlButton.setText(R.string.resume_string);
                    setFocus(true);
                }
            }
        });

    }
     @Override
     public void onSaveInstanceState(Bundle outState){
        outState.putInt(LOOPS_LEFT, loopsLeft);
        outState.putInt(STARTING_LOOPS, startingLoops);
        outState.putInt(CURRENT_TIMER_INDEX, currentTimerIndex);
        outState.putBoolean(TIMERS_RUNNING, timersRunning);
        outState.putBoolean(TIMERS_STARTED, timersStarted);
        outState.putLong(TIME_LEFT_IN_MILLISECONDS, timerList.get(currentTimerIndex).getTimeLeftInMilliseconds());
        outState.putLong(TIMER_USER_INPUT_TIME, timerList.get(0).getUserInputTime());
        outState.putLong(BREAK_USER_INPUT_TIME, timerList.get(1).getUserInputTime());
        outState.putInt(TIMER_USER_HOURS, timerList.get(0).getUserHours());
        outState.putInt(TIMER_USER_MINUTES, timerList.get(0).getUserMinutes());
        outState.putInt(TIMER_USER_SECONDS, timerList.get(0).getUserSeconds());
        outState.putInt(BREAK_USER_HOURS, timerList.get(1).getUserHours());
        outState.putInt(BREAK_USER_MINUTES, timerList.get(1).getUserMinutes());
        outState.putInt(BREAK_USER_SECONDS, timerList.get(1).getUserSeconds());
        outState.putString(CONTROL_BUTTON_STRING, controlButton.getText().toString());
        outState.putBoolean(CONTROL_BUTTON_STATE, controlButton.isEnabled());
        if (timersRunning) {
            timerList.get(currentTimerIndex).countDownTimer.cancel();
        }
         Log.d(TAG, "onSaveInstanceState: timersRunning = " + timersRunning);
         super.onSaveInstanceState(outState);
     }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        loopsLeft = savedInstanceState.getInt(LOOPS_LEFT);
        startingLoops = savedInstanceState.getInt(STARTING_LOOPS);
        currentTimerIndex = savedInstanceState.getInt(CURRENT_TIMER_INDEX);
        timersRunning = savedInstanceState.getBoolean(TIMERS_RUNNING);
        timersStarted = savedInstanceState.getBoolean(TIMERS_STARTED);
        timerList.get(0).setUserInputTime(savedInstanceState.getLong(TIMER_USER_INPUT_TIME));
        timerList.get(0).setUserHours(savedInstanceState.getInt(TIMER_USER_HOURS));
        timerList.get(0).setUserMinutes(savedInstanceState.getInt(TIMER_USER_MINUTES));
        timerList.get(0).setUserSeconds(savedInstanceState.getInt(TIMER_USER_SECONDS));
        timerList.get(1).setUserInputTime(savedInstanceState.getLong(BREAK_USER_INPUT_TIME));
        timerList.get(1).setUserHours(savedInstanceState.getInt(BREAK_USER_HOURS));
        timerList.get(1).setUserMinutes(savedInstanceState.getInt(BREAK_USER_MINUTES));
        timerList.get(1).setUserSeconds(savedInstanceState.getInt(BREAK_USER_SECONDS));
        timerList.get(currentTimerIndex).setTimeLeftInMilliseconds(savedInstanceState.getLong(TIME_LEFT_IN_MILLISECONDS));
        controlButton.setText(savedInstanceState.getString(CONTROL_BUTTON_STRING));
        controlButton.setEnabled(savedInstanceState.getBoolean(CONTROL_BUTTON_STATE));
        Log.d(TAG, "onRestoreInstanceState: timersRunning = " + timersRunning);
        if (timersRunning) {
            timerList.get(currentTimerIndex).resumeTimer();
            setFocus(false);
        } else {
            setFocus(true);
        }
        if(timersStarted && timersRunning){
            setResetButtonEnabled(false);
        }
        if (!timersRunning){
            setResetButtonEnabled(true);
        }
        if (aTimerIsFilled()){
            setControlButtonEnabled(true);
        }
    }

    public void startTimers(){
        timerList.get(0).startTimer();
        currentTimerIndex = 0;
        timersStarted = true;
        timersRunning = true;
    }

    public void timersEnded(){
        int i;
        for (i = 0; i < timerList.size(); i ++){
            timerList.get(i).resetTimer();
        }
        controlButton.setText(R.string.start_string);
        loopEditText.setText(String.valueOf(startingLoops));
        loopTextView.setText(R.string.loop_string);
        timersRunning = false;
        timersStarted = false;
        setResetButtonEnabled(true);
        setFocus(true);
        endRingtone.start();
    }

    public void onTimerFinished(Timer timer){
        int index = timerList.indexOf(timer);
        if (timerList.size() > index + 1) {
            timerList.get(index + 1).startTimer();
            currentTimerIndex = index + 1;
            timerEndRingtone.start();
        } else if (loopsLeft > 0){
            timerList.get(0).startTimer();
            currentTimerIndex = 0;
            timerList.get(currentTimerIndex + 1).resetTimer();
            loopsLeft -= 1;
            loopEditText.setText(String.valueOf(loopsLeft));
            breakEndRingtone.start();
        } else if (loopsLeft == 0 && timerList.size() == index + 1){
            timersEnded();
        }
    }

    public boolean aTimerIsFilled(){
        return (!secondsEditText.getText().toString().matches("") || !minutesEditText.getText().toString().matches("") || !hoursEditText.getText().toString().matches("")) ||
                (!breakSecondsEditText.getText().toString().matches("") || !breakMinutesEditText.getText().toString().matches("") || !breakHoursEditText.getText().toString().matches(""));
    }

    TextWatcher editTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            if (aTimerIsFilled()){
                setControlButtonEnabled(true);
            } else {
                setControlButtonEnabled(false);
            }
        }
    };

    public void setResetButtonEnabled(boolean setEnabled){
        resetButton.setEnabled(setEnabled);
        if (!setEnabled){
            resetButton.setBackgroundColor(Color.parseColor("#DDC564"));
            resetButton.setTextColor(Color.parseColor("#929292"));
        } else {
            resetButton.setBackgroundColor(Color.parseColor("#FFCE00"));
            resetButton.setTextColor(Color.parseColor("#000000"));
        }
    }

    public void setControlButtonEnabled(boolean setEnabled){
        controlButton.setEnabled(setEnabled);
        if (!setEnabled){
            controlButton.setBackgroundColor(Color.parseColor("#8CC583"));
            controlButton.setTextColor(Color.parseColor("#929292"));
        } else {
            controlButton.setBackgroundColor(Color.parseColor("#20f000"));
            controlButton.setTextColor(Color.parseColor("#000000"));
        }
    }

    public void setFocus(boolean enabled){
        secondsEditText.setEnabled(enabled);
        minutesEditText.setEnabled(enabled);
        hoursEditText.setEnabled(enabled);
        breakSecondsEditText.setEnabled(enabled);
        breakMinutesEditText.setEnabled(enabled);
        breakHoursEditText.setEnabled(enabled);
        loopEditText.setEnabled(enabled);
    }
}