package com.example.fridgebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fridgebuddy.database.CatalogItemDatabase;
import com.example.fridgebuddy.database.Item;
import com.example.fridgebuddy.database.ItemDatabase;
import com.example.fridgebuddy.util.Util;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class AddActivity extends AppCompatActivity {

    private Util util;
    private ItemDatabase itemDB;
    private CatalogItemDatabase catalogDB;

    // Function to save an item to the database in a separate thread
    private boolean saveItemToDatabase(Item item) {
        try {
            new Thread(() -> itemDB.itemDao().upsertItem(item)).start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Function to show a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Function to generate a random UPC for an item
    private String generateRandomUPC() {
        Random random = new Random();
        StringBuilder upcBuilder = new StringBuilder();

        // Generate 8 random digits
        for (int i = 0; i < 8; i++) {
            upcBuilder.append(random.nextInt(10));
        }

        return upcBuilder.toString();
    }

    // Map to store month names and their corresponding numerical values
    private Map<String, String> monthMap;

    // Function to check if a string contains a number
    private boolean containsNumber(String input) {
        for (char character : input.toCharArray()) {
            if (Character.isDigit(character)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // initialize util method and database
        util = new Util();
        itemDB = ItemDatabase.getDatabase(getApplicationContext());
        catalogDB = CatalogItemDatabase.getDatabase(getApplicationContext());


        // Retrieve month values from resources
        String[] monthValues = getResources().getStringArray(R.array.month_values);

        // Create a map of month names to numerical values
        monthMap = new HashMap<>();
        String[] monthNames = getResources().getStringArray(R.array.month_name);
        for (int i = 0; i < monthNames.length && i < monthValues.length; i++) {
            monthMap.put(monthNames[i], monthValues[i]);
        }

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

        // Add the button for scanning barcodes
        Button scanBarcode = findViewById(R.id.scanBarcode);
        scanBarcode.setOnClickListener(view -> util.scan(AddActivity.this, itemDB, catalogDB));

        TextInputLayout nameTextInputLayout = findViewById(R.id.productNameEditText);
        EditText nameEditText = nameTextInputLayout.getEditText();

        TextInputLayout upcTextInputLayout = findViewById(R.id.upcEditText);
        EditText upcEditText = upcTextInputLayout.getEditText();

        // OnClickListener for the save button -SM
        saveButton.setOnClickListener(view -> {

            // Retrieve values from Spinners and TextViews
            String selectedDay = daySpinner.getSelectedItem().toString();
            String selectedMonth = monthSpinner.getSelectedItem().toString();
            String selectedYear = yearSpinner.getSelectedItem().toString();
            String itemName = nameEditText.getText().toString();
            String upcValue = upcEditText.getText().toString();

            // Check if a valid name is inserted and does not contain a number
            if (itemName.trim().isEmpty() || containsNumber(itemName)) {
                // Display an error message and do not proceed
                showToast("Please insert a valid name");
                return;
            }

            // If UPC is not entered, generate a random one
            if (upcValue.trim().isEmpty()) {
                upcValue = generateRandomUPC();
            }

            // Convert date values to a Date object
            calendar.set(Integer.parseInt(selectedYear), Arrays.asList(getResources().getStringArray(R.array.month_name)).indexOf(selectedMonth), Integer.parseInt(selectedDay));
            Date selectedDate = calendar.getTime();

            // Create an Item object
            Item newItem = new Item(upcValue, itemName, selectedDate, "");

            // Save the item to the database
            if (saveItemToDatabase(newItem)) {
                showToast("Item saved");
            } else {
                showToast("Could not save item");
            }


            // Redirect to Home
            Intent intent = new Intent(AddActivity.this, MainActivity.class);
            startActivity(intent);
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













