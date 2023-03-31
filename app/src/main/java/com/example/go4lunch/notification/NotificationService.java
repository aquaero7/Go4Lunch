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
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 4;
    private final String NOTIFICATION_TAG = "GO4LUNCH";

    String currentDate = CalendarUtils.getCurrentDate();
    User currentUser = FirestoreUtils.getCurrentUserFromDatabaseDocument();
    String currentUserId = currentUser.getUid();
    String selectionId = currentUser.getSelectionId();
    String selectionDate = currentUser.getSelectionDate();
    String rName = null;
    String rAddress = null;
    List<User> selectorsList;
    String notificationBodyText;


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            // Get message sent by Firebase
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Log.e("NotificationService", notification.getBody());

            // Send notification with message   // TODO : To be deleted
            // sendLunchNotification(notification);

            // Check if current user has selected a restaurant
            boolean aRestaurantIsSelected = selectionId != null && currentDate.equals(selectionDate);

            // Send notification with message only if current user has selected a restaurant
            if (aRestaurantIsSelected) {
                // Get selected restaurant data from restaurants collection in database
                getSelectedRestaurantData();
                // Get the list of workmates with the same selection (other than current user)
                getSelectorsList();
                // Create notification body text
                createNotificationBodyText();
                // Send notification with message
                sendLunchNotification();
            }
        }
    }

    private void sendLunchNotification() {

        // Create an Intent that will be shown each day at 12
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_go4lunch)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(notificationBodyText)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent);

        // Create the notification manager that will handle the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a new channel for Android versions >= 8
        CharSequence channelName = "Firebase Messages";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);

        // Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }


    private void createNotificationBodyText() {
        notificationBodyText = getString(R.string.notification_body_header) + "\n" + rName + "\n" + rAddress;
        if (selectorsList.isEmpty()) {
            notificationBodyText = notificationBodyText + "\n" + "\n" + getString(R.string.none_joining_text);
        } else {
            notificationBodyText = notificationBodyText + "\n" + "\n" + getString(R.string.workmates_joining_text);
            for (User selector : selectorsList) {
                notificationBodyText = notificationBodyText + "\n" + selector.getUsername();
            }
        }
        notificationBodyText = notificationBodyText + "\n" + getString(R.string.greeting);
    }

    private void getSelectorsList() {
        selectorsList = new ArrayList<>();
        List<User> workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();
        for (User workmate : workmatesList) {
            // Check selected restaurant id and date and get users list
            boolean isSelector = (selectionId.equals(workmate.getSelectionId())
                    && currentDate.equals(workmate.getSelectionDate()));
            // Don't add current user to the list of (other) selectors
            if (isSelector && !(currentUserId.equals(workmate.getUid()))) selectorsList.add(workmate);
        }
    }

    private void getSelectedRestaurantData() {
        List<Restaurant> restaurantsList = FirestoreUtils.getRestaurantsListFromDatabaseDocument();
        for (Restaurant restaurant : restaurantsList) {
            if (selectionId.equals(restaurant.getRid())) {
                rName = restaurant.getName();
                rAddress = restaurant.getAddress();
            }
        }
    }

    // TODO : To be deleted
    private void checkUserSelectionAndSendLunchNotification(RemoteMessage.Notification notification) {
        /*
        // Get selected restaurant data from restaurants collection in database
        List<Restaurant> restaurantsList = FirestoreUtils.getRestaurantsListFromDatabaseDocument();
        for (Restaurant restaurant : restaurantsList) {
            if (selectionId.equals(restaurant.getRid())) {
                rName = restaurant.getName();
                rAddress = restaurant.getAddress();
            }
        }

        // Get the list of workmates with the same selection (other than current user)
        List<User> selectorsList = new ArrayList<>();
        List<User> workmatesList = FirestoreUtils.getWorkmatesListFromDatabaseDocument();
        for (User workmate : workmatesList) {
            // Check selected restaurant id and date and get users list
            boolean isSelector = (selectionId.equals(workmate.getSelectionId())
                    && currentDate.equals(workmate.getSelectionDate()));
            // Don't add current user to the list of (other) selectors
            if (isSelector && !(currentUserId.equals(workmate.getUid()))) selectorsList.add(workmate);
        }

        // Send notification with message
        sendLunchNotification(notification, rName, rAddress, selectorsList);
        */

        /*
        final String[] selectionId = new String[1];
        final String[] selectionDate = new String[1];
        final String[] rName = new String[1];
        final String[] rAddress = new String[1];
        String currentDate = CalendarUtils.getCurrentDate();

        // Get current user selected restaurant from database
        UserManager.getInstance().getCurrentUserData().addOnSuccessListener(user -> {
            selectionId[0] = user.getSelectionId();
            selectionDate[0] = user.getSelectionDate();
            boolean isSelected = selectionId[0] != null && currentDate.equals(selectionDate[0]);

            // Send notification only if current user has selected a restaurant
            if (isSelected) {

                // Get selected restaurant data from restaurants collection in database
                RestaurantManager.getRestaurantData(selectionId[0])
                        .addOnSuccessListener(restaurant -> {
                            Log.w("NotificationService", "success task getRestaurantData");
                            rName[0] = restaurant.getName();
                            rAddress[0] = restaurant.getAddress();
                        })
                        .addOnFailureListener(e -> Log.w("NotificationService", e.getMessage()));

                // Get the list of workmates with the same selection (other than current user)
                List<User> selectorsList = new ArrayList<>();
                UserManager.getUsersList(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            // Check selected restaurant id and date and get users list
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData(); // Map data for debug.
                                User selectorToAdd = FirestoreUtils.getUserFromDatabaseDocument(document);
                                boolean isSelector = (selectionId[0].equals(selectorToAdd.getSelectionId())
                                        && currentDate.equals(selectorToAdd.getSelectionDate()));
                                if (isSelector && !(UserManager.getInstance().getCurrentUserId()
                                        .equals(selectorToAdd.getUid())))
                                    selectorsList.add(selectorToAdd);
                            }
                        }
                    } else {
                        Log.w("NotificationService", "Error getting documents: ", task.getException());
                    }
                });

                // Send notification with message
                sendLunchNotification(notification, rName[0], rAddress[0], selectorsList);
            }
        });
        */
    }

}
