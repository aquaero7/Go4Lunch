package com.example.go4lunch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.example.go4lunch.model.api.model.Geometry;
import com.example.go4lunch.model.api.model.OpenClose;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Period;
import com.example.go4lunch.model.model.LikedRestaurant;
import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.repository.LikedRestaurantRepository;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.model.repository.UserRepository;
import com.example.go4lunch.utils.Utils;
import com.example.go4lunch.viewmodel.DetailRestaurantViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DetailRestaurantViewModelTest {
    private MockitoSession mockito;
    private UserRepository userRepositoryMock;
    private RestaurantRepository restaurantRepositoryMock;
    private LikedRestaurantRepository likedRestaurantRepositoryMock;
    private DetailRestaurantViewModel detailRestaurantViewModel;
    private Context contextMock;
    private Utils utilsMock;
    private Utils utils;
    private String currentDate;

    private String uId1, uName1, uEmail1, uUrl1, selId1, selDate1, selName1, selAddress1, radius1, notifications1;
    private User user1;
    private String uId2, uName2, uEmail2, uUrl2, selId2, selDate2, selName2, selAddress2, radius2, notifications2;
    private User user2;
    private String uId3, uName3, uEmail3, uUrl3, selId3, selDate3, selName3, selAddress3, radius3, notifications3;
    private User user3;
    private String uId4, uName4, uEmail4, uUrl4, selId4, selDate4, selName4, selAddress4, radius4, notifications4;
    private long today, tomorrow, otherDay;
    private Period weekP, otherDayP1, otherDayP2, todayP24, todayP, todayP1, todayP2, todayP2a;
    private List<Period> periods3, periods4, periods5, periods6, periods7, periods8;
    private OpeningHours oh2, oh3, oh4, oh5, oh6, oh7, oh8;
    private String closed, open247, open24, openUntil, openAt, unknown;
    private User user4;
    private Restaurant restaurant;

    private void initializeData() {
        // Set and start Mockito strictness
        mockito = Mockito.mockitoSession()
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();

        userRepositoryMock = mock(UserRepository.class);
        restaurantRepositoryMock = mock(RestaurantRepository.class);
        likedRestaurantRepositoryMock = mock(LikedRestaurantRepository.class);
        utilsMock = mock(Utils.class);
        contextMock = mock(Context.class);
        utils = Utils.getNewInstance(mock(InputMethodManager.class));

        uId1 = "uId1"; uName1 = "uName1"; uEmail1 = "uEmail1"; uUrl1 = "uUrl1";
        selId1 = "rId1"; selDate1 = "selDate1"; selName1 = "rName1"; selAddress1 = "rAddress1";
        radius1 = "radius1"; notifications1 = "true";
        user1 = new User(uId1, uName1, uEmail1, uUrl1, selId1, selDate1, selName1, selAddress1, radius1, notifications1);

        uId2 = "uId2"; uName2 = "uName2"; uEmail2 = "uEmail2"; uUrl2 = "uUrl2";
        selId2 = "rId2"; selDate2 = "selDate2"; selName2 = "rName2"; selAddress2 = "rAddress2";
        radius2 = "radius2"; notifications2 = "true";
        user2 = new User(uId2, uName2, uEmail2, uUrl2, selId2, selDate2, selName2, selAddress2, radius2, notifications2);

        uId3 = "uId3"; uName3 = "uName3"; uEmail3 = "uEmail3"; uUrl3 = "uUrl3";
        selId3 = "rId1"; selDate3 = "selDate3"; selName3 = "rName1"; selAddress3 = "rAddress1";
        radius3 = "radius3"; notifications3 = "true";
        user3 = new User(uId3, uName3, uEmail3, uUrl3, selId3, selDate3, selName3, selAddress3, radius3, notifications3);

        uId4 = "uId4"; uName4 = "uName4"; uEmail4 = "uEmail4"; uUrl4 = "uUrl4";
        selId4 = "rId1"; selDate4 = "selDate3"; selName4 = "rName1"; selAddress4 = "rAddress1";
        radius4 = "radius4"; notifications4 = "false";
        user4 = new User(uId4, uName4, uEmail4, uUrl4, selId4, selDate4, selName4, selAddress4, radius4, notifications4);

        restaurant = new Restaurant("rId", "rName", new ArrayList<>(), "rAddress",
                1, new OpeningHours(),"phoneNumber", "website", new Geometry(), 1);

        currentDate = utils.getCurrentDate();
        // DayOfWeek converted to Google PlaceOpeningHoursPeriodDetail format
        today = (utils.getCurrentDayOfWeek() != 7) ? utils.getCurrentDayOfWeek() : 0;
        tomorrow = (today != 6) ? today + 1 : 0;
        otherDay = (today != 0) ? today - 1 : 6;

        weekP = new Period(null, new OpenClose(0, "0000"));
        otherDayP1 = new Period(new OpenClose(otherDay, "1400"), new OpenClose(otherDay, "1100"));
        otherDayP2 = new Period(new OpenClose(otherDay, "2300"), new OpenClose(otherDay, "1900"));
        todayP24 = new Period(new OpenClose(tomorrow, "0000"), new OpenClose(today, "0000"));
        todayP = new Period(new OpenClose(today, "2200"), new OpenClose(today, "1000"));
        todayP1 = new Period(new OpenClose(today, "1400"), new OpenClose(today, "1100"));
        todayP2 = new Period(new OpenClose(today, "2300"), new OpenClose(today, "1900"));
        todayP2a = new Period(new OpenClose(tomorrow, "0100"), new OpenClose(today, "1900"));

        periods3 = Collections.singletonList(weekP);
        periods4 = Arrays.asList(otherDayP1, otherDayP2);
        periods5 = Arrays.asList(otherDayP1, otherDayP2, todayP24);
        periods6 = Arrays.asList(otherDayP1, otherDayP2, todayP);
        periods7 = Arrays.asList(otherDayP1, otherDayP2, todayP1, todayP2);
        periods8 = Arrays.asList(otherDayP1, otherDayP2, todayP1, todayP2a);

        oh2 = new OpeningHours(false, null, new ArrayList<>());
        oh3 = new OpeningHours(false, periods3, new ArrayList<>());
        oh4 = new OpeningHours(false, periods4, new ArrayList<>());
        oh5 = new OpeningHours(false, periods5, new ArrayList<>());
        oh6 = new OpeningHours(false, periods6, new ArrayList<>());
        oh7 = new OpeningHours(false, periods7, new ArrayList<>());
        oh8 = new OpeningHours(false, periods8, new ArrayList<>());

        closed = "Closed";
        open247  = "Open 24/7";
        open24 = "Open all day";
        openUntil = "Open until ";
        openAt = "Open at ";
        unknown = "Unknown opening hours";

        // Class under test
        detailRestaurantViewModel = new DetailRestaurantViewModel(userRepositoryMock, restaurantRepositoryMock, likedRestaurantRepositoryMock, utilsMock);
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
    public void getRestaurantDetailsMutableLiveDataWithSuccess() {
        detailRestaurantViewModel.getRestaurantDetailsMutableLiveData();
        verify(restaurantRepositoryMock, times(1)).getRestaurantDetailsMutableLiveData();
    }

    @Test
    public void getWorkmatesMutableLiveDataWithSuccess() {
        detailRestaurantViewModel.getWorkmatesMutableLiveData();
        verify(userRepositoryMock, times(1)).getWorkmatesMutableLiveData();
    }

    @Test
    public void getLikedRestaurantsMutableLiveDataWithSuccess() {
        detailRestaurantViewModel.getLikedRestaurantsMutableLiveData();
        verify(likedRestaurantRepositoryMock, times(1)).getLikedRestaurantsMutableLiveData();
    }

    @Test
    public void fetchRestaurantDetailsWithSuccess() {
        detailRestaurantViewModel.fetchRestaurantDetails(restaurant, "");
        verify(restaurantRepositoryMock, times(1)).fetchRestaurantDetails(restaurant, "");
    }

    @Test
    public void updateLikedRestaurantWithSuccess() {
        /** Also testing createLikedRestaurant() and deleteLikedRestaurant() called within this method only */

        when(userRepositoryMock.getFbCurrentUserId()).thenReturn("uId");

        // Restaurant isn't already liked
        when(likedRestaurantRepositoryMock.isRestaurantLiked()).thenReturn(false);
        assertTrue(detailRestaurantViewModel.updateLikedRestaurant("rId"));
        verify(userRepositoryMock, times(1)).getFbCurrentUserId();
        verify(likedRestaurantRepositoryMock, times(1)).createLikedRestaurant("rIduId", "rId", "uId");
        verify(likedRestaurantRepositoryMock, times(1)).updateLikedRestaurants("rIduId", "rId", "uId");

        // Restaurant is already liked
        when(likedRestaurantRepositoryMock.isRestaurantLiked()).thenReturn(true);
        assertFalse(detailRestaurantViewModel.updateLikedRestaurant("rId"));
        verify(userRepositoryMock, times(2)).getFbCurrentUserId();
        verify(likedRestaurantRepositoryMock, times(1)).deleteLikedRestaurant("rIduId");
        verify(likedRestaurantRepositoryMock, times(1)).updateLikedRestaurants("rIduId");
    }

    @Test
    public void updateSelectionWithSuccess() {
        /** Also testing createSelection() and deleteSelection() called within this method only */

        when(utilsMock.getCurrentDate()).thenCallRealMethod();

        // Restaurant isn't already selected
        when(restaurantRepositoryMock.isRestaurantSelected()).thenReturn(false);
        assertTrue(detailRestaurantViewModel.updateSelection("rId", "rName", "rAddress"));
        verify(userRepositoryMock, times(1)).updateSelectionId("rId");
        verify(userRepositoryMock, times(1)).updateSelectionDate(currentDate);
        verify(userRepositoryMock, times(1)).updateSelectionName("rName");
        verify(userRepositoryMock, times(1)).updateSelectionAddress("rAddress");
        verify(userRepositoryMock, times(1)).updateCurrentUser("rId", currentDate, "rName", "rAddress");
        verify(userRepositoryMock, times(1)).updateWorkmates("rId", currentDate, "rName", "rAddress");

        // Restaurant is already selected
        when(restaurantRepositoryMock.isRestaurantSelected()).thenReturn(true);
        assertFalse(detailRestaurantViewModel.updateSelection("rId", "rName", "rAddress"));
        verify(userRepositoryMock, times(1)).updateSelectionId(null);
        verify(userRepositoryMock, times(1)).updateSelectionDate(null);
        verify(userRepositoryMock, times(1)).updateSelectionName(null);
        verify(userRepositoryMock, times(1)).updateSelectionAddress(null);
        verify(userRepositoryMock, times(1)).updateCurrentUser(null, null, null, null);
        verify(userRepositoryMock, times(1)).updateWorkmates(null, null, null, null);
    }

    @Test
    public void checkCurrentUserSelectionWithSuccess() {
        when(userRepositoryMock.getFbCurrentUserId()).thenReturn("uId3");
        List<User> selectors;

        // Restaurant isn't selected by current user
        selectors = Arrays.asList(user1, user2);
        detailRestaurantViewModel.checkCurrentUserSelection(selectors);
        verify(restaurantRepositoryMock, times(1)).setRestaurantSelected(false);
        verify(restaurantRepositoryMock, never()).setRestaurantSelected(true);
        verify(userRepositoryMock, times(2)).getFbCurrentUserId();
        verify(restaurantRepositoryMock, times(1)).isRestaurantSelected();

        // Restaurant is selected by current user
        selectors = Arrays.asList(user1, user2, user3);
        detailRestaurantViewModel.checkCurrentUserSelection(selectors);
        verify(restaurantRepositoryMock, times(2)).setRestaurantSelected(false);
        verify(restaurantRepositoryMock, times(1)).setRestaurantSelected(true);
        verify(userRepositoryMock, times(5)).getFbCurrentUserId();
        verify(restaurantRepositoryMock, times(2)).isRestaurantSelected();
    }

    @Test
    public void checkCurrentUserLikesWithSuccess() {
        when(userRepositoryMock.getFbCurrentUserId()).thenReturn("uId3");
        List<LikedRestaurant> likedRestaurants = Arrays.asList(
                new LikedRestaurant("rId1uId1", "rId1", "uId1"),
                new LikedRestaurant("rId2uId3", "rId2", "uId3"),
                new LikedRestaurant("rId3uId3", "rId3", "uId3"));

        // Restaurant isn't liked by current user
        detailRestaurantViewModel.checkCurrentUserLikes("rId1", likedRestaurants);
        verify(userRepositoryMock, times(1)).getFbCurrentUserId();
        verify(likedRestaurantRepositoryMock, times(1)).setRestaurantLiked(false);
        verify(likedRestaurantRepositoryMock, never()).setRestaurantLiked(true);
        verify(likedRestaurantRepositoryMock, times(1)).isRestaurantLiked();

        // Restaurant is liked by current user
        detailRestaurantViewModel.checkCurrentUserLikes("rId2", likedRestaurants);
        verify(userRepositoryMock, times(2)).getFbCurrentUserId();
        verify(likedRestaurantRepositoryMock, times(2)).setRestaurantLiked(false);
        verify(likedRestaurantRepositoryMock, times(1)).setRestaurantLiked(true);
        verify(likedRestaurantRepositoryMock, times(2)).isRestaurantLiked();

        // The list of liked restaurants is null
        detailRestaurantViewModel.checkCurrentUserLikes("rId3", null);
        verify(userRepositoryMock, times(3)).getFbCurrentUserId();
        verify(likedRestaurantRepositoryMock, times(3)).setRestaurantLiked(false);
        verify(likedRestaurantRepositoryMock, times(1)).setRestaurantLiked(true);
        verify(likedRestaurantRepositoryMock, times(3)).isRestaurantLiked();
    }

    @Test
    public void sortByNameWithSuccess() {
        List<User> workmates = new ArrayList<>();
        detailRestaurantViewModel.sortByName(workmates);
        verify(userRepositoryMock, times(1)).sortByName(workmates);
    }

    @Test
    public void sortByAscendingOpeningTimeWithSuccess() {
        List<Period> periods = new ArrayList<>();
        detailRestaurantViewModel.sortByAscendingOpeningTime(periods);
        verify(restaurantRepositoryMock, times(1)).sortByAscendingOpeningTime(periods);
    }

    @Test
    public void sortByDescendingOpeningTimeWithSuccess() {
        List<Period> periods = new ArrayList<>();
        detailRestaurantViewModel.sortByDescendingOpeningTime(periods);
        verify(restaurantRepositoryMock, times(1)).sortByDescendingOpeningTime(periods);
    }

    @Test
    public void getOpeningInformationWithSuccess() {
        when(contextMock.getString(R.string.status_closed)).thenReturn(closed);
        when(contextMock.getString(R.string.status_open247)).thenReturn(open247);
        when(contextMock.getString(R.string.status_open24)).thenReturn(open24);
        when(contextMock.getString(R.string.status_open_until)).thenReturn(openUntil);
        when(contextMock.getString(R.string.status_open_at)).thenReturn(openAt);
        when(contextMock.getString(R.string.status_unknown)).thenReturn(unknown);
        when(utilsMock.getCurrentDayOfWeek()).thenCallRealMethod();
        when(utilsMock.getCurrentTime()).thenCallRealMethod();
        doCallRealMethod().when(restaurantRepositoryMock).sortByAscendingOpeningTime(anyList());

        // Scenario 1 : openingHours = null
        restaurant.setOpeningHours(null);
        assertEquals(unknown, detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

        // Scenario 2 : openingHours.periods = null
        restaurant.setOpeningHours(oh2);
        assertEquals(unknown, detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

        // Scenario 3 : periods.size = 1 (only 1 period)
        restaurant.setOpeningHours(oh3);
        assertEquals(open247, detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

        // Scenario 4 : todayPeriods.size = 0 (no today period)
        restaurant.setOpeningHours(oh4);
        assertEquals(closed, detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

        // Scenario 5 : todayPeriods.size = 1 (1 today period, all day long)
        restaurant.setOpeningHours(oh5);
        assertEquals(open24, detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

        // Scenario 6 : todayPeriods.size = 1 (1 today period, part of the day)
        restaurant.setOpeningHours(oh6);

            // Case 6.1 : Current time is before the period opening time
            when(utilsMock.getCurrentTime()).thenReturn("0900");
            assertEquals(openAt + "10h00", detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

            // Case 6.2 : Current time is inside the period
            when(utilsMock.getCurrentTime()).thenReturn("1500");
            assertEquals(openUntil + "22h00", detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

            // Case 6.3 : Current time is after the period closing time
            when(utilsMock.getCurrentTime()).thenReturn("2300");
            assertEquals(closed, detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

        // Scenario 7 : todayPeriods.size = 2 (2 today periods. Last period closing today)
        restaurant.setOpeningHours(oh7);

            // Case 7.1 : Current time is before the first period opening time
            when(utilsMock.getCurrentTime()).thenReturn("0900");
            assertEquals(openAt + "11h00", detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

            // Case 7.2 : Current time is inside the first period
            when(utilsMock.getCurrentTime()).thenReturn("1300");
            assertEquals(openUntil + "14h00", detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

            // Case 7.3 : Current time is after the first period closing time (and before the last period opening time)
            when(utilsMock.getCurrentTime()).thenReturn("1600");
            assertEquals(openAt + "19h00", detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

            // Case 7.4 : Current time is inside the last period
            when(utilsMock.getCurrentTime()).thenReturn("2000");
            assertEquals(openUntil + "23h00", detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

            // Case 7.5 : Current time is after the last period closing time
            when(utilsMock.getCurrentTime()).thenReturn("2330");
            assertEquals(closed, detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));

        // Scenario 8 : todayPeriods.size = 2 (2 today periods. Last period closing tomorrow)
        restaurant.setOpeningHours(oh8);
        // Current time is inside the last period (other cases are already tested in scenarii 7)
        when(utilsMock.getCurrentTime()).thenReturn("2330");
        assertEquals(openUntil + "01h00", detailRestaurantViewModel.getOpeningInformation(restaurant, contextMock));
    }

    @Test
    public void getRestaurantWithSuccess() {
        detailRestaurantViewModel.getRestaurant();
        verify(restaurantRepositoryMock, times(1)).getRestaurant();
    }

    @Test
    public void getSelectorsWithArgsWithSuccess() {
        // Restaurant is selected by users 1, 4 and 3, but only users 4 and 3 at current date
        user3.setSelectionDate(utils.getCurrentDate());
        user4.setSelectionDate(utils.getCurrentDate());
        List<User> workmates = Arrays.asList(user2, user1, user4, user3);

        when(utilsMock.getCurrentDate()).thenCallRealMethod();
        doCallRealMethod().when(userRepositoryMock).sortByName(anyList());

        List<User> selectors = detailRestaurantViewModel.getSelectors("rId1", workmates);

        verify(userRepositoryMock, times(1)).setSelectors(selectors);
        // We verify if the selectors list is correct and correctly sorted (user3 before user4)
        assertEquals(Arrays.asList(user3, user4), selectors);
    }

    @Test
    public void getSelectorsWithSuccess() {
        detailRestaurantViewModel.getSelectors();
        verify(userRepositoryMock, times(1)).getSelectors();
    }

    @Test
    public void setRestaurantWithSuccess() {
        detailRestaurantViewModel.setRestaurant(restaurant);
        verify(restaurantRepositoryMock, times(1)).setRestaurant(restaurant);
    }

}
