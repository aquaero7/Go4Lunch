package com.example.go4lunch.tobedeleted;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FirestoreUtilsTest {

    /*

    User currentUser, testCurrentUser, testUser1, testUser2;
    String uId, uName, uEmail, uUrlPicture, uSelId, uSelDate, uSelName, uSelAddress, uRadPrefs, uNotPrefs;


    @Before
    public void setUp() throws Exception {
        currentUser = new User("0", "uName0", null, null);
        FirestoreUtils.setCurrentUser(currentUser);
    }


    @Test
    public void setAndGetCurrentUserWithSuccess() {
        // Test with user short constructor //
        uId = "1";
        uName = "uName1";
        uEmail = "uEmail1";
        uUrlPicture = "uUrlPicture1";
        // Create test current user (short constructor)
        testUser1 = new User(uId, uName, uEmail, uUrlPicture);
        // Test current user setting to FirestoreUtils
        FirestoreUtils.setCurrentUser(testUser1);
        // Test current user getting from FirestoreUtils
        testCurrentUser = FirestoreUtils.getCurrentUser();
        // Verify all current user data
        assertEquals("Wrong current user id", uId, testCurrentUser.getUid());
        assertEquals("Wrong current user name", uName, testCurrentUser.getUsername());
        assertEquals("Wrong current user email", uEmail, testCurrentUser.getUserEmail());
        assertEquals("Wrong current user url picture", uUrlPicture, testCurrentUser.getUserUrlPicture());
        assertNull("Wrong current user selection id", testCurrentUser.getSelectionId());
        assertNull("Wrong current user selection date", testCurrentUser.getSelectionDate());
        assertNull("Wrong current user selection name", testCurrentUser.getSelectionName());
        assertNull("Wrong current user selection address", testCurrentUser.getSelectionAddress());
        assertNull("Wrong current user search radius prefs", testCurrentUser.getSearchRadiusPrefs());
        assertNull("Wrong current user notifications prefs", testCurrentUser.getNotificationsPrefs());

        // Test with user full constructor //
        uId = "2";
        uName = "uName2";
        uEmail = "uEmail2";
        uUrlPicture = "uUrlPicture2";
        uSelId = "uSelId2";
        uSelDate = "uSelDate2";
        uSelName = "uSelName2";
        uSelAddress = "uSelAddress2";
        uRadPrefs = "uRadPrefs2";
        uNotPrefs = "uNotPrefs2";
        // Create test current user (full constructor)
        testUser2 = new User(uId, uName, uEmail, uUrlPicture, uSelId, uSelDate, uSelName, uSelAddress, uRadPrefs, uNotPrefs);
        // Test current user setting to FirestoreUtils
        FirestoreUtils.setCurrentUser(testUser2);
        // Test current user getting from FirestoreUtils
        testCurrentUser = FirestoreUtils.getCurrentUser();
        // Verify all current user data
        assertEquals("Wrong current user id", uId, testCurrentUser.getUid());
        assertEquals("Wrong current user name", uName, testCurrentUser.getUsername());
        assertEquals("Wrong current user email", uEmail, testCurrentUser.getUserEmail());
        assertEquals("Wrong current user url picture", uUrlPicture, testCurrentUser.getUserUrlPicture());
        assertEquals("Wrong current user selection id", uSelId, testCurrentUser.getSelectionId());
        assertEquals("Wrong current user selection date", uSelDate, testCurrentUser.getSelectionDate());
        assertEquals("Wrong current user selection name", uSelName, testCurrentUser.getSelectionName());
        assertEquals("Wrong current user selection address", uSelAddress, testCurrentUser.getSelectionAddress());
        assertEquals("Wrong current user search radius prefs", uRadPrefs, testCurrentUser.getSearchRadiusPrefs());
        assertEquals("Wrong current user notifications prefs", uNotPrefs, testCurrentUser.getNotificationsPrefs());
    }

    @Test
    public void updateCurrentUserWirthSuccess() {
        String tag, uSelId, uSelDate, uSelName, uSelAddress, uRadPrefs, uNotPrefs;

        // Mocking Log class is necessary to run this test /////////////////////////////////////////
        // Set and start Mockito strictness                                                       //
        MockitoSession mockito = Mockito.mockitoSession()                                         //
                .strictness(Strictness.STRICT_STUBS)                                              //
                .startMocking();                                                                  //
        // Try method used to delimit the static Log.e mock with a scope local to the test thread //
        try (MockedStatic<Log> mockedLog = mockStatic(Log.class)) {                               //
            mockedLog.when(() -> Log.e(anyString(), anyString())).thenReturn(-1);   //////////

            // Method with 2 or 5 args and wrong tag, so nothing should be updated
            // Reference data
            tag = "XXX";
            // Test update with 2 args
            FirestoreUtils.updateCurrentUser(tag, tag);
            // Test verifications
            testCurrentUser = FirestoreUtils.getCurrentUser();
            assertNull("Wrong current user selection id", testCurrentUser.getSelectionId());
            assertNull("Wrong current user selection date", testCurrentUser.getSelectionDate());
            assertNull("Wrong current user selection name", testCurrentUser.getSelectionName());
            assertNull("Wrong current user selection address", testCurrentUser.getSelectionAddress());
            assertNull("Wrong current user search radius prefs", testCurrentUser.getSearchRadiusPrefs());
            assertNull("Wrong current user notifications prefs", testCurrentUser.getNotificationsPrefs());
            // Test update with 5 args
            FirestoreUtils.updateCurrentUser(tag, tag, tag, tag, tag);
            // Test verifications
            testCurrentUser = FirestoreUtils.getCurrentUser();
            assertNull("Wrong current user selection id", testCurrentUser.getSelectionId());
            assertNull("Wrong current user selection date", testCurrentUser.getSelectionDate());
            assertNull("Wrong current user selection name", testCurrentUser.getSelectionName());
            assertNull("Wrong current user selection address", testCurrentUser.getSelectionAddress());
            assertNull("Wrong current user search radius prefs", testCurrentUser.getSearchRadiusPrefs());
            assertNull("Wrong current user notifications prefs", testCurrentUser.getNotificationsPrefs());
        }
        mockito.finishMocking();    // Stop mocking Log class //////////////////////////////////////

        // Method with 5 args
        // Reference data
        tag = "SEL";
        uSelId = "uSelId0";
        uSelDate = "uSelDate0";
        uSelName = "uSelName0";
        uSelAddress = "uSelAddress0";
        // Test update
        FirestoreUtils.updateCurrentUser(tag, uSelId, uSelDate, uSelName, uSelAddress);
        // Test verifications
        testCurrentUser = FirestoreUtils.getCurrentUser();
        assertEquals("Wrong current user selection id", uSelId, testCurrentUser.getSelectionId());
        assertEquals("Wrong current user selection date", uSelDate, testCurrentUser.getSelectionDate());
        assertEquals("Wrong current user selection name", uSelName, testCurrentUser.getSelectionName());
        assertEquals("Wrong current user selection address", uSelAddress, testCurrentUser.getSelectionAddress());

        // Method with 2 args : Tag "RAD"
        // Reference data
        tag = "RAD";
        uRadPrefs = "uRadPrefs0";
        // Test update
        FirestoreUtils.updateCurrentUser(tag, uRadPrefs);
        // Test verifications
        testCurrentUser = FirestoreUtils.getCurrentUser();
        assertEquals("Wrong current user search radius prefs", uRadPrefs, testCurrentUser.getSearchRadiusPrefs());
        assertNull("Wrong current user search radius prefs", testCurrentUser.getNotificationsPrefs());

        // Method with 2 args : Tag "NOT"
        // Reference data
        tag = "NOT";
        uNotPrefs = "uNotPrefs0";
        // Test update
        FirestoreUtils.updateCurrentUser(tag, uNotPrefs);
        // Test verifications
        testCurrentUser = FirestoreUtils.getCurrentUser();
        assertEquals("Wrong current user search radius prefs", uRadPrefs, testCurrentUser.getSearchRadiusPrefs());
        assertEquals("Wrong current user notifications prefs", uNotPrefs, testCurrentUser.getNotificationsPrefs());
    }


*/

}
