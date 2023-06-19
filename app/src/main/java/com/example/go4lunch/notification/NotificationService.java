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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.go4lunch.R;
import com.example.go4lunch.activity.MainActivity;
import com.example.go4lunch.manager.UserManager;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.utils.CalendarUtils;
import com.example.go4lunch.utils.FirestoreUtils;
import com.example.go4lunch.viewmodel.RestaurantViewModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends FirebaseMessagingService {

    private final int NOTIFICATION_ID = 4;
    private final String NOTIFICATION_TAG = "GO4LUNCH";

    String currentDate = CalendarUtils.getCurrentDate();
    User currentUser;
    String currentUserId;
    String selectionId;
    String selectionDate;
    String notificationPrefs;
    String notificationBodyText;
    UserManager userManager = UserManager.getInstance();


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            // Get message sent by Firebase
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            Log.i("NotificationService", notification.getTitle() + "\n" + notification.getBody());

            userManager.getCurrentUserData()
                    .addOnSuccessListener(user -> {
                        currentUser = user;
                        currentUserId = user.getUid();
                        selectionId = user.getSelectionId();
                        selectionDate = user.getSelectionDate();
                        notificationPrefs = user.getNotificationsPrefs();

                        // Check if current user has selected a restaurant
                        boolean aRestaurantIsSelected = selectionId != null && currentDate.equals(selectionDate);

                        // Send notification with message only if current user has selected a restaurant
                        if (aRestaurantIsSelected) {
                            // Send notification with message if preferences parameter is set true
                            if (Boolean.parseBoolean(notificationPrefs)) {
                                // Get selected restaurant data from restaurants collection in database
                                getSelectedRestaurantData();    // TODO



                                    // Get the list of workmates with the same selection (other than current user)
                                    getSelectorsList();             // TODO


                                        // Create notification body text
                                        createNotificationBodyText(rName, rAddress, selectorsNameList);
                                        // Send notification
                                        sendLunchNotification();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w("NotificationService", e.getMessage());
                    });
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
        CharSequence channelName = "Firebase Messages";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
        notificationManager.createNotificationChannel(mChannel);

        // Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
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

    /*
    private void getSelectorsList() {
        selectorsList = new ArrayList<>();
        List<User> workmatesList = FirestoreUtils.getWorkmatesList();
        for (User workmate : workmatesList) {
            // Check selected restaurant id and date and get users list
            boolean isSelector = (selectionId.equals(workmate.getSelectionId())
                    && currentDate.equals(workmate.getSelectionDate()));
            // Don't add current user to the list of (other) selectors
            if (isSelector && !(currentUserId.equals(workmate.getUid()))) selectorsList.add(workmate);
        }
    }

    private void getSelectedRestaurantData() {
        List<Restaurant> restaurantsList = FirestoreUtils.getRestaurantsList();
        for (Restaurant restaurant : restaurantsList) {
            if (selectionId.equals(restaurant.getRid())) {
                rName = restaurant.getName();
                rAddress = restaurant.getAddress();
            }
        }
    }
    */

}
