package com.vaibhav.affordability;

import android.app.*;
import android.os.*;
import android.provider.Settings;
import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    EditText balance, reserve, upcoming;
    TextView result;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(40,40,40,40);

        TextView title = new TextView(this); title.setText("Affordability MVP"); title.setTextSize(28); title.setPadding(0,0,0,30);
        l.addView(title);
        balance = field("Current bank balance", "74000"); l.addView(balance);
        reserve = field("Minimum reserve", "20000"); l.addView(reserve);
        upcoming = field("Upcoming expenses", "25000"); l.addView(upcoming);

        Button calc = new Button(this); calc.setText("Calculate safe-to-spend");
        calc.setOnClickListener(v -> calculate()); l.addView(calc);

        result = new TextView(this); result.setTextSize(20); result.setPadding(0,30,0,30); l.addView(result);

        Button access = new Button(this); access.setText("Enable checkout detection");
        access.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        l.addView(access);

        TextView note = new TextView(this);
        note.setText("Prototype: detects visible checkout text in selected apps. It does not process or control payments.");
        l.addView(note);
        setContentView(l);
        calculate();
    }
    EditText field(String hint, String value) {
        EditText e = new EditText(this); e.setHint(hint); e.setText(value); e.setInputType(2); return e;
    }
    void calculate() {
        try {
            double b=Double.parseDouble(balance.getText().toString());
            double r=Double.parseDouble(reserve.getText().toString());
            double u=Double.parseDouble(upcoming.getText().toString());
            double s=b-r-u;
            result.setText(String.format("Safe to spend: ₹%,.0f", Math.max(0,s)));
        } catch(Exception e) { result.setText("Enter valid amounts."); }
    }
}
