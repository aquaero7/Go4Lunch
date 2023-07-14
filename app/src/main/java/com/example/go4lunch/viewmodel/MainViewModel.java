package com.example.go4lunch.viewmodel;

import android.app.AlertDialog;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.MainApplication;
import com.example.go4lunch.R;
import com.example.go4lunch.model.model.DialogTuple;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.LocationRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.utils.Utils;
import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainViewModel extends ViewModel {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final LikedRestaurantRepository likedRestaurantRepository;
    // private final Application application; // Only if MainViewModel extends AndroidViewModel


    // Constructor
    public MainViewModel(/*@NonNull Application application*/) { // Only if MainViewModel extends AndroidViewModel
        // super(application); // Only if MainViewModel extends AndroidViewModel
        // this.application = application; // Only if MainViewModel extends AndroidViewModel

        locationRepository = LocationRepository.getInstance();
        userRepository = UserRepository.getInstance();
        restaurantRepository = RestaurantRepository.getInstance();
        likedRestaurantRepository = LikedRestaurantRepository.getInstance();
    }


    /************
     * LiveData *
     ************/

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        return userRepository.getCurrentUserMutableLiveData();
    }


    /***********
     * Methods *
     ***********/

    // Fetchers (using Maps and Firebase APIs)

    public void fetchCurrentUser() {
        userRepository.fetchCurrentUser();
    }


    // Actions

    public void deleteUserLikesAndUser() {
        // Delete current user's likes from Firestore liked restaurants collection
        deleteUserLikes();
        // Delete current user from Firestore users collection
        deleteUser();
    }

    public void deleteUserLikes() {
        for (LikedRestaurant likedRestaurant : likedRestaurantRepository.getLikedRestaurants()) {
            if (likedRestaurant.getUid().equals(getFbCurrentUser().getUid())) {
                likedRestaurantRepository.deleteLikedRestaurant(likedRestaurant.getId());
            }
        }
    }

    public void deleteUser() {
        userRepository.deleteUser(getFbCurrentUser().getUid());
    }

    public Task<Void> deleteFbUser() {
        // Method using AuthUI
        // return userRepository.deleteFbUser(application.getApplicationContext()); // Only if MainViewModel extends AndroidViewModel
        return userRepository.deleteFbUser(MainApplication.getContext());
        // Method using FirebaseAuth
        // return userRepository.deleteFbUser();
    }

    // Method using AuthUI
    public Task<Void> signOut() {
        // return userRepository.signOut(application.getApplicationContext()); // Only if MainViewModel extends AndroidViewModel
        return userRepository.signOut(MainApplication.getContext());
    }
    //
    /* Method using FirebaseAuth
    public void signOut() {
        userRepository.signOut();
    }
    */

    public DialogTuple<AlertDialog, MutableLiveData<Boolean>> buildConfirmationDialog(AlertDialog.Builder builder) {
        MutableLiveData<Boolean> dialogResponseMutableLiveData = new MutableLiveData<>(null);
        // Add the buttons to builder
        builder.setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    dialogResponseMutableLiveData.setValue(true);
                })
                .setNegativeButton(R.string.dialog_button_cancel, (dialog, which) -> {
                    dialogResponseMutableLiveData.setValue(false);
                })
                // Chain together various setter methods to set the dialog characteristics
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message);
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        // Set color for each button text (so, no need to set theme when building AlertDialog)
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MainApplication.getInstance().getColor(R.color.green_fab));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MainApplication.getInstance().getColor(R.color.red));
        });
        return new DialogTuple<>(dialog, dialogResponseMutableLiveData);
    }


    // Getters

    public boolean arePermissionsGranted() {
        return locationRepository.arePermissionsGranted();
    }

    public boolean isFbCurrentUserLogged() {
        return userRepository.isFbCurrentUserLogged();
    }

    public FirebaseUser getFbCurrentUser() {
        return userRepository.getFbCurrentUser();
    }

    public final String[] getTabTitles() {
        final String MAP_VIEW_TAB_TITLE = MainApplication.getInstance().getString(R.string.tab_map_view);
        final String LIST_VIEW_TAB_TITLE = MainApplication.getInstance().getString(R.string.tab_list_view);
        final String WORKMATES_TAB_TITLE = MainApplication.getInstance().getString(R.string.tab_workmates);
        return new String[] {MAP_VIEW_TAB_TITLE, LIST_VIEW_TAB_TITLE, WORKMATES_TAB_TITLE};
    }

    public final int[] getTabIcons() {
        return new int[] {
                R.drawable.ic_baseline_map_black_24,
                R.drawable.ic_baseline_view_list_black_24,
                R.drawable.ic_baseline_group_black_24
        };
    }

    public RestaurantWithDistance getCurrentUserSelection() {
        // Get current user selected restaurant
        String selectionId = userRepository.getCurrentUser().getSelectionId();
        String selectionDate = userRepository.getCurrentUser().getSelectionDate();
        RestaurantWithDistance selectedRestaurant = null;
        // If a restaurant is selected, check if selected restaurant is nearby
        if ((selectionId != null) && (Utils.getCurrentDate().equals(selectionDate))) {
            for (RestaurantWithDistance restaurant : restaurantRepository.getRestaurants()) {
                if (Objects.equals(selectionId, restaurant.getRid())) {
                    selectedRestaurant = restaurant;
                    break;
                }
            }
        }
        return selectedRestaurant;
    }

    public AuthCredential getCredential(String signInProvider, String passWord) {
        AuthCredential credential = null;
        String email = getFbCurrentUser().getEmail();
        switch (Objects.requireNonNull(signInProvider)) {
            case "google.com":
                credential = GoogleAuthProvider.getCredential(Objects.requireNonNull(email), passWord);
                break;
            case "facebook.com":
                credential = FacebookAuthProvider.getCredential(Objects.requireNonNull(AccessToken.getCurrentAccessToken()).getToken());
                break;
            case "twitter.com":
                // ???
                break;
            case "password":
                credential = EmailAuthProvider.getCredential(Objects.requireNonNull(email), passWord);
                break;
        }
        return credential;
    }


}
