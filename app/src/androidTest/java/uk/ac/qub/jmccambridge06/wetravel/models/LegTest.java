package uk.ac.qub.jmccambridge06.wetravel.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class LegTest {

    private Leg leg;
    private Trip trip;
    private Activity activity1;
    private Activity activity2;
    private  Activity activity3;
    int testActivityId = 200;


    @Before
    public void setUp() throws Exception {
        int testLegId = 100;
        leg = new Leg();
        leg.setEntryId(1);
        trip = new Trip();
        Date date1 = new Date();
        date1.setTime(1000000211);
        Date date2 = new Date();
        date2.setTime(1000000212);
        Date date3 = new Date();
        date3.setTime(1000000213);
        activity1 = new Activity();
        activity1.setEntryId(testActivityId+1);
        activity1.setStartDate(date1);
        activity2 = new Activity();
        activity2.setEntryId(testActivityId+2);
        activity2.setStartDate(date1);
        activity3 = new Activity();
        activity3.setEntryId(testActivityId+3);
        activity3.setStartDate(date1);
    }

    @After
    public void tearDown() throws Exception {
        leg = null;
    }

    @Test
    public void addActivity() {
        leg.addActivity(activity1);
        leg.addActivity(activity2);
        leg.addActivity(activity3);

        assertEquals(leg.getActivities().get(testActivityId+1).getEntryId(), testActivityId+1);
        assertEquals(leg.getActivities().get(testActivityId+2).getEntryId(), testActivityId+2);
        assertEquals(leg.getActivities().get(testActivityId+3).getEntryId(), testActivityId+3);

    }
}