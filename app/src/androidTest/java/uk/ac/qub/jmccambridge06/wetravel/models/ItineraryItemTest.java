package uk.ac.qub.jmccambridge06.wetravel.models;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

import static org.junit.Assert.*;

public class ItineraryItemTest {

    private Profile testProfile;
    private ItineraryItem item;
    private JSONObject jsonTrip =null;
    private JSONArray userJson = null;
    private JSONObject galleryJson = null;
    int testId = 100;
    String testName = "TestName";
    long testDateStart = 1000000000;
    long testDateFinish = 1000000001;
    String testDescription = "TestDescription";
    String testLocationId = "TestLocationID";
    String testLocationName = "TestLocationName";
    String testReview = "TestReview";
    String testProfileName = "TestProfileName";
    String testUrl = "testUrl";
    JSONObject user1;
    JSONObject user2;
    JSONObject user3;

    @Before
    public void setUp() throws Exception {
        // set up costructor
        testProfile = new Profile(testId, "testProfileName", 0);
        jsonTrip = new JSONObject();
        jsonTrip.put("ID", testId).put("Name", testName).put("DateStart", testDateStart).put("DateFinish", testDateFinish).put("Description",
                testDescription).put("LocationID", testLocationId).put("LocationDetail", testLocationName).put("Review", testReview);
        // set up setUserlist.
        item = new ItineraryItem(jsonTrip, testProfile);
        user1 = new JSONObject().put("UserID", testId+1).put("Name", testProfileName+1);
        user2 = new JSONObject().put("UserID", testId+2).put("Name", testProfileName+2);
        user3 = new JSONObject().put("UserID", testId+3).put("Name", testProfileName+3);
        userJson = new JSONArray();
        userJson.put(user1).put(user2).put(user3);
        // set up gallery
        JSONObject image1Json = new JSONObject().put("Url", testUrl+1);
        JSONObject image2Json =new JSONObject().put("Url", testUrl+2);
        JSONArray galleryJsonArray = new JSONArray().put(image1Json).put(image2Json);
        galleryJson = new JSONObject().put("images", galleryJsonArray);
        galleryJson.put("status", "true");
    }

    @After
    public void tearDown() throws Exception {
        item = null;
    }

    /**
     * Tests the item constructor from json data along with getter/setter methods. Also tests trip location constructor and getters.
     * Also tests DateTime conversions
     */
    @Test
    public void itemConstructorAndGetters() {
        assertEquals(item.getEntryId(), testId);
        assertEquals(item.getEntryName(), testName);
        assertEquals(item.getStartDate(), DateTime.sqlToDate(testDateStart));
        assertEquals(item.getEndDate(), DateTime.sqlToDate(testDateFinish));
        assertEquals(item.getDescription(), testDescription);
        assertEquals(item.getLocation().getId(), testLocationId);
        assertEquals(item.getLocation().getName(), testLocationName);
        assertEquals(item.getReview(), testReview);
        assertEquals(item.getProfile().getUserId(), testId);
        assertNotNull(item.getUserList()); // should be set up during construction
    }


    @Test
    public void setUserList() {
        item.setUserList(userJson);
        assertEquals(item.getUserList().get(testId+1), testProfileName+1);
        assertEquals(item.getUserList().get(testId+2), testProfileName+2);
        assertEquals(item.getUserList().get(testId+3), testProfileName+3);
    }

    /**
     * checks gallery data set
     */
    @Test
    public void setGallery() {
        item.setGallery(galleryJson);
        assertEquals(item.getGallery().get(0), testUrl+1);
        assertEquals(item.getGallery().get(1), testUrl+2);
    }
}