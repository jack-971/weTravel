package uk.ac.qub.jmccambridge06.wetravel.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserAccountTest {

    JSONArray friendsArray;
    String testFriendName = "testFriendName";
    int testFriendId = 100;
    int testFriendType = 0;
    UserAccount userAccount;

    @Before
    public void setUp() throws Exception {
        friendsArray = new JSONArray();
        JSONObject friend1 = new JSONObject().put("Name", testFriendName+1).put("UserID",
                testFriendId+1).put("RelationshipType", testFriendType+1).put("Username", "").put("Dob",
                10000000).put("HomeLocation", "").put("ProfilePicture", "").put("Description",
                "").put("Private", 1).put("DateJoined", 100003003);
        JSONObject friend2 = new JSONObject().put("Name", testFriendName+2).put("UserID",
                testFriendId+2).put("RelationshipType", testFriendType+2).put("Username", "").put("Dob",
                10000000).put("HomeLocation", "").put("ProfilePicture", "").put("Description",
                "").put("Private", 1).put("DateJoined", 100003003);
        JSONObject friend3 = new JSONObject().put("Name", testFriendName+3).put("UserID",
                testFriendId+3).put("RelationshipType", testFriendType+3).put("Username", "").put("Dob",
                10000000).put("HomeLocation", "").put("ProfilePicture", "").put("Description",
                "").put("Private", 1).put("DateJoined", 100003003);
        friendsArray.put(friend1).put(friend2).put(friend3);
        userAccount = new UserAccount(1);
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Tests if friend list is correctly created from JSON data with correct profile types from Relationship Types file
     * @throws JSONException
     */
    @Test
    public void setFriendsList() throws JSONException {
        userAccount.setFriendsList(friendsArray);
        assertEquals(userAccount.getFriendsList().get(0).getUserId(), testFriendId+1);
        assertEquals(userAccount.getFriendsList().get(0).getProfileType(), testFriendType+1);
        assertEquals(userAccount.getFriendsList().get(1).getUserId(), testFriendId+2);
        assertEquals(userAccount.getFriendsList().get(1).getProfileType(), testFriendType+1);
        assertEquals(userAccount.getFriendsList().get(2).getUserId(), testFriendId+3);
        assertEquals(userAccount.getFriendsList().get(2).getProfileType(), testFriendType+1);
    }

    @Test
    public void checkFriend() throws JSONException {
        userAccount.setFriendsList(friendsArray);
        int userId;
        userId = testFriendId+1;
        assertEquals(userAccount.checkFriend(userId).getName(), testFriendName+1);
        userId = testFriendId+4;
        assertNull(userAccount.checkFriend(userId));

    }


}