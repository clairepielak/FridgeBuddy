package com.example.fridgebuddy.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.fridgebuddy.AddActivity;
import com.example.fridgebuddy.database.CatalogItemDatabase;
import com.example.fridgebuddy.database.ItemDatabase;
import com.example.fridgebuddy.MainActivity;
import com.example.fridgebuddy.R;
import com.example.fridgebuddy.util.Util;

// imports for Date -SM
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import java.util.Locale;


public class HomeFragment extends Fragment {

    private Util util;
    private ItemDatabase itemDB;
    private CatalogItemDatabase catalogDB;

    private View root;

    // Code for month suffix
    private String getDayOfMonthSuffix(int day) {
        // Check if the day falls in the range of 11 to 13 (11th, 12th, 13th)
        // Special exceptions to the rule
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st"; // If the last digit is 1, use "st"
            case 2:
                return "nd"; // If the last digit is 2, use "nd"
            case 3:
                return "rd"; // If the last digit is 3, use "rd"
            default:
                return "th"; // For all other cases, use "th"
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        util = new Util();
        itemDB = ItemDatabase.getDatabase(requireContext().getApplicationContext());
        catalogDB = CatalogItemDatabase.getDatabase(requireContext().getApplicationContext());

        // Find the TextView with the ID "dayOfWeek" in the layout
        TextView dayOfWeekTextView = root.findViewById(R.id.dayOfWeek);

        // Find the TextView with the ID "timeOfDay" in the layout
        TextView timeOfDayTextView = root.findViewById(R.id.monthAndDay);

        // Create a SimpleDateFormat to format the date to display the day of the week (EEEE)
        SimpleDateFormat sdfDayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault());

        // Get the current date and time and format it to get the day of the week
        String currentDayOfWeek = sdfDayOfWeek.format(new Date());

        // Set the text of the day of the week TextView -SM
        dayOfWeekTextView.setText(currentDayOfWeek);

        /// Create a SimpleDateFormat to format the date to display the month and day (MMMM d)
        SimpleDateFormat sdfMonthAndDay = new SimpleDateFormat("MMMM d", Locale.getDefault());

        // Get the current date and format it to extract the month and day
        String currentMonthAndDay = sdfMonthAndDay.format(new Date());

        // Get the day of the month (as an integer) to determine the suffix
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Determine the suffix based on the day of the month
        String dayOfMonthSuffix = getDayOfMonthSuffix(dayOfMonth);

        // Append the suffix to the formatted month and day
        currentMonthAndDay = currentMonthAndDay + dayOfMonthSuffix;

        // Set the text of the month and day TextView -SM
        timeOfDayTextView.setText(currentMonthAndDay);

        // Find the TextView for the dateCalendar
        TextView dateCalendarTextView = root.findViewById(R.id.dateCalendar);

        // Create a SimpleDateFormat to format the date to display the date (d)
        SimpleDateFormat sdfDateCalendar = new SimpleDateFormat("d", Locale.getDefault());

        // Get the current date and format it to extract the date number
        String currentDateNumber = sdfDateCalendar.format(new Date());

        // Set the text of the dateCalendar TextView
        dateCalendarTextView.setText(currentDateNumber);

        // Find the scan Now button by its ID in the layout
        Button scanNowButton = root.findViewById(R.id.scannow);

        // Find the Add Item button by its ID in the layout
        Button addItemButton = root.findViewById(R.id.AddItem);

        // Allows the scannow button on the home page to be used when on return
        // from a different tab
        scanNowButton.setOnClickListener(v -> {
            if (requireActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) requireActivity();
                util.scan(mainActivity, itemDB, catalogDB);
            }
        });
        // Add functionality to the add item button within the fragment
        //so on return from other fragments functionality remains -SM
        Button addButton = addItemButton.findViewById(R.id.AddItem);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivity(intent);
        });

        return root;
    }
}