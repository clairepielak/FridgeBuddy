package com.example.fridgebuddy;

import android.os.Bundle;
import android.widget.Button;

import com.example.fridgebuddy.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fridgebuddy.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private Button scanMove;
    private ActivityMainBinding binding;

    private Util util;
    private ItemDatabase itemDB;
    private CatalogItemDatabase catalogDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize util method and database
        util = new Util();
        itemDB = ItemDatabase.getDatabase(getApplicationContext());
        catalogDB = CatalogItemDatabase.getDatabase(getApplicationContext());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_ShoppingList)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        scanMove=findViewById(R.id.scannow);
        scanMove.setOnClickListener(v -> util.Scan(MainActivity.this, itemDB, catalogDB));
    }
}