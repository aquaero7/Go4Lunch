package com.example.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private List<Restaurant> fakeApiRestaurants, fakeRestaurants;
    private GmapsRestaurantPojo fakeNearPlaces;
    private GmapsRestaurantDetailsPojo fakePlaceDetails;
    private List<Photo> photos1, photos2, photos3, photos4;
    private OpeningHours openingHours1, openingHours2, openingHours3, openingHours4;
    private LatLng latLng1, latLng2, latLng3, latLng4, currentLatLng;
    private Geometry geometry1, geometry2, geometry3, geometry4;
    private double distance1, distance2, distance3, distance4;
    private Restaurant apiRestaurant1, apiRestaurant2, apiRestaurant3, apiRestaurant4D;
    private Restaurant restaurant1, restaurant2, restaurant3, restaurant4D;
    private List<Period> periods4;
    private List<String> weekdayText4;

    /** Needed for the use of LiveDataTestUtils
     * InstantTaskExecutorRule is a JUnit Test Rule that swaps the background executor used by the Architecture Components
     * with a different one which executes each task synchronously.
     */
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @SuppressWarnings("unchecked")
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

        apiRestaurant1 = new Restaurant("rId1", "rName1", photos1, "rAddress1", 1D,
                openingHours1, "rPhoneNumber1", "rWebsite1", geometry1);
        apiRestaurant2 = new Restaurant("rId2", "rName2", photos2, "rAddress2", 2D,
                openingHours2, "rPhoneNumber2", "rWebsite2", geometry2);
        apiRestaurant3 = new Restaurant("rId3", "rName3", photos3, "rAddress3", 3D,
                openingHours3, "rPhoneNumber3", "rWebsite3", geometry3);
        restaurant1 = new Restaurant("rId1", "rName1", photos1, "rAddress1", 1D,
                openingHours1, "rPhoneNumber1", "rWebsite1", geometry1, (long) distance1);
        restaurant2 = new Restaurant("rId2", "rName2", photos2, "rAddress2", 2D,
                openingHours2, "rPhoneNumber2", "rWebsite2", geometry2, (long) distance2);
        restaurant3 = new Restaurant("rId3", "rName3", photos3, "rAddress3", 3D,
                openingHours3, "rPhoneNumber3", "rWebsite3", geometry3, (long) distance3);

        fakeApiRestaurants = new ArrayList<>(Arrays.asList(apiRestaurant2, apiRestaurant3, apiRestaurant1));    // Unsorted
        fakeNearPlaces = new GmapsRestaurantPojo(fakeApiRestaurants, "", "", "");
        fakeRestaurants = new ArrayList<>(Arrays.asList(restaurant3, restaurant1, restaurant2));    // Sorted by distance then by name

        periods4 = Collections.singletonList(new Period(null, new OpenClose(0, "0000")));
        weekdayText4 = new ArrayList<>();
        openingHours4 = new OpeningHours(true, periods4, weekdayText4);
        photos4 = new ArrayList<>();
        latLng4 = new LatLng(48.5, 2.0);
        geometry4 = new Geometry(new Location(latLng4.latitude, latLng4.longitude));
        distance4 = SphericalUtil.computeDistanceBetween(currentLatLng, latLng4);

        apiRestaurant4D = new Restaurant("rId4", "rName4", photos4, "rAddress4", 0D,
                openingHours4, "rPhoneNumber4", "rWebsite4", geometry4);
        restaurant4D = new Restaurant("rId4", "rName4", photos4, "rAddress4", 0D,
                openingHours4, "rPhoneNumber4", "rWebsite4", geometry4, (long) distance4);

        fakePlaceDetails = new  GmapsRestaurantDetailsPojo(apiRestaurant4D, "");

        // Class under test
        restaurantRepository = RestaurantRepository.getNewInstance(apiClientMock);
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
    @SuppressWarnings("unchecked")
    public void fetchRestaurantsWithSuccess() throws InterruptedException {
        /** Also testing RestaurantRepository.getRestaurantsMutableLiveData() method */

        when(apiClientMock.getPlaces(anyString(), anyString(), anyInt(), anyString())).thenReturn(call1Mock);
        doAnswer(invocation -> {
            Callback<GmapsRestaurantPojo> callback1 = invocation.getArgument(0);
            callback1.onResponse(call1Mock, Response.success(fakeNearPlaces));
            return null;
        }).when(call1Mock).enqueue(any(Callback.class));

        restaurantRepository.fetchRestaurants(currentLatLng, "1", "key");

        verify(call1Mock, times(1)).enqueue(any());
        verify(apiClientMock, times(1)).getPlaces(
                "restaurant",currentLatLng.latitude + "," + currentLatLng.longitude,1000,"key"
        );
        verify(response1Mock,never()).isSuccessful();
        verify(nearPlacesMock, never()).getNearRestaurants();

        MutableLiveData<List<Restaurant>> restaurantsLiveData = restaurantRepository.getRestaurantsMutableLiveData();
        List<Restaurant> result = LiveDataTestUtils.getValue(restaurantsLiveData);

        assertEquals(fakeApiRestaurants.size(), fakeRestaurants.size());
        assertEquals(fakeApiRestaurants.size(), result.size());
        // Restaurants should be sorted by distance then by name
        for (int i = 0; i< result.size(); i++) {
            assertEquals(fakeRestaurants.get(i).getRid(), result.get(i).getRid());
            assertEquals(fakeRestaurants.get(i).getName(), result.get(i).getName());
            assertEquals(fakeRestaurants.get(i).getPhotos(), result.get(i).getPhotos());
            assertEquals(fakeRestaurants.get(i).getAddress(), result.get(i).getAddress());
            assertEquals(fakeRestaurants.get(i).getRating(), result.get(i).getRating(), 0);
            assertEquals(fakeRestaurants.get(i).getOpeningHours(), result.get(i).getOpeningHours());
            assertEquals(fakeRestaurants.get(i).getPhoneNumber(), result.get(i).getPhoneNumber());
            assertEquals(fakeRestaurants.get(i).getWebsite(), result.get(i).getWebsite());
            assertEquals(fakeRestaurants.get(i).getGeometry(), result.get(i).getGeometry());
            assertEquals(fakeRestaurants.get(i).getDistance(), result.get(i).getDistance());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
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

        restaurantRepository.fetchRestaurantDetails(restaurant4D,"key");

        verify(call2Mock, times(1)).enqueue(any());
        verify(apiClientMock, times(1)).getPlaceDetails(
                apiRestaurant4D.getRid(),detailFields,"key");
        verify(response2Mock,never()).isSuccessful();
        verify(placeDetailsMock, never()).getRestaurantDetails();

        MutableLiveData<Restaurant> restaurantDetailsLiveData = restaurantRepository.getRestaurantDetailsMutableLiveData();
        Restaurant result = LiveDataTestUtils.getValue(restaurantDetailsLiveData);

        assertEquals(restaurant4D.getRid(), result.getRid());
        assertEquals(restaurant4D.getName(), result.getName());
        assertEquals(restaurant4D.getPhotos(), result.getPhotos());
        assertEquals(restaurant4D.getAddress(), result.getAddress());
        assertEquals(restaurant4D.getRating(), result.getRating(), 0);
        assertEquals(restaurant4D.getOpeningHours(), result.getOpeningHours());
        assertEquals(restaurant4D.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(restaurant4D.getWebsite(), result.getWebsite());
        assertEquals(restaurant4D.getGeometry(), result.getGeometry());
        assertEquals(restaurant4D.getDistance(), result.getDistance());
    }

    @Test
    public void updateRestaurantsListWithDistancesWithSuccess() {
        double[] distances = {distance2, distance3, distance1};
        restaurantRepository.updateRestaurantsListWithDistances(fakeApiRestaurants, currentLatLng);
        for (int i = 0; i< fakeApiRestaurants.size(); i++) {
            assertEquals(distances[i], fakeApiRestaurants.get(i).getDistance(), 0);
        }
    }

    @Test
    public void calculateRestaurantDistanceWithSuccess() {
        assertEquals(distance1, restaurantRepository.calculateRestaurantDistance(apiRestaurant1, currentLatLng), 0);
        assertEquals(distance2, restaurantRepository.calculateRestaurantDistance(apiRestaurant2, currentLatLng), 0);
        assertEquals(distance3, restaurantRepository.calculateRestaurantDistance(apiRestaurant3, currentLatLng), 0);
    }

    @Test
    public void sortByDistanceAndNameWithSuccess() {
        List<Restaurant> testedList = Arrays.asList(restaurant2, restaurant3, restaurant1);

        // Before sort
        boolean sorted = true;
        for (int i = 0; i < testedList.size() - 1; i++) {
            if (testedList.get(i).getDistance() > testedList.get(i+1).getDistance()) {
                sorted = false;
                break;
            } else if (testedList.get(i).getDistance() == testedList.get(i+1).getDistance()) {
                if (testedList.get(i).getName().compareTo(testedList.get(i+1).getName()) > 0) {
                    sorted = false;
                    break;
                };
            }
        }
        assertFalse(sorted);

        // Sort
        restaurantRepository.sortByDistanceAndName(testedList);

        // After sort
        assertEquals(fakeRestaurants.size(), testedList.size());
        for (int i = 0; i < testedList.size(); i++) {
            assertEquals(fakeRestaurants.get(i), testedList.get(i));
        }
        for (int i = 0; i < testedList.size() - 1; i++) {
            assertTrue(testedList.get(i).getDistance() <= testedList.get(i+1).getDistance());
            if (testedList.get(i).getDistance() == testedList.get(i+1).getDistance()) {
                assertTrue(testedList.get(i).getName().compareTo(testedList.get(i+1).getName()) <= 0);
            }
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

        restaurantRepository.setRestaurant(restaurant1);
        assertEquals(restaurant1, restaurantRepository.getRestaurant());
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

        restaurantRepository.setRestaurantsToDisplay(fakeRestaurants);
        assertEquals(fakeRestaurants, restaurantRepository.getRestaurantsToDisplay());
    }

    @Test
    public void setAndGetRestaurantsMutableLiveDataWithSuccess() {
        List<Restaurant> result;

        result = restaurantRepository.getRestaurantsMutableLiveData().getValue();
        assertNull(result);

        restaurantRepository.setRestaurantsMutableLiveData(fakeRestaurants);

        result = restaurantRepository.getRestaurantsMutableLiveData().getValue();
        assertNotNull(result);
        assertEquals(fakeRestaurants.size(), result.size());
        assertEquals(restaurant3, result.get(0));
        assertEquals(restaurant1, result.get(1));
        assertEquals(restaurant2, result.get(2));
    }

    @Test
    public void setAndGetRestaurantDetailsMutableLiveDataWithSuccess() {
        Restaurant result;

        result = restaurantRepository.getRestaurantDetailsMutableLiveData().getValue();
        assertNull(result);

        restaurantRepository.setRestaurantDetailsMutableLiveData(restaurant4D);

        result = restaurantRepository.getRestaurantDetailsMutableLiveData().getValue();
        assertNotNull(result);
        assertEquals(restaurant4D, result);
    }

}
