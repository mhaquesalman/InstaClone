package com.salman.instaclone.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.salman.instaclone.R;

import com.salman.instaclone.fragment.HomeFragment;
import com.salman.instaclone.fragment.NotificationFragment;
import com.salman.instaclone.fragment.ProfileFragment;
import com.salman.instaclone.fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");
            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                    break;

                    case R.id.nav_search:
                        fragment = new SearchFragment();
                        break;

                    case R.id.nav_add:
                        fragment = null;
                        startActivity(new Intent(MainActivity.this,PostActivity.class));
                        break;

                    case R.id.nav_like:
                        fragment = new NotificationFragment();
                        break;

                    case R.id.nav_profile:
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        fragment = new ProfileFragment();
                        break;
                }
                if (fragment != null ){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                }
                return true;
            }
        });

    }
}
