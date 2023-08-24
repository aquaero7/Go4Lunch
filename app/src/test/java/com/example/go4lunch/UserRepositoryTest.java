package com.example.go4lunch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.go4lunch.model.helper.UserHelper;
import com.example.go4lunch.model.model.User;
import com.example.go4lunch.model.repository.UserRepository;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepositoryTest {
    private MockitoSession mockito;
    private UserRepository userRepository;
    private UserRepository userRepositoryAndHelper;
    private UserHelper userHelper;
    private UserHelper userHelperMock;
    private Context contextMock;

    private FirebaseFirestore firebaseFirestoreMock;
    private FirebaseAuth firebaseAuthMock;
    private AuthUI authUiMock;
    private FirebaseUser firebaseUserMock;
    private CollectionReference collectionReferenceMock;
    private DocumentReference documentReferenceMock;
    private Query queryMock;
    private Task<DocumentSnapshot> documentSnapshotTaskMock;
    private DocumentSnapshot documentSnapshotMock;
    private Task<User> userTaskMock;
    private Task<QuerySnapshot> querySnapshotTaskMock;
    private QuerySnapshot querySnapshotMock;
    private User user1, user2, user3, user4;
    private List<User> workmates;


    @Rule
    // Needed for the use of LiveDataTestUtils
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private void initializeData() {
        // Set and start Mockito strictness
        mockito = Mockito.mockitoSession()
                .strictness(Strictness.STRICT_STUBS)
                // .strictness(Strictness.LENIENT)
                .startMocking();

        contextMock = mock(Context.class);
        userHelperMock = mock(UserHelper.class);

        firebaseFirestoreMock = mock(FirebaseFirestore.class);
        firebaseAuthMock = mock(FirebaseAuth.class);
        authUiMock = mock(AuthUI.class);
        firebaseUserMock = mock(FirebaseUser.class);
        collectionReferenceMock = mock(CollectionReference.class);
        documentReferenceMock = mock(DocumentReference.class);
        documentSnapshotTaskMock = mock(Task.class);
        documentSnapshotMock = mock(DocumentSnapshot.class);
        userTaskMock = mock(Task.class);
        queryMock = mock(Query.class);
        querySnapshotTaskMock = mock(Task.class);
        querySnapshotMock = mock(QuerySnapshot.class);

        user1 = new User("uId1", "name1", "eMail1", "url1",
                null, null, null, null, null, null);
        user2 = new User("uId2", "name2", "eMail2", "url2",
                null, null, null, null, null, null);
        user3 = new User("uId3", "name3", "eMail3", "url3",
                null, null, null, null, null, null);
        user4 = new User("uId4", "name4", "eMail4", "url4",
                null, null, null, null, null, null);
        workmates = Arrays.asList(user3, user1, user4, user2);


        // Class under test
        userRepository = UserRepository.getNewInstance(userHelperMock);
        userRepositoryAndHelper = UserRepository.getNewInstance(firebaseFirestoreMock, firebaseAuthMock, authUiMock);
        userHelper = UserHelper.getNewInstance(firebaseFirestoreMock, firebaseAuthMock, authUiMock);


    }

    private void waitingToBeUsed() {


        when(userHelperMock.getFbCurrentUserUID()).thenCallRealMethod();
        when(firebaseFirestoreMock.collection("users")).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock);
        when(documentReferenceMock.get()).thenReturn(documentSnapshotTaskMock);

        when(collectionReferenceMock.whereEqualTo(anyString(), anyString())).thenReturn(queryMock);
        when(queryMock.whereEqualTo(anyString(), anyString())).thenReturn(queryMock);
        when(queryMock.get()).thenReturn(querySnapshotTaskMock);
        when(querySnapshotTaskMock.addOnSuccessListener(any(OnSuccessListener.class))).thenReturn(querySnapshotTaskMock);
        when(querySnapshotMock.getDocuments()).thenReturn(Collections.singletonList(documentSnapshotMock));


        when(collectionReferenceMock.document()).thenReturn(documentReferenceMock);
        when(documentReferenceMock.get()).thenReturn(documentSnapshotTaskMock);
        when(documentSnapshotTaskMock.addOnCompleteListener(any())).thenReturn(documentSnapshotTaskMock);

        when(documentSnapshotMock.get(anyString())).thenReturn("test");

        when(userHelperMock.getUsersCollection().get().addOnCompleteListener(any(OnCompleteListener.class))).thenReturn(documentSnapshotTaskMock);


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
    public void getInstanceWithSuccess() {
        assertNotNull(UserRepository.getInstance());
    }

    @Test
    public void getNewInstanceWithSuccess() {
        assertNotNull(userRepository);
        assertNotNull(userRepositoryAndHelper);
    }

    @Test
    public void getUsersCollectionWithSuccess() {
        // Testing Repository
        when(userHelperMock.getUsersCollection()).thenReturn(collectionReferenceMock);
        assertEquals(collectionReferenceMock, userRepository.getUsersCollection());
        verify(userHelperMock, times(1)).getUsersCollection();

        // Testing Helper
        when(firebaseFirestoreMock.collection("users")).thenReturn(collectionReferenceMock);
        assertEquals(collectionReferenceMock, userRepositoryAndHelper.getUsersCollection());
    }

    @Test
    public void getFbCurrentUserWithSuccess() {
        // Testing Repository
        when(userHelperMock.getFbCurrentUser()).thenReturn(firebaseUserMock);
        assertEquals(firebaseUserMock, userRepository.getFbCurrentUser());
        verify(userHelperMock, times(1)).getFbCurrentUser();

        // Testing Helper
        when(firebaseAuthMock.getCurrentUser()).thenReturn(firebaseUserMock);
        assertEquals(firebaseUserMock, userRepositoryAndHelper.getFbCurrentUser());
    }

    @Test
    public void getFbCurrentUserIdWithSuccess() {
        // Testing Repository
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        assertEquals("uId", userRepository.getFbCurrentUserId());
        verify(userHelperMock, times(1)).getFbCurrentUserUID();

        // Testing Helper
        when(firebaseAuthMock.getCurrentUser()).thenReturn(firebaseUserMock);
        when(firebaseUserMock.getUid()).thenReturn("uId");
        assertEquals("uId", userRepositoryAndHelper.getFbCurrentUserId());
    }

    @Test
    public void getFbCurrentUserLoggingStatusWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertFalse(userRepository.isFbCurrentUserLogged());
        // User is logged
        when(userHelperMock.getFbCurrentUser()).thenReturn(firebaseUserMock);
        assertTrue(userRepository.isFbCurrentUserLogged());
    }

    @Test
    public void getUsersListWithSuccess() {
        userRepository.getUsersList(task -> {
            // Testing Repository
            verify(userHelperMock, times(1)).getUsersList(any(OnCompleteListener.class));
            // Testing Helper
            verify(userHelperMock, times(1)).getUsersCollection().get().addOnCompleteListener(any(OnCompleteListener.class));
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getCurrentUserDataWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertNull(userRepository.getCurrentUserData());
        verify(userHelperMock, never()).getCurrentUserData("uId");
        // User is logged
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        when(userHelperMock.getCurrentUserData("uId")).thenReturn(documentSnapshotTaskMock);
        userRepository.getCurrentUserData();
        verify(userHelperMock, times(1)).getCurrentUserData("uId");

        // Testing Helper
        when(firebaseAuthMock.getCurrentUser()).thenReturn(firebaseUserMock);
        when(firebaseUserMock.getUid()).thenReturn("uId");
        when(firebaseFirestoreMock.collection("users")).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document("uId")).thenReturn(documentReferenceMock);
        when(documentReferenceMock.get()).thenReturn(documentSnapshotTaskMock);
        assertEquals(documentSnapshotTaskMock, userHelper.getCurrentUserData("uId"));

        when(documentSnapshotTaskMock.continueWith(any(Continuation.class))).thenReturn(userTaskMock);
        assertEquals(userTaskMock, userRepositoryAndHelper.getCurrentUserData());
    }

    @Test
    public void createUserWithSuccess() {
        // Testing Repository
        when(userHelperMock.getFbCurrentUser()).thenReturn(firebaseUserMock);
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        when(userHelperMock.getCurrentUserData(anyString())).thenReturn(documentSnapshotTaskMock);
        userRepository.createUser();
        verify(userHelperMock, times(1)).getCurrentUserData("uId");

        // Impossible to go further with the repository test. More can be tested with VM test.
    }

    @Test
    public void updateSelectionIdWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertNull(userRepository.updateSelectionId("rId"));
        verify(userHelperMock, never()).updateSelectionId("uId", "rId");
        // User is logged
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        userRepository.updateSelectionId("rId");
        verify(userHelperMock, times(1)).updateSelectionId("uId","rId");

        // Testing Helper
        when(firebaseFirestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock);
        userHelper.updateSelectionId("uId","rId");
        verify(documentReferenceMock, times(1)).update("selectionId", "rId");
    }

    @Test
    public void updateSelectionDateWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertNull(userRepository.updateSelectionDate("rDate"));
        verify(userHelperMock, never()).updateSelectionDate("uId", "rDate");
        // User is logged
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        userRepository.updateSelectionDate("rDate");
        verify(userHelperMock, times(1)).updateSelectionDate("uId","rDate");

        // Testing Helper
        when(firebaseFirestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock);
        userHelper.updateSelectionDate("uId","rDate");
        verify(documentReferenceMock, times(1)).update("selectionDate", "rDate");
    }

    @Test
    public void updateSelectionNameWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertNull(userRepository.updateSelectionName("rName"));
        verify(userHelperMock, never()).updateSelectionName("uId", "rName");
        // User is logged
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        userRepository.updateSelectionName("rName");
        verify(userHelperMock, times(1)).updateSelectionName("uId","rName");

        // Testing Helper
        when(firebaseFirestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock);
        userHelper.updateSelectionName("uId","rName");
        verify(documentReferenceMock, times(1)).update("selectionName", "rName");
    }

    @Test
    public void updateSelectionAddressWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertNull(userRepository.updateSelectionAddress("rAddress"));
        verify(userHelperMock, never()).updateSelectionAddress("uId", "rAddress");
        // User is logged
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        userRepository.updateSelectionAddress("rAddress");
        verify(userHelperMock, times(1)).updateSelectionAddress("uId","rAddress");

        // Testing Helper
        when(firebaseFirestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock);
        userHelper.updateSelectionAddress("uId","rAddress");
        verify(documentReferenceMock, times(1)).update("selectionAddress", "rAddress");
    }

    @Test
    public void updateSearchRadiusPrefsWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertNull(userRepository.updateSearchRadiusPrefs("radius"));
        verify(userHelperMock, never()).updateSearchRadiusPrefs("uId", "radius");
        // User is logged
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        userRepository.updateSearchRadiusPrefs("radius");
        verify(userHelperMock, times(1)).updateSearchRadiusPrefs("uId","radius");

        // Testing Helper
        when(firebaseFirestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock);
        userHelper.updateSearchRadiusPrefs("uId","radius");
        verify(documentReferenceMock, times(1)).update("searchRadiusPrefs", "radius");
    }

    @Test
    public void updateNotificationsPrefsWithSuccess() {
        // Testing Repository

        // User isn't logged
        assertNull(userRepository.updateNotificationsPrefs("notificationsPrefs"));
        verify(userHelperMock, never()).updateNotificationsPrefs("uId", "notificationsPrefs");
        // User is logged
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId");
        userRepository.updateNotificationsPrefs("notificationsPrefs");
        verify(userHelperMock, times(1)).updateNotificationsPrefs("uId","notificationsPrefs");

        // Testing Helper
        when(firebaseFirestoreMock.collection(anyString())).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document(anyString())).thenReturn(documentReferenceMock);
        userHelper.updateNotificationsPrefs("uId","notificationsPrefs");
        verify(documentReferenceMock, times(1)).update("notificationsPrefs", "notificationsPrefs");
    }

    @Test
    public void signOutUsingAuthUIWithSuccess() {
        // Testing Repository
        userRepository.signOut(contextMock);
        verify(userHelperMock, times(1)).signOut(contextMock);

        // Testing Helper
        userRepositoryAndHelper.signOut(contextMock);
        verify(authUiMock, times(1)).signOut(contextMock);
    }

    @Test
    public void signOutUsingFirebaseAuthWithSuccess() {
        // Testing Repository
        userRepository.signOut();
        verify(userHelperMock, times(1)).signOut();

        // Testing Helper
        userRepositoryAndHelper.signOut();
        verify(firebaseAuthMock, times(1)).signOut();
    }

    @Test
    public void deleteUserWithSuccess() {
        // Testing Repository
        userRepository.deleteUser("uId");
        verify(userHelperMock, times(1)).deleteUser("uId");

        // Testing Helper
        when(firebaseFirestoreMock.collection("users")).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.document("uId")).thenReturn(documentReferenceMock);
        userRepositoryAndHelper.deleteUser("uId");
        verify(documentReferenceMock, times(1)).delete();
    }

    @Test
    public void deleteFbUserUsingAuthUIWithSuccess() {
        // Testing Repository
        userRepository.deleteFbUser(contextMock);
        verify(userHelperMock, times(1)).deleteFbUser(contextMock);

        // Testing Helper
        userRepositoryAndHelper.deleteFbUser(contextMock);
        verify(authUiMock, times(1)).delete(contextMock);
    }

    @Test
    public void deleteFbUserUsingFirebaseAuthWithSuccess() {
        // Testing Repository
        userRepository.deleteFbUser();
        verify(userHelperMock, times(1)).deleteFbUser();

        // Testing Helper
        when(firebaseAuthMock.getCurrentUser()).thenReturn(firebaseUserMock);
        userRepositoryAndHelper.deleteFbUser();
        verify(firebaseUserMock, times(1)).delete();
    }

    @Test
    public void fetchWorkmatesWithSuccess() {
        // Testing Repository
        doNothing().when(userHelperMock).getUsersList(any(OnCompleteListener.class));
        userRepository.fetchWorkmates();
        verify(userHelperMock, times(1)).getUsersList(any(OnCompleteListener.class));

        // Testing Helper
        when(firebaseFirestoreMock.collection("users")).thenReturn(collectionReferenceMock);
        when(collectionReferenceMock.get()).thenReturn(querySnapshotTaskMock);
        userRepositoryAndHelper.fetchWorkmates();
        verify(firebaseFirestoreMock, atLeastOnce()).collection("users");
        verify(collectionReferenceMock, atLeastOnce()).get();
    }

    @Test
    public void fetchCurrentUserWithSuccess() {
        // Impossible to test with the repository test. Can be tested with VM test.
    }

    @Test
    public void sortByNameWithSuccess() {
        // Testing Repository

        // Before sort
        assertEquals(4, workmates.size());
        assertEquals("name3", workmates.get(0).getUsername());
        assertEquals("name1", workmates.get(1).getUsername());
        assertEquals("name4", workmates.get(2).getUsername());
        assertEquals("name2", workmates.get(3).getUsername());

        // Sort
        userRepository.sortByName(workmates);

        // After sort
        assertEquals(4, workmates.size());
        assertEquals("name1", workmates.get(0).getUsername());
        assertEquals("name2", workmates.get(1).getUsername());
        assertEquals("name3", workmates.get(2).getUsername());
        assertEquals("name4", workmates.get(3).getUsername());
    }

    @Test
    public void setAndGetUserCreationResponseMutableLiveDataWithSuccess() {
        // Testing Repository

        userRepository.setUserCreationResponseMutableLiveData(false);
        assertEquals(Boolean.FALSE, userRepository.getUserCreationResponseMutableLiveData().getValue());

        userRepository.setUserCreationResponseMutableLiveData(true);
        assertEquals(Boolean.TRUE, userRepository.getUserCreationResponseMutableLiveData().getValue());
    }

    @Test
    public void setAndGetWorkmatesMutableLiveDataWithSuccess() {
        // Testing Repository

        userRepository.setWorkmatesMutableLiveData(new ArrayList<>());
        assertEquals(0, Objects.requireNonNull(userRepository.getWorkmatesMutableLiveData().getValue()).size());

        userRepository.setWorkmatesMutableLiveData(workmates);
        assertEquals(workmates, userRepository.getWorkmatesMutableLiveData().getValue());
    }

    @Test
    public void setAndGetCurrentUserMutableLiveDataWithSuccess() {
        // Testing Repository

        userRepository.setCurrentUserMutableLiveData(null);
        assertNull(userRepository.getCurrentUserMutableLiveData().getValue());

        userRepository.setCurrentUserMutableLiveData(user1);
        assertEquals(user1, userRepository.getCurrentUserMutableLiveData().getValue());
    }

    @Test
    public void updateWorkmatesWithSelectionWithSuccess() {
        // Testing Repository
        userRepository.setWorkmates(workmates);
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId3");

        // Before updating workmate 3 (position 1 / index 0 before any sort)
        assertNull(userRepository.getWorkmates().get(0).getSelectionId());
        assertNull(userRepository.getWorkmates().get(0).getSelectionDate());
        assertNull(userRepository.getWorkmates().get(0).getSelectionName());
        assertNull(userRepository.getWorkmates().get(0).getSelectionAddress());

        // Updating workmate 3 (position 1 / index 0 before any sort)
        userRepository.updateWorkmates("rId3", "date3", "rName3", "rAddress3");

        // After updating workmate 3 (position 3 / index 2 after sort by name)
        assertEquals("rId3", userRepository.getWorkmates().get(2).getSelectionId());
        assertEquals("date3", userRepository.getWorkmates().get(2).getSelectionDate());
        assertEquals("rName3", userRepository.getWorkmates().get(2).getSelectionName());
        assertEquals("rAddress3", userRepository.getWorkmates().get(2).getSelectionAddress());

        assertEquals(userRepository.getWorkmates(), userRepository.getWorkmatesMutableLiveData().getValue());
    }

    @Test
    public void updateWorkmatesWithSearchRadiusPrefsWithSuccess() {
        // Testing Repository
        userRepository.setWorkmates(workmates);
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId3");

        // Before updating workmate 3 (position 1 / index 0 before any sort)
        assertEquals("uId3", userRepository.getWorkmates().get(0).getUid());
        assertNull(userRepository.getWorkmates().get(0).getSearchRadiusPrefs());
        assertNull(userRepository.getWorkmates().get(0).getNotificationsPrefs());

        // Updating workmate 3 (position 1 / index 0 before any sort)
        userRepository.updateWorkmates("RAD", "radius3");

        // After updating workmate 3 (position 3 / index 2 after sort by name)
        assertEquals("uId3", userRepository.getWorkmates().get(2).getUid());
        assertEquals("radius3", userRepository.getWorkmates().get(2).getSearchRadiusPrefs());
        assertNull(userRepository.getWorkmates().get(2).getNotificationsPrefs());

        assertEquals(userRepository.getWorkmates(), userRepository.getWorkmatesMutableLiveData().getValue());
    }

    @Test
    public void updateWorkmatesWithNotificationsPrefsWithSuccess() {
        // Testing Repository
        userRepository.setWorkmates(workmates);
        when(userHelperMock.getFbCurrentUserUID()).thenReturn("uId3");

        // Before updating workmate 3 (position 1 / index 0 before any sort)
        assertEquals("uId3", userRepository.getWorkmates().get(0).getUid());
        assertNull(userRepository.getWorkmates().get(0).getSearchRadiusPrefs());
        assertNull(userRepository.getWorkmates().get(0).getNotificationsPrefs());

        // Updating workmate 3 (position 1 / index 0 before any sort)
        userRepository.updateWorkmates("NOT", "notifications3");

        // After updating workmate 3 (position 3 / index 2 after sort by name)
        assertEquals("uId3", userRepository.getWorkmates().get(2).getUid());
        assertNull(userRepository.getWorkmates().get(2).getSearchRadiusPrefs());
        assertEquals("notifications3", userRepository.getWorkmates().get(2).getNotificationsPrefs());

        assertEquals(userRepository.getWorkmates(), userRepository.getWorkmatesMutableLiveData().getValue());
    }

    @Test
    public void updateCurrentUserWithSelectionWithSuccess() {
        // Testing Repository
        userRepository.setCurrentUser(user1);

        // Before updating current user
        assertNull(userRepository.getCurrentUser().getSelectionId());
        assertNull(userRepository.getCurrentUser().getSelectionDate());
        assertNull(userRepository.getCurrentUser().getSelectionName());
        assertNull(userRepository.getCurrentUser().getSelectionAddress());

        // Updating current user
        userRepository.updateCurrentUser("rId1", "date1", "rName1", "rAddress1");

        // After updating current user
        assertEquals("rId1", userRepository.getCurrentUser().getSelectionId());
        assertEquals("date1", userRepository.getCurrentUser().getSelectionDate());
        assertEquals("rName1", userRepository.getCurrentUser().getSelectionName());
        assertEquals("rAddress1", userRepository.getCurrentUser().getSelectionAddress());

        assertEquals(userRepository.getCurrentUser(), userRepository.getCurrentUserMutableLiveData().getValue());
    }

    @Test
    public void updateCurrentUserWithSearchRadiusPrefsWithSuccess() {
        // Testing Repository
        userRepository.setCurrentUser(user1);

        // Before updating current user
        assertNull(userRepository.getCurrentUser().getSearchRadiusPrefs());
        assertNull(userRepository.getCurrentUser().getNotificationsPrefs());

        // Updating current user
        userRepository.updateCurrentUser("RAD", "radius1");

        // After updating current user
        assertEquals("radius1", userRepository.getCurrentUser().getSearchRadiusPrefs());
        assertNull(userRepository.getCurrentUser().getNotificationsPrefs());

        assertEquals(userRepository.getCurrentUser(), userRepository.getCurrentUserMutableLiveData().getValue());
    }

    @Test
    public void updateCurrentUserWithNotificationsPrefsWithSuccess() {
        // Testing Repository
        userRepository.setCurrentUser(user1);

        // Before updating current user
        assertNull(userRepository.getCurrentUser().getSearchRadiusPrefs());
        assertNull(userRepository.getCurrentUser().getNotificationsPrefs());

        // Updating current user
        userRepository.updateCurrentUser("NOT", "notifications1");

        // After updating current user
        assertNull(userRepository.getCurrentUser().getSearchRadiusPrefs());
        assertEquals("notifications1", userRepository.getCurrentUser().getNotificationsPrefs());

        assertEquals(userRepository.getCurrentUser(), userRepository.getCurrentUserMutableLiveData().getValue());
    }

    @Test
    public void setAndGetCurrentUserWithSuccess() {
        // Testing Repository

        userRepository.setCurrentUser(null);
        assertNull(userRepository.getCurrentUser());

        userRepository.setCurrentUser(user1);
        assertEquals(user1, userRepository.getCurrentUser());
    }

    @Test
    public void setAndGetWorkmatesWithSuccess() {
        // Testing Repository

        userRepository.setWorkmates(new ArrayList<>());
        assertEquals(0, userRepository.getWorkmates().size());

        userRepository.setWorkmates(workmates);
        assertEquals(workmates, userRepository.getWorkmates());
    }

    @Test
    public void setAndGetSelectorsWithSuccess() {
        // Testing Repository

        userRepository.setSelectors(new ArrayList<>());
        assertEquals(0, userRepository.getSelectors().size());

        userRepository.setSelectors(workmates);
        assertEquals(workmates, userRepository.getSelectors());
    }




}
