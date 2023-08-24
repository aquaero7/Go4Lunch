package com.example.go4lunch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.api.GmapsApiInterface;
import com.example.go4lunch.model.api.GmapsRestaurantDetailsPojo;
import com.example.go4lunch.model.api.GmapsRestaurantPojo;
import com.example.go4lunch.model.api.model.Geometry;
import com.example.go4lunch.model.api.model.Location;
import com.example.go4lunch.model.api.model.OpenClose;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Period;
import com.example.go4lunch.model.api.model.Photo;
import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.utils.LiveDataTestUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepositoryTest {
    private MockitoSession mockito;
    private Call<GmapsRestaurantPojo> call1Mock;
    private Response<GmapsRestaurantPojo> response1Mock;
    private Call<GmapsRestaurantDetailsPojo> call2Mock;
    private Response<GmapsRestaurantDetailsPojo> response2Mock;
    private GmapsRestaurantPojo nearPlacesMock;
    private GmapsRestaurantDetailsPojo placeDetailsMock;
    private GmapsApiInterface apiClientMock;
    private RestaurantRepository restaurantRepository;
    private List<Restaurant> fakeRestaurants;
    private List<RestaurantWithDistance> fakeRestaurantsWithDistance;
    private GmapsRestaurantPojo fakeNearPlaces;
    private GmapsRestaurantDetailsPojo fakePlaceDetails;
    List<Photo> photos1, photos2, photos3, photos4;
    OpeningHours openingHours1, openingHours2, openingHours3, openingHours4;
    LatLng latLng1, latLng2, latLng3, latLng4, currentLatLng;
    Geometry geometry1, geometry2, geometry3, geometry4;
    double distance1, distance2, distance3, distance4;
    Restaurant restaurant1, restaurant2, restaurant3, restaurant4D;
    RestaurantWithDistance restaurantWithDistance1, restaurantWithDistance2, restaurantWithDistance3, restaurantWithDistance4D;
    List<Period> periods4;
    List<String> weekdayText4;

    @Rule
    // Needed for the use of LiveDataTestUtils
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private void initializeData() {
        // Set and start Mockito strictness
        mockito = Mockito.mockitoSession()
                .strictness(Strictness.STRICT_STUBS)
                // .strictness(Strictness.LENIENT)
                .startMocking();

        call1Mock = mock(Call.class);
        call2Mock = mock(Call.class);
        response1Mock = mock(Response.class);
        response2Mock = mock(Response.class);
        nearPlacesMock = mock(GmapsRestaurantPojo.class);
        placeDetailsMock = mock(GmapsRestaurantDetailsPojo.class);

        apiClientMock = mock(GmapsApiInterface.class);
        // Class under test
        restaurantRepository = RestaurantRepository.getNewInstance(apiClientMock);

        photos1 = new ArrayList<>();
        photos2 = new ArrayList<>();
        photos3 = new ArrayList<>();
        openingHours1 = new OpeningHours();
        openingHours2 = new OpeningHours();
        openingHours3 = new OpeningHours();
        latLng1 = new LatLng(48.9, 2.3);
        latLng2 = new LatLng(48.9, 2.3);
        latLng3 = new LatLng(48.8, 2.2);
        geometry1 = new Geometry(new Location(latLng1.latitude, latLng1.longitude));
        geometry2 = new Geometry(new Location(latLng2.latitude, latLng2.longitude));
        geometry3 = new Geometry(new Location(latLng3.latitude, latLng3.longitude));

        currentLatLng = new LatLng(48.7, 2.1);
        distance1 = (long) SphericalUtil.computeDistanceBetween(currentLatLng, latLng1);
        distance2 = (long) SphericalUtil.computeDistanceBetween(currentLatLng, latLng2);
        distance3 = (long) SphericalUtil.computeDistanceBetween(currentLatLng, latLng3);

        restaurant1 = new Restaurant(
                "rId1", "rName1", photos1, "rAddress1", 1D,
                openingHours1, "rPhoneNumber1", "rWebsite1", geometry1);
        restaurant2 = new Restaurant(
                "rId2", "rName2", photos2, "rAddress2", 2D,
                openingHours2, "rPhoneNumber2", "rWebsite2", geometry2);
        restaurant3 = new Restaurant(
                "rId3", "rName3", photos3, "rAddress3", 3D,
                openingHours3, "rPhoneNumber3", "rWebsite3", geometry3);
        restaurantWithDistance1 = new RestaurantWithDistance(
                "rId1", "rName1", photos1, "rAddress1", 1D,
                openingHours1, "rPhoneNumber1", "rWebsite1", geometry1, (long) distance1);
        restaurantWithDistance2 = new RestaurantWithDistance(
                "rId2", "rName2", photos2, "rAddress2", 2D,
                openingHours2, "rPhoneNumber2", "rWebsite2", geometry2, (long) distance2);
        restaurantWithDistance3 = new RestaurantWithDistance(
                "rId3", "rName3", photos3, "rAddress3", 3D,
                openingHours3, "rPhoneNumber3", "rWebsite3", geometry3, (long) distance3);

        fakeRestaurants = new ArrayList<>(Arrays.asList(restaurant2, restaurant3, restaurant1));    // Random order to test sort
        fakeNearPlaces = new GmapsRestaurantPojo(fakeRestaurants, "", "", "");
        fakeRestaurantsWithDistance = new ArrayList<>(Arrays.asList(restaurantWithDistance1, restaurantWithDistance2, restaurantWithDistance3));

        periods4 = Collections.singletonList(new Period(null, new OpenClose(0, "0000")));
        weekdayText4 = new ArrayList<>();
        openingHours4 = new OpeningHours(true, periods4, weekdayText4);
        photos4 = new ArrayList<>();
        latLng4 = new LatLng(48.5, 2.0);
        geometry4 = new Geometry(new Location(latLng4.latitude, latLng4.longitude));
        distance4 = SphericalUtil.computeDistanceBetween(currentLatLng, latLng4);

        restaurant4D = new Restaurant(
                "rId4", "rName4", photos4, "rAddress4", 0D,
                openingHours4, "rPhoneNumber4", "rWebsite4", geometry4);
        restaurantWithDistance4D = new RestaurantWithDistance(
                "rId4", "rName4", photos4, "rAddress4", 0D,
                openingHours4, "rPhoneNumber4", "rWebsite4", geometry4, (long) distance4);

        fakePlaceDetails = new  GmapsRestaurantDetailsPojo(restaurant4D, "");
    }

    private int reOrder(int rank) {
        // Determine the test final restaurants order after sort by distance then by name : 3-1-2
        return (rank != 0) ? rank-1 : 2;
    }

    private int reverseOrder(int rank) {
        // Determine the relationship between the order of restaurants in
        // fakeRestaurantsWithDistance relative to fakeRestaurants : 2-3-1
        return (rank != 2) ? rank+1 : 0;
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
    public void getNewInstanceWithSuccess() {
        assertNotNull(RestaurantRepository.getNewInstance(apiClientMock));
    }

    @Test
    public void getInstanceWithSuccess() {
        assertNotNull(RestaurantRepository.getInstance());
    }

    @Test
    public void fetchRestaurantsWithSuccess() throws InterruptedException {
        /** Also testing RestaurantRepository.getRestaurantsMutableLiveData() method */

        when(apiClientMock.getPlaces(anyString(), anyString(), anyInt(), anyString())).thenReturn(call1Mock);
        // doReturn(call1Mock).when(apiClientMock).getPlaces(anyString(), anyString(), anyInt(), anyString());
        doAnswer(invocation -> {
            Callback<GmapsRestaurantPojo> callback1 = invocation.getArgument(0);
            callback1.onResponse(call1Mock, Response.success(fakeNearPlaces));
            return null;
        }).when(call1Mock).enqueue(any(Callback.class));

        restaurantRepository.fetchRestaurants(currentLatLng, "1", "key");

        verify(call1Mock, times(1)).enqueue(any());
        verify(apiClientMock, times(1)).getPlaces(
                "restaurant",currentLatLng.latitude + "," + currentLatLng.longitude,1000,"key");
        verify(response1Mock,never()).isSuccessful();
        verify(nearPlacesMock, never()).getNearRestaurants();

        MutableLiveData<List<RestaurantWithDistance>> restaurantsLiveData = restaurantRepository.getRestaurantsMutableLiveData();
        List<RestaurantWithDistance> result = LiveDataTestUtils.getValue(restaurantsLiveData);

        assertEquals(fakeRestaurants.size(), fakeRestaurantsWithDistance.size());
        assertEquals(fakeRestaurants.size(), result.size());
        // Restaurants should be sorted by distance then by name
        for (int i=0; i<fakeRestaurants.size(); i++) {
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getRid(), result.get(i).getRid());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getName(), result.get(i).getName());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getPhotos(), result.get(i).getPhotos());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getAddress(), result.get(i).getAddress());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getRating(), result.get(i).getRating(), 0);
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getOpeningHours(), result.get(i).getOpeningHours());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getPhoneNumber(), result.get(i).getPhoneNumber());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getWebsite(), result.get(i).getWebsite());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getGeometry(), result.get(i).getGeometry());
            assertEquals(fakeRestaurantsWithDistance.get(reOrder(i)).getDistance(), result.get(i).getDistance());
        }
    }

    @Test
    public void fetchRestaurantDetailsWithSuccess() throws InterruptedException {
        /** Also testing RestaurantRepository.getRestaurantDetailsMutableLiveData() method */

        String detailFields = "formatted_address,formatted_phone_number,website,opening_hours";
        when(apiClientMock.getPlaceDetails(anyString(), anyString(), anyString())).thenReturn(call2Mock);
        // doReturn(call2Mock).when(apiClientMock).getPlaceDetails(anyString(), anyString(), anyString());
        doAnswer(invocation -> {
            Callback<GmapsRestaurantDetailsPojo> callback2 = invocation.getArgument(0);
            callback2.onResponse(call2Mock, Response.success(fakePlaceDetails));
            return null;
        }).when(call2Mock).enqueue(any(Callback.class));

        restaurantRepository.fetchRestaurantDetails(restaurantWithDistance4D,"key");

        verify(call2Mock, times(1)).enqueue(any());
        verify(apiClientMock, times(1)).getPlaceDetails(
                restaurant4D.getRid(),detailFields,"key");
        verify(response2Mock,never()).isSuccessful();
        verify(placeDetailsMock, never()).getRestaurantDetails();

        MutableLiveData<RestaurantWithDistance> restaurantDetailsLiveData = restaurantRepository.getRestaurantDetailsMutableLiveData();
        RestaurantWithDistance result = LiveDataTestUtils.getValue(restaurantDetailsLiveData);

        assertEquals(restaurantWithDistance4D.getRid(), result.getRid());
        assertEquals(restaurantWithDistance4D.getName(), result.getName());
        assertEquals(restaurantWithDistance4D.getPhotos(), result.getPhotos());
        assertEquals(restaurantWithDistance4D.getAddress(), result.getAddress());
        assertEquals(restaurantWithDistance4D.getRating(), result.getRating(), 0);
        assertEquals(restaurantWithDistance4D.getOpeningHours(), result.getOpeningHours());
        assertEquals(restaurantWithDistance4D.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(restaurantWithDistance4D.getWebsite(), result.getWebsite());
        assertEquals(restaurantWithDistance4D.getGeometry(), result.getGeometry());
        assertEquals(restaurantWithDistance4D.getDistance(), result.getDistance());
    }

    @Test
    public void updateRestaurantsListWithDistancesWithSuccess() {
        List<RestaurantWithDistance> result = restaurantRepository.updateRestaurantsListWithDistances(fakeRestaurants, currentLatLng);
        assertEquals(fakeRestaurants.size(), result.size());
        for (int i=0; i<fakeRestaurants.size(); i++) {
            assertEquals(fakeRestaurants.get(i).getRid(), result.get(i).getRid());
            assertEquals(fakeRestaurants.get(i).getName(), result.get(i).getName());
            assertEquals(fakeRestaurants.get(i).getPhotos(), result.get(i).getPhotos());
            assertEquals(fakeRestaurants.get(i).getAddress(), result.get(i).getAddress());
            assertEquals(fakeRestaurants.get(i).getRating(), result.get(i).getRating(), 0);
            assertEquals(fakeRestaurants.get(i).getOpeningHours(), result.get(i).getOpeningHours());
            assertEquals(fakeRestaurants.get(i).getPhoneNumber(), result.get(i).getPhoneNumber());
            assertEquals(fakeRestaurants.get(i).getWebsite(), result.get(i).getWebsite());
            assertEquals(fakeRestaurants.get(i).getGeometry(), result.get(i).getGeometry());
            assertEquals(fakeRestaurantsWithDistance.get(reverseOrder(i)).getDistance(), result.get(i).getDistance());
        }
    }

    @Test
    public void calculateRestaurantDistanceWithSuccess() {
        int result;
        result = restaurantRepository.calculateRestaurantDistance(restaurant1, currentLatLng);
        assertEquals(distance1, result, 0);
        result = restaurantRepository.calculateRestaurantDistance(restaurant2, currentLatLng);
        assertEquals(distance2, result, 0);
        result = restaurantRepository.calculateRestaurantDistance(restaurant3, currentLatLng);
        assertEquals(distance3, result, 0);
    }

    @Test
    public void sortByDistanceAndNameWithSuccess() {
        // Before sort
        double[] unsortedDistances = new double[]{distance1, distance2, distance3};
        for (int i=0; i<fakeRestaurantsWithDistance.size(); i++) {
            assertEquals(fakeRestaurants.get(reOrder(i)).getRid(), fakeRestaurantsWithDistance.get(i).getRid());
            assertEquals(fakeRestaurants.get(reOrder(i)).getName(), fakeRestaurantsWithDistance.get(i).getName());
            assertEquals(fakeRestaurants.get(reOrder(i)).getPhotos(), fakeRestaurantsWithDistance.get(i).getPhotos());
            assertEquals(fakeRestaurants.get(reOrder(i)).getAddress(), fakeRestaurantsWithDistance.get(i).getAddress());
            assertEquals(fakeRestaurants.get(reOrder(i)).getRating(), fakeRestaurantsWithDistance.get(i).getRating(), 0);
            assertEquals(fakeRestaurants.get(reOrder(i)).getOpeningHours(), fakeRestaurantsWithDistance.get(i).getOpeningHours());
            assertEquals(fakeRestaurants.get(reOrder(i)).getPhoneNumber(), fakeRestaurantsWithDistance.get(i).getPhoneNumber());
            assertEquals(fakeRestaurants.get(reOrder(i)).getWebsite(), fakeRestaurantsWithDistance.get(i).getWebsite());
            assertEquals(fakeRestaurants.get(reOrder(i)).getGeometry(), fakeRestaurantsWithDistance.get(i).getGeometry());
            assertEquals(unsortedDistances[i], fakeRestaurantsWithDistance.get(i).getDistance(), 0);
        }

        // Sort
        restaurantRepository.sortByDistanceAndName(fakeRestaurantsWithDistance);

        // After sort
        double[] sortedDistances = new double[]{distance3, distance1, distance2};
        for (int i=0; i<fakeRestaurantsWithDistance.size(); i++) {
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getRid(), fakeRestaurantsWithDistance.get(i).getRid());
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getName(), fakeRestaurantsWithDistance.get(i).getName());
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getPhotos(), fakeRestaurantsWithDistance.get(i).getPhotos());
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getAddress(), fakeRestaurantsWithDistance.get(i).getAddress());
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getRating(), fakeRestaurantsWithDistance.get(i).getRating(), 0);
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getOpeningHours(), fakeRestaurantsWithDistance.get(i).getOpeningHours());
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getPhoneNumber(), fakeRestaurantsWithDistance.get(i).getPhoneNumber());
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getWebsite(), fakeRestaurantsWithDistance.get(i).getWebsite());
            assertEquals(fakeRestaurants.get(reverseOrder(i)).getGeometry(), fakeRestaurantsWithDistance.get(i).getGeometry());
            assertEquals(sortedDistances[i], fakeRestaurantsWithDistance.get(i).getDistance(), 0);
        }
    }

    @Test
    public void sortByAscendingOpeningTimeWithSuccess() {
        Period periodA = new Period(null, new OpenClose(0, "1600"));
        Period periodB = new Period(new OpenClose(0, "0000"), new OpenClose(0, "0600"));
        Period periodC = new Period(new OpenClose(1, "0000"), new OpenClose(0, "1900"));
        Period periodD = new Period(new OpenClose(0, "0000"), new OpenClose(0, "1100"));

        // Before sort
        List<Period> unsortedPeriods = Arrays.asList(periodA, periodB, periodC, periodD);
        List<Period> periods = new ArrayList<>(unsortedPeriods);
        assertEquals(unsortedPeriods, periods);

        // Ascending sort
        restaurantRepository.sortByAscendingOpeningTime(periods);

        // After sort
        List<Period> sortedPeriods = Arrays.asList(periodB, periodD, periodA, periodC);
        assertEquals(sortedPeriods, periods);
    }

    @Test
    public void sortByDescendingOpeningTimeWithSuccess() {
        Period periodA = new Period(null, new OpenClose(0, "1600"));
        Period periodB = new Period(new OpenClose(0, "0000"), new OpenClose(0, "0600"));
        Period periodC = new Period(new OpenClose(1, "0000"), new OpenClose(0, "1900"));
        Period periodD = new Period(new OpenClose(0, "0000"), new OpenClose(0, "1100"));

        // Before sort
        List<Period> unsortedPeriods = Arrays.asList(periodA, periodB, periodC, periodD);
        List<Period> periods = new ArrayList<>(unsortedPeriods);
        assertEquals(unsortedPeriods, periods);

        // Descending sort
        restaurantRepository.sortByDescendingOpeningTime(periods);

        // After sort
        List<Period> sortedPeriods = Arrays.asList(periodC, periodA, periodD, periodB);
        assertEquals(sortedPeriods, periods);
    }

    @Test
    public void getDefaultRadiusWithSuccess() {
        assertEquals(RestaurantRepository.DEFAULT_RADIUS, restaurantRepository.getDefaultRadius());
    }

    @Test
    public void setAndGetRestaurantWithSuccess() {
        assertNull(restaurantRepository.getRestaurant());

        restaurantRepository.setRestaurant(restaurantWithDistance1);
        assertEquals(restaurantWithDistance1, restaurantRepository.getRestaurant());
    }

    @Test
    public void setAndGetRestaurantSelectionWithSuccess() {
        assertFalse(restaurantRepository.isRestaurantSelected());

        restaurantRepository.setRestaurantSelected(true);
        assertTrue(restaurantRepository.isRestaurantSelected());

        restaurantRepository.setRestaurantSelected(false);
        assertFalse(restaurantRepository.isRestaurantSelected());
    }

    @Test
    public void getRestaurantsWithSuccess() {
        assertNotNull(restaurantRepository.getRestaurants());
        assertTrue(restaurantRepository.getRestaurants().isEmpty());
    }

    @Test
    public void setAndGetRestaurantsToDisplayWithSuccess() {
        assertNotNull(restaurantRepository.getRestaurantsToDisplay());
        assertTrue(restaurantRepository.getRestaurantsToDisplay().isEmpty());

        restaurantRepository.setRestaurantsToDisplay(fakeRestaurantsWithDistance);
        assertEquals(fakeRestaurantsWithDistance, restaurantRepository.getRestaurantsToDisplay());
    }
    
}
