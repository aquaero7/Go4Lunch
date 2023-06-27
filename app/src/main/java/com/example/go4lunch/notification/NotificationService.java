package com.example.go4lunch.notification;

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
import com.example.go4lunch.activity.MainActivity;
import com.example.go4lunch.repository.UserRepository;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.DataProcessingUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 4;
    // private final String NOTIFICATION_TAG = "GO4LUNCH";

    private String currentDate = DataProcessingUtils.getCurrentDate();
    private User currentUser;
    private String currentUserId;
    private List<User> workmatesList;
    private List<String> selectorsNameList = new ArrayList<>();
    private String selectionId;
    private String selectionDate;
    private String selectionName;
    private String selectionAddress;
    private String notificationPrefs;
    private String notificationBodyText;
    private UserRepository userRepository = UserRepository.getInstance();


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            // Get message sent by Firebase
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Log.i("NotificationService", notification.getTitle() + "\n" + notification.getBody());
            // Get current user data
            currentUser = userRepository.getCurrentUser();
            currentUserId = currentUser.getUid();
            selectionId = currentUser.getSelectionId();
            selectionDate = currentUser.getSelectionDate();
            selectionName = currentUser.getSelectionName();
            selectionAddress = currentUser.getSelectionAddress();
            notificationPrefs = currentUser.getNotificationsPrefs();
            // Check if current user has selected a restaurant
            boolean aRestaurantIsSelected = selectionId != null && currentDate.equals(selectionDate);

            /* Send notification with message only if current user has selected a restaurant
               and if his notifications preferences parameter is set true */
            if (aRestaurantIsSelected && Boolean.parseBoolean(notificationPrefs)) {
                // Get workmates list
                workmatesList = userRepository.getWorkmates();
                // Get the list of workmates names with the same selection (other than current user)
                // Clear the selectors list of this restaurant
                if (selectorsNameList != null) selectorsNameList.clear();
                // For each workmate, get his/her name and the selected restaurant id and date
                for (User workmate : workmatesList) {
                    String wId = workmate.getUid();
                    // Get workmate selection only if he/she is not the current user
                    if (!currentUserId.equals(wId)) {
                        String wName = workmate.getUsername();
                        String wSelId = (workmate.getSelectionId() != null) ? workmate.getSelectionId() : "";
                        String wSelDate = (workmate.getSelectionDate() != null) ? workmate.getSelectionDate() : "";
                        /* If the workmate selection matches that of the current user,
                           add his/her name to the selectors list */

                        if (selectionId.equals(wSelId) && currentDate.equals(wSelDate)) selectorsNameList.add(wName);
                    }
                }
                // Create notification body text
                createNotificationBodyText(selectionName, selectionAddress, selectorsNameList);
                // Send notification
                sendLunchNotification();
            }
        }
    }

    private void sendLunchNotification() {
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
                        // .setContentText(notificationBodyText)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBodyText))
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
        // notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
        notificationManager.notify(getString(R.string.app_name), NOTIFICATION_ID, notificationBuilder.build());
    }


    private void createNotificationBodyText(String rName, String rAddress, List<String> selectorsNameList) {
        notificationBodyText = getString(R.string.notification_body_header) + "\n" + "\n" + rName + "\n" + rAddress;
        if (selectorsNameList.isEmpty()) {
            notificationBodyText = notificationBodyText + "\n" + "\n" + getString(R.string.text_none_joining);
        } else {
            notificationBodyText = notificationBodyText + "\n" + "\n" + getString(R.string.text_workmates_joining);
            for (String selectorName : selectorsNameList) {
                notificationBodyText = notificationBodyText + "\n- " + selectorName;
            }
        }
        notificationBodyText = notificationBodyText + "\n" + "\n" + getString(R.string.text_greeting);
        Log.i("NotificationService", notificationBodyText);
    }


}
