package com.project.sveglia;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by simonerigon on 06/08/18.
 */

public class openGoogleMapsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int notification_ID = intent.getExtras().getInt("notification_ID");
        String maps_direction_request = intent.getExtras().getString("maps_direction_request");
        int id_travel_to = intent.getExtras().getInt("id_travel_to");
        int position = intent.getExtras().getInt("position");

        //System.out.println("Notication ID = " + notification_ID);

        // Recupero informazioni dal database --
        DB_Manager db_manager = new DB_Manager(context);
        db_manager.open();
        ArrayList<String> maps_direction_request_array = db_manager.getAllMapsDirectionRequest();
        maps_direction_request = maps_direction_request_array.get(position);
        id_travel_to = Integer.parseInt(db_manager.getAllID().get(position));
        Cancel_Alarm_Class.cancel_Alarm(id_travel_to,context,db_manager,true);
        db_manager.close();

        // Attivo servizio per cancellare la musica della notifica --
        context.stopService(new Intent(context, Notification_Sound_Service.class));

        if(maps_direction_request != null){

            // Apro google maps --
            // Create a Uri from an intent string. Use the result to create an Intent.
            Uri gmmIntentUri = Uri.parse(maps_direction_request);
            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            // Make the Intent explicit by setting the Google Maps package
            mapIntent.setPackage("com.google.android.apps.maps");

            // Attempt to start an activity that can handle the Intent
            context.startActivity(mapIntent);

        }

        // cancello la notifica --
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification_ID);

    }
}
