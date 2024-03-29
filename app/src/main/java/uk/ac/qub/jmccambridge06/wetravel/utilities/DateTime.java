package uk.ac.qub.jmccambridge06.wetravel.utilities;

import android.app.DatePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility functions relating to date and time
 */
public class DateTime extends Date{


    /**
     * Takes a string date of sql format and calculates the age based on the date.
     * @param dob
     * @return
     */
    public static int getAge(Date dob) {
        Calendar birth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        birth.setTime(dob);
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        Integer ageInt = new Integer(age);
        return ageInt;
    }

    /**
     * Converts a time in milliseconds to a Date type
     * @param milliseconds
     * @return
     */
    public static Date sqlToDate(Long milliseconds) {
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return calendar.getTime();
    }

    /**
     * Converts a Date object to a time in milliseconds
     * @param date
     * @return
     */
    public static long dateToSQL(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    /**
     * Converts a string date into milliseconds long format.
     * @param dateString
     * @return
     * @throws ParseException
     */
    public static String dateToMilliseconds(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        Date date = null;
        try {
            date = format.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return Long.toString(calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a date datatype to a date string
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        return formatter.format(date);
    }

    /**
     * Converts a Date datatype to a time string
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    /**
     * Converts a string to a date data type
     * @param date
     * @return
     */
    public static Date formatDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Takes two date inputs and checks whether the second is on the same day or after the first.
     * @return
     */
    public static boolean checkDateBefore(String firstStringDate, String secondStringDate) {
        Date firstDate = formatDate(firstStringDate);
        Date secondDate = formatDate(secondStringDate);
        if (firstDate == null || secondDate == null) {
            return false;
        } else {
            if (secondDate.before(firstDate)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Returns today date as a string
     * @return
     */
    public static String todaysDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        String formattedDate = format.format(date);
        return formattedDate;
    }



}
