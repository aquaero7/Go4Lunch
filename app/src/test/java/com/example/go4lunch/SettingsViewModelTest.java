package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import static com.example.go4lunch.model.repository.RestaurantRepository.DEFAULT_RADIUS;

import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.viewmodel.SettingsViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

public class SettingsViewModelTest {
    private MockitoSession mockito;
    private UserRepository userRepositoryMock;
    private RestaurantRepository restaurantRepositoryMock;
    private SettingsViewModel settingsViewModel;
    private String id1, name1, email1, url1, selId1, selDate1, selName1, selAddress1, radius1, notifications1;
    private User currentUser;

    private void initializeData() {
        // Set and start Mockito strictness
        mockito = Mockito.mockitoSession()
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();
        userRepositoryMock = mock(UserRepository.class);
        restaurantRepositoryMock = mock(RestaurantRepository.class);

        settingsViewModel = new SettingsViewModel(userRepositoryMock, restaurantRepositoryMock);

        id1 = "id1"; name1 = "name1"; email1 = "email1"; url1 = "url1";
        selId1 = "selId1"; selDate1 = "selDate1"; selName1 = "selName1"; selAddress1 = "selAddress1";
        radius1 = "radius1"; notifications1 = "true";

        currentUser = new User(id1, name1, email1, url1, selId1, selDate1, selName1, selAddress1, radius1, notifications1);
    }

    @Before // Before each test
    public void setup() {
        initializeData();
    }

    @After  // After each test
    public void tearDown() {
        // Stop Mockito strictness
        mockito.finishMocking();
    }


    /*********
     * Tests *
     *********/

    @Test
    public void updateSearchRadiusPrefsWithSuccess() {
        assertTrue(settingsViewModel.updateSearchRadiusPrefs("2"));
        assertFalse(settingsViewModel.updateSearchRadiusPrefs("0"));
        assertFalse(settingsViewModel.updateSearchRadiusPrefs(""));

        verify(userRepositoryMock, times(1)).updateSearchRadiusPrefs("2");
        verify(userRepositoryMock, times(2)).updateSearchRadiusPrefs(null);

        verify(userRepositoryMock, times(1)).updateCurrentUser("RAD","2");
        verify(userRepositoryMock, times(2)).updateCurrentUser("RAD",null);

        verify(userRepositoryMock, times(1)).updateWorkmates("RAD","2");
        verify(userRepositoryMock, times(2)).updateWorkmates("RAD",null);
    }

    @Test
    public void updateNotificationsPrefsWithSuccess() {
        assertTrue(settingsViewModel.updateNotificationsPrefs("true"));
        assertFalse(settingsViewModel.updateNotificationsPrefs("false"));
        assertFalse(settingsViewModel.updateNotificationsPrefs(null));

        verify(userRepositoryMock, times(1)).updateNotificationsPrefs("true");
        verify(userRepositoryMock, times(2)).updateNotificationsPrefs(null);

        verify(userRepositoryMock, times(1)).updateCurrentUser("NOT","true");
        verify(userRepositoryMock, times(2)).updateCurrentUser("NOT",null);

        verify(userRepositoryMock, times(1)).updateWorkmates("NOT","true");
        verify(userRepositoryMock, times(2)).updateWorkmates("NOT",null);
    }

    @Test
    public void getCurrentUserWithSuccess() {
        settingsViewModel.getCurrentUser();
        verify(userRepositoryMock, times(1)).getCurrentUser();
    }

    @Test
    public void getSearchRadiusWithSuccess() {
        // If user search radius pref isn't null then return user pref
        assertEquals(radius1, settingsViewModel.getSearchRadius(currentUser));

        // If user search radius pref is null then return default
        currentUser.setSearchRadiusPrefs(null);
        when(restaurantRepositoryMock.getDefaultRadius()).thenCallRealMethod();
        assertEquals(DEFAULT_RADIUS, settingsViewModel.getSearchRadius(currentUser));
        verify(restaurantRepositoryMock, times(1)).getDefaultRadius();
    }

    @Test
    public void getNotificationsPrefsWithSuccess() {
        assertEquals(notifications1, settingsViewModel.getNotificationsPrefs(currentUser));
    }

}
