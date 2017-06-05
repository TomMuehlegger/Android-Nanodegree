/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.tm.android.fitacity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.tm.android.fitacity.data.Category;
import at.tm.android.fitacity.data.Exercise;
import at.tm.android.fitacity.data.FitacityContract;
import at.tm.android.fitacity.utilities.FitacityJsonUtils;
import at.tm.android.fitacity.utilities.NetworkUtils;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private Map<Integer, Category> categoryMenuMap;
    private ExerciseRecyclerViewAdapter exerciseRecyclerViewAdapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        setupRecyclerView(rv);

        new FetchCategoryDataTask().execute();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        exerciseRecyclerViewAdapter = new ExerciseRecyclerViewAdapter(getFavoriteExercises());
        recyclerView.setAdapter(exerciseRecyclerViewAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Exercise> getFavoriteExercises() {
        final ContentResolver resolver = getContentResolver();
        String[] projectionColumns = {
                FitacityContract.ExerciseEntry.COLUMN_ID,
                FitacityContract.ExerciseEntry.COLUMN_NAME,
                FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION,
                FitacityContract.ExerciseEntry.COLUMN_CATEGORY,
                FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT,
                FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY,
                FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL,
                FitacityContract.ExerciseEntry.COLUMN_IMG_URL
        };

        Cursor cursor = resolver.query(FitacityContract.ExerciseEntry.CONTENT_URI,
                projectionColumns,
                null,
                null,
                null);

        List<Exercise> exercises = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            int category = cursor.getInt(3);
            String equipment = cursor.getString(4);
            float difficulty = cursor.getFloat(5);
            String videoUrl = cursor.getString(6);
            String imgUrl = cursor.getString(7);

            exercises.add(new Exercise(id, name, description, category, equipment, difficulty, videoUrl, imgUrl));
        }

        return exercises;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_favorites) {
            setExerciseData(getFavoriteExercises());
        }
        else {
            // A category selected
            Category selectedCategory = categoryMenuMap.get(itemId);

            if (selectedCategory != null) {
                // Load category data here
                new FetchExerciseDataTask().execute(selectedCategory.getId());
            }
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    public class FetchCategoryDataTask extends AsyncTask<Void, Void, ArrayList<Category>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the loading indicator
            System.out.println("Start fetching categories from server");
        }

        @Override
        protected ArrayList<Category> doInBackground(Void... params) {

            // Build url to get the category data
            URL categoryDataRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonCategoryDataResponse = NetworkUtils.getResponseFromHttpUrl(categoryDataRequestUrl);

                System.out.println("Categories response: " + jsonCategoryDataResponse);

                // Parse categories from json response string
                return FitacityJsonUtils.getCategoryDataFromJson(jsonCategoryDataResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Category> categories) {
            // Set the category data to the menu
            setCategoriesMenu(categories);
        }
    }

    public class FetchExerciseDataTask extends AsyncTask<Integer, Void, ArrayList<Exercise>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show the loading indicator
        }

        @Override
        protected ArrayList<Exercise> doInBackground(Integer... params) {

            // Build url to get the category data
            URL exerciseDataRequestUrl = NetworkUtils.buildExerciseUrl(params[0]);

            try {
                String jsonExerciseDataResponse = NetworkUtils.getResponseFromHttpUrl(exerciseDataRequestUrl);

                // Parse exercises from json response string
                return FitacityJsonUtils.getExerciseDataFromJson(jsonExerciseDataResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Exercise> exercises) {
            // Set the category data to the menu
            setExerciseData(exercises);
        }
    }

    /**
     * Method to display the category data
     *
     * @param categories - category list to display
     */
    private void setCategoriesMenu(ArrayList<Category> categories) {
        // If the provided category data is valid
        if (categories != null) {
            categoryMenuMap = new HashMap<>();

            // Add the category data to the main menu
            for (Category category : categories) {
                int itemId = View.generateViewId();

                System.out.println("Category name: " + category.getName() + " id: " + itemId);

                categoryMenuMap.put(itemId, category);
                MenuItem menuItem = navigationView.getMenu().getItem(0).getSubMenu().add(0, itemId, 0, category.getName());

                menuItem.setCheckable(true);

                menuItem.setIcon(R.drawable.ic_dashboard);
                System.out.println(category);
            }
        }
    }

    /**
     * Method to display the exercise data
     *
     * @param exercises - exercise list to display
     */
    private void setExerciseData(List<Exercise> exercises) {
        // If the provided exercise data is valid
        if (exercises != null) {
            // Show the exercise data
            for (Exercise exercise : exercises) {
                System.out.println(exercise);
            }
            exerciseRecyclerViewAdapter.updateExerciseData(exercises);
        }
    }
}
