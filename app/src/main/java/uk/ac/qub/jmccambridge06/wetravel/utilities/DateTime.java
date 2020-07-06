package uk.ac.qub.jmccambridge06.wetravel.utilities;

import java.util.Calendar;
import java.util.Date;

public class DateTime extends Date {


    /**
     * Takes a string date of sql format and calculates the age based on the date.
     * @param dob
     * @return
     */
    public static int getAge(String dob) {
        Calendar birth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob = dob.substring(0, 10);

        String[] split = dob.split("-");
        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int day = Integer.parseInt(split[2]);

        birth.set(year, month - 1, day);
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);

        return ageInt;
    }



}
