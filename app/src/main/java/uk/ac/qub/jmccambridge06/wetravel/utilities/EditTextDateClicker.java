package uk.ac.qub.jmccambridge06.wetravel.utilities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTextDateClicker implements View.OnClickListener {

    Context context;
    EditText editText;
    Date dateToSet;

    public EditTextDateClicker(Context context, EditText editText, Date dateToSet) {
        this.context = context;
        this.editText = editText;
        this.dateToSet = dateToSet;
    }

    @Override
    public void onClick(View v) {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                editText.setText(sdf.format(myCalendar.getTime()));
            }

        };
        if (dateToSet == null) {
            new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateToSet);
            int month = (calendar.get(Calendar.MONTH));
            int day = (calendar.get(Calendar.DAY_OF_MONTH));
            int year = (calendar.get(Calendar.YEAR));
            new DatePickerDialog(context, date, year, month, day).show();
        }

    }
}
