package com.project.sveglia;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by simonerigon on 27/03/18.
 */

public class delayNotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmReceiver.mySensorManager.unregisterListener(AlarmReceiver.proximitySensorEventListener);
        //System.out.println("Sono entrato in delay notification receiver");

        // Recupero dati da intent --------------------------
        int notification_ID = intent.getExtras().getInt("notification_ID");
        int alarm_music_ID = intent.getExtras().getInt("alarm_music_ID");
        boolean isDelayAlarm = intent.getExtras().getBoolean("isDelayAlarm");
        String alarm_name = intent.getExtras().getString("alarm_name");
        int repeatAlarmNumberTimes = intent.getExtras().getInt("repeatAlarmNumberTimes");
        boolean isRepetitionDayAlarm = intent.getExtras().getBoolean("isRepetitionDayAlarm");
        int position = intent.getExtras().getInt("View_ID_position");
        int ALARM_ID = intent.getExtras().getInt("alarm_ID");
        String maps_direction_request = intent.getExtras().getString("maps_direction_request");


        repeatAlarmNumberTimes -= 1;
        //System.out.println("**** RepeatAlarmNumeberTimes: " + repeatAlarmNumberTimes);
        if(repeatAlarmNumberTimes == 0){
            isDelayAlarm = invertiIsDelayAlarm(isDelayAlarm);
        }

        // Attivo servizio per cancellare la musica della notifica --
        context.stopService(new Intent(context, Notification_Sound_Service.class));

        // cancello la notifica -----------------------------
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_ID);

        // Apro il database ---------------------------------
        DB_Manager db_manager = new DB_Manager(context);
        db_manager.open();

        // Recupero tempo di attesa della nuova sveglia -----
        // ---> da recuperare poi da impostazioni
        long delayTime = db_manager.getRitardaMinuti();

        // Setto la nuova sveglia ---------------------------
        Calendar cal = Calendar.getInstance();
        long timeDelayAlarm = cal.getTimeInMillis() + delayTime;

        int repeatAlarmID = createID(timeDelayAlarm);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);


        Intent startRepeatAlarm = new Intent(context, AlarmReceiver.class);
        startRepeatAlarm.putExtra("View_ID", 0);
        startRepeatAlarm.putExtra("View_ID_position", position);
        startRepeatAlarm.putExtra("isRepeatAlarm", false);
        startRepeatAlarm.putExtra("alarm_music_ID", alarm_music_ID);
        startRepeatAlarm.putExtra("isDelayAlarm", isDelayAlarm);
        startRepeatAlarm.putExtra("alarmName", alarm_name);
        startRepeatAlarm.putExtra("repeatAlarmNumberTimes", repeatAlarmNumberTimes);
        startRepeatAlarm.putExtra("isFirstTimeAlarm", false);
        startRepeatAlarm.putExtra("alarm_ID", ALARM_ID);
        startRepeatAlarm.putExtra("maps_direction_request", maps_direction_request);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, repeatAlarmID, startRepeatAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeDelayAlarm, alarmPendingIntent);

        // Attivo notifica con CountDown Timer del tempo di Delay --
        int countDownNotification_ID = notification_ID + 10;
        Bundle bundle = new Bundle();
        bundle.putLong("numero", delayTime);
        String counteDownTimerText = "";

        Intent notif_Context_Intent = new Intent(context, MainActivity.class);
        PendingIntent notif_Context_Pend_Intent = PendingIntent.getActivity(context, 0, notif_Context_Intent, 0);

        // Azione da aggiungere alla notifica per terminare il conto alla rovescia e cancellare la sveglia --
        Intent countDown_Notif_Cancel = new Intent(context, CancelNotificationReceiver.class);
        countDown_Notif_Cancel.putExtra("alarm_ID", repeatAlarmID);
        countDown_Notif_Cancel.putExtra("isRepetitionDayAlarm", isRepetitionDayAlarm);
        countDown_Notif_Cancel.putExtra("notification_ID", countDownNotification_ID);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, countDownNotification_ID, countDown_Notif_Cancel, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creazione della notifica "countDown Timer" --
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Creo un notification Channel ------------------------------------
            String not_CountDownTimer_Channel = "not_CountDownTimer_Channel";
            NotificationChannel notificationChannel = new NotificationChannel(not_CountDownTimer_Channel, "CountDown Timer", NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setShowBadge(false);
            notificationChannel.setSound(null, null);

            NotificationCompat.Builder countDown_Notif = (NotificationCompat.Builder) new NotificationCompat.Builder(context, not_CountDownTimer_Channel)
                    .setSmallIcon(R.drawable.icons8_alarm_clock_24)
                    .setContentTitle("Alarm Notification")
                    .setExtras(bundle)
                    .setContentText(counteDownTimerText)
                    .setContentIntent(notif_Context_Pend_Intent)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .addAction(0, "TERMINA", cancelPendingIntent);

            notificationManager.notify(countDownNotification_ID, countDown_Notif.build());
            notificationManager.createNotificationChannel(notificationChannel);

            // attivo CountDown Timer ----------------------
            CountDownTimer.startCountDownTimer(countDown_Notif, countDownNotification_ID, notificationManager, bundle, context);
            CountDownTimer.startCountDown();

        } else {
            NotificationCompat.Builder countDown_Notif = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icons8_alarm_clock_24)
                    .setContentTitle("Alarm Notification")
                    .setExtras(bundle)
                    .setContentText(counteDownTimerText)
                    .setContentIntent(notif_Context_Pend_Intent)
                    .setAutoCancel(true)
                    .addAction(0, "TERMINA", cancelPendingIntent);

            notificationManager.notify(countDownNotification_ID, countDown_Notif.build());

            // attivo CountDown Timer ----------------------
            CountDownTimer.startCountDownTimer(countDown_Notif, countDownNotification_ID, notificationManager, bundle, context);
            CountDownTimer.startCountDown();

        }
db_manager.close();
    }


    /**
     * Funzione per creare un ID per la notifica
     * @param time current time in millisecond
     * @return Notification ID
     */
    private static int createID(long time){
        int res = 0;
        int tmp = (int)time;
        res = Math.abs(tmp);
        return res;
    }


    private boolean invertiIsDelayAlarm(boolean isDelayAlarm){

        if(isDelayAlarm == true){
            isDelayAlarm = false;
        }else{
            isDelayAlarm = true;
        }

        return isDelayAlarm;
    }
}
