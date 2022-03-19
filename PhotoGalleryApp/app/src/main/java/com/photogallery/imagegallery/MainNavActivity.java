package com.photogallery.imagegallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.photogallery.imagegallery.fragments_nav.AboutUsFragment;
import com.photogallery.imagegallery.fragments_nav.GalleryFragment;
import com.photogallery.imagegallery.fragments_nav.SettingFragment;
import com.google.android.material.navigation.NavigationView;
import com.photogallery.imagegallery.theam.Constant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainNavActivity extends AppCompatActivity {

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("PhotoGallery");

        toolbar.setTitleTextColor(Color.WHITE);
        SharedPreferences sharedPreferences = getSharedPreferences("MyGAllery", Context.MODE_PRIVATE);
        int color = sharedPreferences.getInt("KEY_COLOR", 0);
        if (sharedPreferences.contains("KEY_COLOR")){
            toolbar.setBackgroundColor(color);

        }else {

            toolbar.setBackgroundColor(Constant.color);

        }


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_open_drawer_description);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        View headerView = navigationView.getHeaderView(0);
        LinearLayout layout = headerView.findViewById(R.id.lin_header);
        layout.setBackgroundColor(Constant.color);

        GalleryFragment fragment = new  GalleryFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment);
        fragmentTransaction.commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;
               if (item.getItemId() == R.id.nav_gallery){

                   fragment = new  GalleryFragment();

                   FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                   fragmentTransaction.replace(R.id.frame,fragment);
                   fragmentTransaction.commit();

               }else if (item.getItemId() == R.id.nav_google){

                   Uri uri = Uri.parse("https://drive.google.com/");

                   // Uri uri = Uri.parse("https://photos.google.com/");
                   Intent i = new Intent(Intent.ACTION_VIEW,uri);
                   startActivity(i);



               }else if (item.getItemId() == R.id.nav_about){

                   fragment = new AboutUsFragment();

                   FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                   fragmentTransaction.replace(R.id.frame,fragment);
                   fragmentTransaction.commit();


               }else if (item.getItemId() == R.id.nav_settings){

                   fragment = new SettingFragment();

                   FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                   fragmentTransaction.replace(R.id.frame,fragment);
                   fragmentTransaction.commit();
               }

            drawerLayout.closeDrawer(GravityCompat.START);



                return true;
            }
        });
    }

}