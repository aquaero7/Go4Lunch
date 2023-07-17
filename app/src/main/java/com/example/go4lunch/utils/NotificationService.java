package com.example.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.view.activity.MainActivity;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends FirebaseMessagingService {

    private final UserRepository userRepository = UserRepository.getInstance();
    private final Utils utils = Utils.getInstance();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            // Get message sent by Firebase
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Log.i("NotificationService", notification.getTitle() + "\n" + notification.getBody());
            // Get current user
            User currentUser = userRepository.getCurrentUser();
            // Check if current user has selected a restaurant
            boolean aRestaurantIsSelected = currentUser.getSelectionId() != null && utils.getCurrentDate().equals(currentUser.getSelectionDate());
            /* Send notification with message only if current user has selected a restaurant
               and if his notifications preferences parameter is set true */
            if (aRestaurantIsSelected && Boolean.parseBoolean(currentUser.getNotificationsPrefs())) {
                // Get workmates list
                List<User> workmatesList = userRepository.getWorkmates();
                // Get the list of workmates names with the same selection (other than current user)
                final List<String> selectorsNameList = new ArrayList<>();
                // For each workmate, get his/her name and the selected restaurant id and date
                for (User workmate : workmatesList) {
                    String wId = workmate.getUid();
                    // Get workmate selection only if he/she is not the current user
                    if (!currentUser.getUid().equals(wId)) {
                        String wName = workmate.getUsername();
                        String wSelId = (workmate.getSelectionId() != null) ? workmate.getSelectionId() : "";
                        String wSelDate = (workmate.getSelectionDate() != null) ? workmate.getSelectionDate() : "";

                        /* If the workmate selection matches that of the current user, add his/her name to the selectors list */
                        if (currentUser.getSelectionId().equals(wSelId) && utils.getCurrentDate().equals(wSelDate)) selectorsNameList.add(wName);
                    }
                }
                // Create notification body text
                String notificationBodyText = createNotificationBodyText(currentUser.getSelectionName(), currentUser.getSelectionAddress(), selectorsNameList);

                // Send notification
                sendLunchNotification(notificationBodyText);
            }
        }
    }

    private String createNotificationBodyText(String rName, String rAddress, List<String> selectorsNameList) {
        String text = getString(R.string.notification_body_header) + "\n" + "\n" + rName + "\n" + rAddress;
        if (selectorsNameList.isEmpty()) {
            text = text + "\n" + "\n" + getString(R.string.text_none_joining);
        } else {
            text = text + "\n" + "\n" + getString(R.string.text_workmates_joining);
            for (String selectorName : selectorsNameList) {
                text = text + "\n- " + selectorName;
            }
        }
        text = text + "\n" + "\n" + getString(R.string.text_greeting);
        Log.i("NotificationService", text);

        return text;
    }

    private void sendLunchNotification(String bodyText) {
        final int NOTIFICATION_ID = 4;

        // Create an Intent that will be shown each day at 12 (configured in Firebase Cloud Messaging)
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        // Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);
        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_go4lunch)
                        .setContentTitle(getString(R.string.notification_title))
                        // .setContentText(bodyText)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(bodyText))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);
        // Create the notification manager that will handle the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Create a new channel for Android versions >= 8
        CharSequence channelName = getString(R.string.notification_channel_name);   // "Firebase Messages";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);

        // Show notification
        notificationManager.notify(getString(R.string.app_name), NOTIFICATION_ID, notificationBuilder.build());
    }

}
