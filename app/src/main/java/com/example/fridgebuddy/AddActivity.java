package com.example.fridgebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.fridgebuddy.util.Util;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    private Util util;

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // initialize util method and database
        util = new Util();
        database = AppDatabase.getDatabase(getApplicationContext());

        // Connects the add_layout.xml to the activity
        setContentView(R.layout.add_layout);

        // Code for the day number spinner (dropdown) -SM
        Spinner daySpinner = findViewById(R.id.daySpinner);
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this, R.array.day_number,
                android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        // Code for the month spinner (dropdown) -SM
        Spinner monthSpinner = findViewById(R.id.monthSpinner);
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.month_name,
                android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        // Code for the year spinner (dropdown) -SM
        // Calculate the current year
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        // Create a list of years starting from the current year and going up infinitely
        ArrayList<String> years = new ArrayList<>();
        for (int year = currentYear; ; year++) {
            years.add(String.valueOf(year));
            //Allow the list to go up to 100 more years than the current year
            if (year == currentYear + 100) {
                break;
            }
        }
        // Convert the list of years to an array
        String[] yearArray = years.toArray(new String[0]);

        // Create the year Spinner
        Spinner yearSpinner = findViewById(R.id.yearSpinner);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearArray);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        // Enable the "back" button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        // Add the button for saving and redirecting to the MainActivity
        Button saveButton = findViewById(R.id.save);

        Button scanBarcode = findViewById(R.id.scanBarcode);
        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.Scan(AddActivity.this, database);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Will insert code for saving the values the user inputs to the db -SM
                TextInputLayout textLayout = findViewById(R.id.productNameEditText);
                EditText editText = textLayout.getEditText();
                if (editText != null) {
                    String name = editText.getText().toString();

                    // the date is for testing
                    // needs to be changed to the date given from spinners
                    util.AddItem(AddActivity.this, database, name, "11/20/2023");

                    editText.setText("");
                }


                // Redirect to InventoryFragment
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Finish the current activity (AddActivity) and return to the previous activity
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}













