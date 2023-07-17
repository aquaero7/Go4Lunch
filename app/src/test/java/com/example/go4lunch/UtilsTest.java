package com.example.go4lunch;

import static org.junit.Assert.assertEquals;

import com.example.go4lunch.model.model.Restaurant;
import com.example.go4lunch.model.model.RestaurantWithDistance;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.api.model.Geometry;
import com.example.go4lunch.model.api.model.Location;
import com.example.go4lunch.model.api.model.OpenClose;
import com.example.go4lunch.model.api.model.OpeningHours;
import com.example.go4lunch.model.api.model.Period;
import com.example.go4lunch.model.repository.RestaurantRepository;
import com.example.go4lunch.utils.Utils;
import com.example.go4lunch.viewmodel.DetailRestaurantViewModel;
import com.example.go4lunch.viewmodel.MapViewViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UtilsTest {
    LatLng testLatLng1, testLatLng2, testLatLng3, testLatLng4;
    Geometry restGeometry1, restGeometry2, restGeometry3, restGeometry4;
    Restaurant testRestaurant1, testRestaurant2, testRestaurant3, testRestaurant4, testRestaurant0,
            testRestaurantPerNull, testRestaurantOpHoNull;
    List<Restaurant> testRestaurants, testRestaurants0, testRestaurantsPerNull, testRestaurantsOpHoNull;
    List<RestaurantWithDistance> testRestaurantsWithDistance, testRestaurantsWithDistance0,
            testRestaurantsWithDistancePerNull, testRestaurantsWithDistanceOpHoNull;
    String weekDay;
    String openDay, closeDay;
    String opAM, clAM, opPM, clPM, clPMJ;
    String op1, cl1, op2, cl2, op3, cl3, op4, cl4;
    Period period1, period2, period3, period4;
    List<Period> periods13, periods4, periods0;
    List<String> weekDayTexts13, weekDayTexts4, weekDayTexts0;
    OpeningHours openingHours13, openingHours4, openingHours0, openingHoursPerNull;
    LatLng refLatLng;
    String currentDate;
    String currentTime;
    long currentDayOfWeek;

    DetailRestaurantViewModel detailRestaurantViewModel;
    MapViewViewModel mapViewViewModel;
    RestaurantRepository restaurantRepository;
    Utils utils;


    private void initializeData() {
        detailRestaurantViewModel = new DetailRestaurantViewModel();
        mapViewViewModel = new MapViewViewModel();
        restaurantRepository = RestaurantRepository.getInstance();
        utils = Utils.getInstance();
        // Reference LatLng for test restaurants
        refLatLng = new LatLng(0, 0);    // Equator - Greenwich meridian
        // LatLng for test restaurants
        testLatLng1 = new LatLng(0, 180);    // Equator - Meridian 180
        testLatLng2 = new LatLng(90, 0); // North pole - Greenwich meridian
        testLatLng3 = new LatLng(0, -180); // Equator - Meridian -180
        testLatLng4 = new LatLng(-90, 0); // South pole - Greenwich meridian
        // Test restaurant geometries
        restGeometry1 = new Geometry(new Location(testLatLng1.latitude, testLatLng1.longitude));
        restGeometry2 = new Geometry(new Location(testLatLng2.latitude, testLatLng2.longitude));
        restGeometry3 = new Geometry(new Location(testLatLng3.latitude, testLatLng3.longitude));
        restGeometry4 = new Geometry(new Location(testLatLng4.latitude, testLatLng4.longitude));
        // Test restaurant openingHours
        opAM = "1100"; clAM = "1400"; opPM = "1900"; clPM = "2200"; clPMJ = "0100";
        periods13 = Arrays.asList(new Period(new OpenClose(1, clAM), new OpenClose(1, opAM)),
                new Period(new OpenClose(1, clPM), new OpenClose(1, opPM)),
                new Period(new OpenClose(2, clAM), new OpenClose(2, opAM)),
                new Period(new OpenClose(2, clPM), new OpenClose(2, opPM)),
                new Period(new OpenClose(3, clAM), new OpenClose(3, opAM)),
                new Period(new OpenClose(3, clPM), new OpenClose(3, opPM)),
                new Period(new OpenClose(4, clAM), new OpenClose(4, opAM)),
                new Period(new OpenClose(4, clPM), new OpenClose(4, opPM)),
                new Period(new OpenClose(5, clAM), new OpenClose(5, opAM)),
                new Period(new OpenClose(6, clPMJ), new OpenClose(5, opPM)),
                new Period(new OpenClose(0, "0000"), new OpenClose(6, "0000")));
        periods4 = Collections.singletonList(new Period(null, new OpenClose(0, "0000")));
        // Set a day of week (9) that can't match with any day of the week for the case when no period exists today
        periods0 = Collections.singletonList(new Period(new OpenClose(9, "0000"), new OpenClose(9, "0000")));
        weekDay = ": 11:00 AM - 2:00 PM, 7:00 - 10:00 PM";
        openDay = ": Open 24 hours";
        closeDay = ": Closed";
        weekDayTexts13 = Arrays.asList("Monday" + weekDay, "Tuesday" + weekDay, "Wednesday" + weekDay,
                "Thursday" + weekDay, "Friday" + weekDay, "Saturday" + openDay, "Sunday" + closeDay);
        weekDayTexts4 = Arrays.asList("Monday" + openDay, "Tuesday" + openDay, "Wednesday" + openDay,
                "Thursday" + openDay, "Friday" + openDay, "Saturday" + openDay, "Sunday" + openDay);
        weekDayTexts0 = Arrays.asList("Monday" + closeDay, "Tuesday" + closeDay, "Wednesday" + closeDay,
                "Thursday" + closeDay, "Friday" + closeDay, "Saturday" + closeDay, "Sunday" + closeDay);
        openingHours13 = new OpeningHours(true, periods13, weekDayTexts13);
        openingHours4 = new OpeningHours(true, periods4, weekDayTexts4);
        openingHours0 = new OpeningHours(true, periods0, weekDayTexts0);
        openingHoursPerNull = new OpeningHours(true, null, null);
        // Test restaurants
        testRestaurant1 = new Restaurant("1", "restName1", null,"restAddress1",
                1, openingHours13, null, null, restGeometry1);
        testRestaurant2 = new Restaurant("2", "restName2", null,"restAddress2",
                2, openingHours13, null, null, restGeometry2);
        testRestaurant3 = new Restaurant("3", "restName3", null,"restAddress3",
                3, openingHours13, null, null, restGeometry3);
        testRestaurant4 = new Restaurant("4", "restName4", null,"restAddress4",
                0, openingHours4, null, null, restGeometry4);
        testRestaurant0 = new Restaurant("0", "restName0", null,"restAddress0",
                1, openingHours0, null, null, restGeometry1);
        testRestaurantPerNull = new Restaurant("00", "restName00", null,"restAddress00",
                1, openingHoursPerNull, null, null, restGeometry1);
        testRestaurantOpHoNull = new Restaurant("00", "restName000", null,"restAddress000",
                1, null, null, null, restGeometry1);
        // Test restaurants list
        testRestaurants = Arrays.asList(testRestaurant1, testRestaurant2, testRestaurant3, testRestaurant4);
        testRestaurants0 = Collections.singletonList(testRestaurant0);
        testRestaurantsPerNull = Collections.singletonList(testRestaurantPerNull);
        testRestaurantsOpHoNull = Collections.singletonList(testRestaurantOpHoNull);
        // Test restaurants list with distance
        testRestaurantsWithDistance = restaurantRepository.updateRestaurantsListWithDistances(testRestaurants, refLatLng);
        testRestaurantsWithDistance0 = restaurantRepository.updateRestaurantsListWithDistances(testRestaurants0, refLatLng);
        testRestaurantsWithDistancePerNull = restaurantRepository.updateRestaurantsListWithDistances(testRestaurantsPerNull, refLatLng);
        testRestaurantsWithDistanceOpHoNull = restaurantRepository.updateRestaurantsListWithDistances(testRestaurantsOpHoNull, refLatLng);
        // Test periods list sort
        op1 = "1100"; cl1 = "1200"; op2 = "1101"; cl2 = "1201"; op3 = "1900"; cl3 = "2200"; op4 = "1901"; cl4 = "2201";
        period1 = new Period(new OpenClose(1, cl1), new OpenClose(1, op1));
        period2 = new Period(new OpenClose(1, cl2), new OpenClose(1, op2));
        period3 = new Period(new OpenClose(1, cl3), new OpenClose(1, op3));
        period4 = new Period(new OpenClose(1, cl4), new OpenClose(1, op4));
    }

    private void initializeCalendar() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.FRANCE);
        Date date = calendar.getTime();
        currentDate = sdf.format(date);

        String hod = (calendar.get(Calendar.HOUR_OF_DAY) > 9) ?
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) : "0" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String min = (calendar.get(Calendar.MINUTE) > 9) ?
                String.valueOf(calendar.get(Calendar.MINUTE)) : "0" + String.valueOf(calendar.get(Calendar.MINUTE));
        currentTime = hod + min;

        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }


    @Before // Before each test
    public void setup() {
        initializeData();
        initializeCalendar();
    }


    @Test
    public void calculateRestaurantDistanceWithSuccess() {
        // Reference distances
        double refDistance1 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng1);  // 20015115 m = Half equatorial perimeter of the earth
        double refDistance2 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng2);  // 10007557 m = Quarter meridian perimeter of the earth
        double refDistance3 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng3);  // 20015115 m = Half equatorial perimeter of the earth
        double refDistance4 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng4);  // 10007557 m = Quarter meridian perimeter of the earth
        // Test distances
        double testDistance1 = restaurantRepository.calculateRestaurantDistance(testRestaurant1, refLatLng);
        double testDistance2 = restaurantRepository.calculateRestaurantDistance(testRestaurant2, refLatLng);
        double testDistance3 = restaurantRepository.calculateRestaurantDistance(testRestaurant3, refLatLng);
        double testDistance4 = restaurantRepository.calculateRestaurantDistance(testRestaurant4, refLatLng);
        // Test verifications
        assertEquals("Wrong distance", refDistance1, testDistance1,1);
        assertEquals("Wrong distance", refDistance2, testDistance2,1);
        assertEquals("Wrong distance", refDistance3, testDistance3,1);
        assertEquals("Wrong distance", refDistance4, testDistance4,1);
        assertEquals("Wrong distance", testDistance1, testDistance3,1);   // Equatorial d : dLng(0, -180) equals dLng(0, 180)
        assertEquals("Wrong distance", testDistance2, testDistance4,1);   // Meridian d : dLat(90, 0) equals dLat(-90, 0)
        assertEquals("Wrong distance", refDistance1 / 2, refDistance2, 1);
        assertEquals("Wrong distance", testDistance1 / 2, testDistance2, 1);
        assertEquals("Wrong distance", refDistance3 / 2, refDistance4, 1);
        assertEquals("Wrong distance", testDistance3 / 2, testDistance4, 1);
    }

    /*
    @Test
    public void calculateBoundsWithSuccess() {
        // Reference Lat and Lng
        double refLat = refLatLng.latitude;
        double refLng = refLatLng.longitude;
        // Calculate reference radius
        double testLat = 1;
        double testLng = 1;
        int refRadius = (int) (SphericalUtil.computeDistanceBetween(new LatLng(refLat,refLng), new LatLng(testLat, testLng)) / Math.sqrt(2));
        // Test bounds
        LatLngBounds testBounds = mapViewViewModel.calculateBounds(new LatLng(refLat,refLng), refRadius);
        double swLat = testBounds.southwest.latitude;
        double swLng = testBounds.southwest.longitude;
        double neLat = testBounds.northeast.latitude;
        double neLng = testBounds.northeast.longitude;
        // Test verifications
        assertEquals("Wrong swLat", - testLat, swLat, 0.1);
        assertEquals("Wrong swLng", - testLng, swLng, 0.1);
        assertEquals("Wrong neLat", testLat, neLat, 0.1);
        assertEquals("Wrong neLng", testLng, neLng, 0.1);
    }
    */

    @Test
    public void updateRestaurantsListWithDistancesWithSuccess() {
        // Reference distances
        double refDistance1 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng1);  // 20015115 m = Half equatorial perimeter of the earth
        double refDistance2 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng2);  // 10007557 m = Quarter meridian perimeter of the earth
        double refDistance3 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng3);  // 20015115 m = Half equatorial perimeter of the earth
        double refDistance4 = SphericalUtil.computeDistanceBetween(refLatLng, testLatLng4);  // 10007557 m = Quarter meridian perimeter of the earth
        // Test update of restaurants list with distance
        List<RestaurantWithDistance> restaurantsWithDistance = restaurantRepository.updateRestaurantsListWithDistances(testRestaurants, refLatLng);
        // Test verifications
        assertEquals("Wrong distance1", refDistance1, restaurantsWithDistance.get(0).getDistance(), 1);
        assertEquals("Wrong distance2", refDistance2, restaurantsWithDistance.get(1).getDistance(), 1);
        assertEquals("Wrong distance3", refDistance3, restaurantsWithDistance.get(2).getDistance(), 1);
        assertEquals("Wrong distance4", refDistance4, restaurantsWithDistance.get(3).getDistance(), 1);
    }

    @Test
    public void getOpeningInformationWithSuccess() {
        // Reference information
        final String OPA = "OPA";   // Open at
        final String OPU = "OPU";   // Open until
        final String OPD = "OPD";   // Open H24 today
        final String OPW = "OP*";   // Open 24/7
        final String CLO = "CLO";   // Closed
        final String UNK = "???";   // Unknown opening hours
        long currentDayOfWeek = utils.getCurrentDayOfWeek();
        String currentTime = utils.getCurrentTime();

        /** Test openingInformation (if at least one period exists today) */
        for (RestaurantWithDistance restaurantWithDistance : testRestaurantsWithDistance) {
            String openingInformation = detailRestaurantViewModel.getOpeningInformation(restaurantWithDistance);
            String rId = restaurantWithDistance.getRid();
            // Test verifications
            switch (rId) {
                case "1" :
                case "2" :
                case "3" :
                    switch ((int) currentDayOfWeek) {
                        case 1 :
                        case 2 :
                        case 3 :
                        case 4 :
                            if (currentTime.compareTo(opAM) >=0 && currentTime.compareTo(clAM) <0) {
                                assertEquals("Wrong information", OPU + clAM, openingInformation);
                            } else if (currentTime.compareTo(opPM) >=0 && currentTime.compareTo(clPM) <0) {
                                assertEquals("Wrong information", OPU + clPM, openingInformation);
                            } else if (currentTime.compareTo(opAM) <0) {
                                assertEquals("Wrong information", OPA + opAM, openingInformation);
                            } else if (currentTime.compareTo(clAM) >= 0 && currentTime.compareTo(opPM) < 0) {
                                assertEquals("Wrong information", OPA + opPM, openingInformation);
                            } else assertEquals("Wrong information", CLO, openingInformation);
                            break;
                        case 5 :
                            if (currentTime.compareTo(opAM) >=0 && currentTime.compareTo(clAM) <0) {
                                assertEquals("Wrong information", OPU + clAM, openingInformation);
                            } else if (currentTime.compareTo(opPM) >=0 && currentTime.compareTo(clPM) <0) {
                                assertEquals("Wrong information", OPU + clPMJ, openingInformation);
                            } else if (currentTime.compareTo(opAM) <0) {
                                assertEquals("Wrong information", OPA + opAM, openingInformation);
                            } else if (currentTime.compareTo(clAM) >= 0 && currentTime.compareTo(opPM) < 0) {
                                assertEquals("Wrong information", OPA + opPM, openingInformation);
                            } else assertEquals("Wrong information", CLO, openingInformation);
                            break;
                        case 6 :
                            assertEquals("Wrong information", OPD, openingInformation);
                            break;
                        case 0 :
                            assertEquals("Wrong information", CLO, openingInformation);
                            break;
                    }
                    break;
                case "4" :
                    assertEquals("Wrong information", OPW, openingInformation);
                    break;
            }
        }

        /** Test opening information (if no period exists today) */
        String openingInformation0 = detailRestaurantViewModel.getOpeningInformation(testRestaurantsWithDistance0.get(0));
        // Test verification
        assertEquals("Wrong information 0", CLO, openingInformation0);

        /** Test opening information (if periods list is null) */
        String openingInformationPerNull = detailRestaurantViewModel.getOpeningInformation(testRestaurantsWithDistancePerNull.get(0));
        // Test verification
        assertEquals("Wrong information 00", UNK, openingInformationPerNull);


        /** Test opening information (openingInformation object is null) */
        String openingInformationOpHoNull = detailRestaurantViewModel.getOpeningInformation(testRestaurantsWithDistanceOpHoNull.get(0));
        // Test verification
        assertEquals("Wrong information 000", UNK, openingInformationOpHoNull);
    }

    @Test
    public void sortByDistanceAndNameWithSuccess() {
        List<RestaurantWithDistance> testRestaurants = new ArrayList<>();
        // Create reference list in restaurant names order D-C-B-A with distances 300-100-300-200 (m)
        for (RestaurantWithDistance restaurant : testRestaurantsWithDistance) {
            String rName = restaurant.getName();
            switch (rName) {
                case "restName1" :
                    restaurant.setName("restD");
                    restaurant.setDistance(300);
                    break;
                case "restName2" :
                    restaurant.setName("restC");
                    restaurant.setDistance(100);
                    break;
                case "restName3" :
                    restaurant.setName("restB");
                    restaurant.setDistance(300);
                    break;
                case "restName4" :
                    restaurant.setName("restA");
                    restaurant.setDistance(200);
                    break;
            }
            testRestaurants.add(restaurant);
        }
        // Verify initial list order : Restaurant names D-C-B-A with distances 300-100-300-200 (m)
        assertEquals("Wrong order", 300, testRestaurants.get(0).getDistance());
        assertEquals("Wrong order", "restD", testRestaurants.get(0).getName());
        assertEquals("Wrong order", 100, testRestaurants.get(1).getDistance());
        assertEquals("Wrong order", "restC", testRestaurants.get(1).getName());
        assertEquals("Wrong order", 300, testRestaurants.get(2).getDistance());
        assertEquals("Wrong order", "restB", testRestaurants.get(2).getName());
        assertEquals("Wrong order", 200, testRestaurants.get(3).getDistance());
        assertEquals("Wrong order", "restA", testRestaurants.get(3).getName());
        // Test sort
        restaurantRepository.sortByDistanceAndName(testRestaurants);
        // Test verifications
        // New list order expected : Restaurant names C-A-B-D with distances 100-200-300-300 (m)
        assertEquals("Wrong order", 100, testRestaurants.get(0).getDistance());
        assertEquals("Wrong order", "restC", testRestaurants.get(0).getName());
        assertEquals("Wrong order", 200, testRestaurants.get(1).getDistance());
        assertEquals("Wrong order", "restA", testRestaurants.get(1).getName());
        assertEquals("Wrong order", 300, testRestaurants.get(2).getDistance());
        assertEquals("Wrong order", "restB", testRestaurants.get(2).getName());
        assertEquals("Wrong order", 300, testRestaurants.get(3).getDistance());
        assertEquals("Wrong order", "restD", testRestaurants.get(3).getName());
    }

    @Test
    public void sortByNameWithSuccess() {
        // Create reference users list in user name order B-C-A
        User testUser1 = new User("1", "userA", null, null);
        User testUser2 = new User("2", "userB", null, null);
        User testUser3 = new User("3", "userC", null, null);
        List<User> testUsers = Arrays.asList(testUser2, testUser3, testUser1);
        // Verify initial list order : User names B-C-A
        assertEquals("Wrong order", "userB", testUsers.get(0).getUsername());
        assertEquals("Wrong order", "userC", testUsers.get(1).getUsername());
        assertEquals("Wrong order", "userA", testUsers.get(2).getUsername());
        // Test sort
        detailRestaurantViewModel.sortByName(testUsers);
        // Test verifications
        // New list order expected : User names A-B-C
        assertEquals("Wrong order", "userA", testUsers.get(0).getUsername());
        assertEquals("Wrong order", "userB", testUsers.get(1).getUsername());
        assertEquals("Wrong order", "userC", testUsers.get(2).getUsername());
    }

    @Test
    public void sortByAscendingOpeningTimeWithSuccess() {
        // Create reference periods list in opening time order 3-1-4-2
        List<Period> testPeriods = Arrays.asList(period3, period1, period4, period2);
        // Verify initial list order : Period opening time 3-1-4-2
        assertEquals("Wrong order", op3, testPeriods.get(0).getOpen().getTime());
        assertEquals("Wrong order", op1, testPeriods.get(1).getOpen().getTime());
        assertEquals("Wrong order", op4, testPeriods.get(2).getOpen().getTime());
        assertEquals("Wrong order", op2, testPeriods.get(3).getOpen().getTime());
        // Test ascending sort
        restaurantRepository.sortByAscendingOpeningTime(testPeriods);
        // Test verifications
        // New list order expected : Period opening time 1-2-3-4
        assertEquals("Wrong order", op1, testPeriods.get(0).getOpen().getTime());
        assertEquals("Wrong order", op2, testPeriods.get(1).getOpen().getTime());
        assertEquals("Wrong order", op3, testPeriods.get(2).getOpen().getTime());
        assertEquals("Wrong order", op4, testPeriods.get(3).getOpen().getTime());
    }

    @Test
    public void sortByDescendingOpeningTimeWithSuccess() {
        // Create reference periods list in opening time order 3-1-4-2
        List<Period> testPeriods = Arrays.asList(period3, period1, period4, period2);
        // Verify initial list order : Period opening time 3-1-4-2
        assertEquals("Wrong order", op3, testPeriods.get(0).getOpen().getTime());
        assertEquals("Wrong order", op1, testPeriods.get(1).getOpen().getTime());
        assertEquals("Wrong order", op4, testPeriods.get(2).getOpen().getTime());
        assertEquals("Wrong order", op2, testPeriods.get(3).getOpen().getTime());
        // Test ascending sort
        restaurantRepository.sortByDescendingOpeningTime(testPeriods);
        // Test verifications
        // New list order expected : Period opening time 1-2-3-4
        assertEquals("Wrong order", op4, testPeriods.get(0).getOpen().getTime());
        assertEquals("Wrong order", op3, testPeriods.get(1).getOpen().getTime());
        assertEquals("Wrong order", op2, testPeriods.get(2).getOpen().getTime());
        assertEquals("Wrong order", op1, testPeriods.get(3).getOpen().getTime());
    }

    /******************
     * Calendar tests *
     ******************/

    @Test
    public void getCurrentDayOfWeekWithSuccess() {
        assertEquals("Wrong day of week", currentDayOfWeek, utils.getCurrentDayOfWeek());
    }

    @Test
    public void getCurrentTimeWithSuccess() {
        assertEquals("Wrong time", currentTime, utils.getCurrentTime());
    }

    @Test
    public void getCurrentDateWithSuccess() {
        assertEquals("Wrong date", currentDate, utils.getCurrentDate());
    }

}
