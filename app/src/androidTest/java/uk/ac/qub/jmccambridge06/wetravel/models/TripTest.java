package uk.ac.qub.jmccambridge06.wetravel.models;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TripTest {

    private Trip trip;
    String testLegName = "testLegName";
    int testLegId = 100;
    int testActivityId = 200;
    int testActivityUserId = 0;
    String testActivityName = "testActivityName";
    JSONArray legsArray;
    JSONArray activitiesArray;

    @Before
    public void setUp() throws Exception {
        trip = new Trip();
        Profile testProfile = new Profile(testActivityUserId+1, "testProfileName", 0);
        trip.setProfile(testProfile);
        trip.setStatus("test status");

        JSONObject user1 = new JSONObject().put("UserID", testActivityUserId+1).put("Name", "test1");
        JSONObject user2 = new JSONObject().put("UserID", testActivityUserId+2).put("Name", "test2");
        JSONObject user3 = new JSONObject().put("UserID", testActivityUserId+3).put("Name", "test3");
        JSONArray userJson = new JSONArray();
        userJson.put(user1).put(user2).put(user3);
        trip.setUserList(userJson);
        JSONObject leg1Json = new JSONObject().put("ID", testLegId+1).put("Name", testLegName+1).put("UserID", 1);
        JSONObject leg2Json = new JSONObject().put("ID", testLegId+2).put("Name", testLegName+2).put("UserID", 2);
        legsArray = new JSONArray().put(leg1Json).put(leg2Json);

        activitiesArray = new JSONArray();
        JSONObject activity1Json = new JSONObject().put("ActivityUserID", testActivityUserId+1).put("LegID",
                testLegId+1).put("ActivityID", testActivityId+1).put("ID", testActivityId+1).put("Notes", "");
        JSONObject activity2Json = new JSONObject().put("ActivityUserID", testActivityUserId+2).put("LegID",
                testLegId+1).put("ActivityID", testActivityId+2).put("ID", testActivityId+2).put("Notes", "");
        JSONObject activity3Json = new JSONObject().put("ActivityUserID", testActivityUserId+3).put("LegID",
                testLegId+2).put("ActivityID", testActivityId+3).put("ID", testActivityId+3).put("Notes", "");
        activitiesArray.put(activity1Json).put(activity2Json).put(activity3Json);
    }

    @After
    public void tearDown() throws Exception {
        trip = null;
    }

    /**
     * Test checks that method adds a json array of legs to a trip with correct attributes and lookup
     */
    @Test
    public void setLegs() {
        trip.setLegs(legsArray);
        // get the leg at leg id index - then check the leg object returned matches input data
        assertEquals(trip.getLegs().get(testLegId+1).getEntryId(), testLegId+1);
        assertEquals(trip.getLegs().get(testLegId+1).getEntryName(), testLegName+1);
        assertEquals(trip.getLegs().get(testLegId+2).getEntryId(), testLegId+2);
        assertEquals(trip.getLegs().get(testLegId+2).getEntryName(), testLegName+2);
    }

    /**
     * Checks method adds activities to the correct legs and with correct attributes
     */
    @Test
    public void addActivities() {
        trip.setLegs(legsArray);
        trip.addActivities(activitiesArray);
        assertEquals(trip.getLegs().get(testLegId+1).getActivities().get(testActivityId+1).getEntryId(), testActivityId+1);
        assertEquals(trip.getLegs().get(testLegId+1).getActivities().get(testActivityId+2).getEntryId(), testActivityId+2);
        assertEquals(trip.getLegs().get(testLegId+2).getActivities().get(testActivityId+3).getEntryId(), testActivityId+3);
    }

}