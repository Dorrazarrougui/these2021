package com.these.school.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.these.school.R;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerlayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.home_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_home);

        drawerlayout = findViewById(R.id.drawarlayout);
        navigationView = findViewById(R.id.navigationView);

        toggle = new ActionBarDrawerToggle(this, drawerlayout, R.string.open, R.string.close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.accueil: {
                        /*Intent intentSales = new Intent(HomeActivity.this, SalesActivity.class);
                        startActivity(intentSales);*/
                        return true;
                    }

                    case R.id.settings: {
                        /*Intent intentPeofile = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intentPeofile);*/
                        return true;
                    }
                    case R.id.logout: {
                        FirebaseAuth.getInstance().signOut();
                        Intent intentLogin = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intentLogin);
                        finish();
                        return true;
                    }
                }
                return HomeActivity.super.onContextItemSelected(item);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }
}