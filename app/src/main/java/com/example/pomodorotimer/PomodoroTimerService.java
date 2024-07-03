package com.example.pomodorotimer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

public class PomodoroTimerService extends Service {
    public static final String CHANNEL_ID = "PomodoroTimerChannel";
    private CountDownTimer countDownTimer;
    //private long timeLeftInMillis = 1500000; // 25 minutes
    private long timeLeftInMillis;
    private boolean isTimerRunning = false;
    private int sessionCount = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pomodoro Timer")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent);

        startForeground(1, notification.build());
        startTimer();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateNotification();
            }
            @Override
            public void onFinish() {
                sessionCount++;
                if (sessionCount % 4 == 0) {
                    timeLeftInMillis = 15 * 60 * 1000; // 15 minutes break
                    //timeLeftInMillis = 15000;
                    //vibratePhone();
                } else if (sessionCount % 2 == 0) {
                    timeLeftInMillis = 5 * 60 * 1000; // 5 minutes break
                    //timeLeftInMillis = 10000;
                    //vibratePhone();
                } else {
                    timeLeftInMillis = 25 * 60 * 1000; // 25 minutes work
                    //vibratePhone();
                }
                sendNotification("Pomodoro Timer123123123", "Time to switch!");
                //vibratePhone();
                startTimer();
            }
        }.start();
        isTimerRunning = true;
    }

    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //.setVibrate(new long[]{0, 0, 0, 0, 0})
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder.build());
        vibratePhone();
    }

    private void vibratePhone() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

    private NotificationCompat.Builder notificationBuilder;

    private void updateNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Pomodoro Timer")
                .setContentText("Time remaining: " + timeLeftInMillis / 1000)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setVibrate(null)
                .setSound(null);

        startForeground(1, notificationBuilder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pomodoro Timer Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

