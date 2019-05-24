package com.example.foodyuser;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

public class customDatePicker  extends DatePicker {
    public customDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        Field[] fields = DatePicker.class.getDeclaredFields();
        try {
            String[] s = new String[] {"January","February","March","April","May","June","July","August","September","October","November","December"};
            for (Field field : fields) {
                field.setAccessible(true);
                if (TextUtils.equals(field.getName(), "mMonthSpinner")) {
                    NumberPicker monthPicker = (NumberPicker) field.get(this);
                    monthPicker.setMinValue(0);
                    monthPicker.setMaxValue(11);
                    monthPicker.setDisplayedValues(s);
                }else{
                    NumberPicker picker = (NumberPicker) field.get(this);
                    picker.setVisibility(View.GONE);
                }
                if (TextUtils.equals(field.getName(), "mShortMonths")) {
                    field.set(this, s);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}