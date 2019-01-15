package com.dtdcreator.intervaltimer;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Bette on 1/7/2018.
 */

public class Timer {
    public static final String TAG = "Timer";

    EditText secondsEditText;
    EditText minutesEditText;
    EditText hoursEditText;
    Button controlButton;

    long userInputTime;
    int userHours;
    int userMinutes;
    int userSeconds;

    CountDownTimer countDownTimer;
    long timeLeftInMilliseconds;
    String timeLeftString;
    boolean timerRunning = false;

    MainActivity parentMainActivity = null;

    public Timer(EditText seconds, EditText minutes, EditText hours, Button control, MainActivity parent){
        secondsEditText = seconds;
        minutesEditText = minutes;
        hoursEditText = hours;
        controlButton = control;
        parentMainActivity = parent;
    }

    public void startTimer(){
        if(userInputTime == 0){
            saveUserTimer();
        }
        timeLeftInMilliseconds = userInputTime;

        resumeTimer();
    }

    public void resumeTimer(){
//        Log.d(TAG, "resume: timeLeftInMilliseconds = " + timeLeftInMilliseconds);
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {
                onCountDownTimerFinished();
            }
        }.start();
        timerRunning = true;
    }

    public void pauseTimer(){
        countDownTimer.cancel();
        timerRunning = false;
    }

    public void resetTimer(){
        if(null != countDownTimer) {
            countDownTimer.cancel();

            String timeResetString;
            timeLeftInMilliseconds = userInputTime;

            if(userHours < 10){
                timeResetString = "0" + userHours;
            } else {
                timeResetString = Integer.toString(userHours);
            }
            hoursEditText.setText(timeResetString);

            if(userMinutes < 10){
                timeResetString = "0" + userMinutes;
            } else {
                timeResetString = Integer.toString(userMinutes);
            }
            minutesEditText.setText(timeResetString);

            if(userSeconds < 10){
                timeResetString = "0" + userSeconds;
            } else {
                timeResetString = Integer.toString(userSeconds);
            }
            secondsEditText.setText(timeResetString);
        }
    }

    public void updateTimer(){
        int hours = (int) timeLeftInMilliseconds / 3600000;
        int minutes = (int) timeLeftInMilliseconds % 3600000 / 60000;
        int seconds = (int) timeLeftInMilliseconds % 3600000 % 60000 / 1000;

//      Updating Timer
        if(hours < 10){
            timeLeftString = "0" + hours;
            hoursEditText.setText(timeLeftString);
        } else {
            timeLeftString = Integer.toString(hours);
            hoursEditText.setText(timeLeftString);
        }
        if(minutes < 10){
            timeLeftString = "0" + minutes;
            minutesEditText.setText(timeLeftString);
        } else {
            timeLeftString = Integer.toString(minutes);
            minutesEditText.setText(timeLeftString);
        }
        if(seconds < 10){
            timeLeftString = "0" + seconds;
            secondsEditText.setText(timeLeftString);
        } else {
            timeLeftString = Integer.toString(seconds);
            secondsEditText.setText(timeLeftString);
        }
    }

    public void saveUserTimer(){
        if(secondsEditText.getText().length() == 0){
            userSeconds = 0;
        } else {
            userSeconds = Integer.parseInt(secondsEditText.getText().toString());
        }
//        Log.d(TAG, "saveUserTimer: userSeconds = " + userSeconds);
        if(minutesEditText.getText().length() == 0){
            userMinutes = 0;
        } else {
            userMinutes = Integer.parseInt(minutesEditText.getText().toString());
        }
//        Log.d(TAG, "saveUserTimer: userMinutes = " + userMinutes);
        if(hoursEditText.getText().length() == 0){
            userHours = 0;
        } else {
            userHours = Integer.parseInt(hoursEditText.getText().toString());
        }
//        Log.d(TAG, "saveUserTimer: userHours = " + userHours);

        userInputTime = (userSeconds * 1000);
        userInputTime += (userMinutes * 60000);
        userInputTime += (userHours * 3600000);

        Log.d(TAG, "saveUserTimer: userInputTime = " + userInputTime);
    }

    public void onCountDownTimerFinished(){
        parentMainActivity.onTimerFinished(this);
    }

    public long getTimeLeftInMilliseconds(){
        return timeLeftInMilliseconds;
    }

    public void setTimeLeftInMilliseconds(long m){
        timeLeftInMilliseconds = m;
    }

    public long getUserInputTime(){
        return userInputTime;
    }

    public void setUserInputTime(long t){
        userInputTime = t;
    }

    public int getUserHours(){
        return userHours;
    }

    public void setUserHours(int i){
        userHours = i;
    }

    public int getUserMinutes() {
        return userMinutes;
    }

    public void setUserMinutes(int i) {
        userMinutes = i;
    }

    public int getUserSeconds() {
        return userSeconds;
    }

    public void setUserSeconds(int i) {
        userSeconds = i;
    }
}
