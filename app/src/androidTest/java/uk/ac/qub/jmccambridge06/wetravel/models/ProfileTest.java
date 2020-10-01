package uk.ac.qub.jmccambridge06.wetravel.models;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProfileTest {

    Profile profile;
    JSONObject jsonUser;
    String name = "testName";
    int userId = 1;
    String username = "test username";
    String profilePicture = "test pic";
    String dob = "705538800000"; // 11/05/1992
    String home = "test home";
    String description = "test description";
    int privateTest = 0;
    long dateJoined = 1600624806;
    int profileType = 1;

    @Before
    public void setUp() throws Exception {
        jsonUser = new JSONObject();
        jsonUser.put("Name",name).put("Username", username).put("Dob", dob).put("ProfilePicture", profilePicture).put(
                "Description", description).put("UserID", userId).put("Private", privateTest).put("DateJoined",
                dateJoined).put("HomeLocation", home);
        profile = new Profile(jsonUser, profileType);
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Tests both constructor along with setters and date conversions
     */
    @Test
    public void testConstructors() {
        assertEquals(profile.getName(), name);
        assertEquals(profile.getDescription(), description);
        assertEquals(profile.getHomeLocation(), home);
        assertEquals(profile.getProfilePicture(), profilePicture);
        assertEquals(profile.getDateJoined(), dateJoined);
        assertEquals(profile.getProfileType(), profileType);
        assertEquals(profile.getUsername(), username);
        assertEquals(profile.isPrivateProfile(), false);
        assertEquals(profile.getUserId(), userId);
        assertEquals(profile.getName(), name);
    }

    @Test
    public void setAge() {
        assertEquals(profile.getAge(), 28);
    }
}