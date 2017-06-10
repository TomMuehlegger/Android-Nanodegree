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

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.tm.android.fitacity.data.Category;
import at.tm.android.fitacity.data.Exercise;
import at.tm.android.fitacity.data.ExerciseLoader;
import at.tm.android.fitacity.data.FitacityContract;
import at.tm.android.fitacity.utilities.FitacityJsonUtils;
import at.tm.android.fitacity.utilities.NetworkUtils;

/**
 * MainActivity providing a list of exercises (favorites or of a specified category)
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private DrawerLayout mDrawerLayout;
    private Map<Integer, Category> mCategoryMenuMap;
    private ExerciseRecyclerViewAdapter mExerciseRecyclerViewAdapter;
    private TextView mEmptyRecyclerView;
    private NavigationView mNavigationView;
    private int mSelectedCategoryId;

    private static final int FAVORITE_CATEGORY_ID = R.id.menu_favorites;
    private static final String EXERCISE_LIST_KEY = "exerciseList";
    private static final String CATEGORY_LIST_KEY = "categoryList";
    private static final String SELECTED_CATEGORY_ID_KEY = "selectedCategoryId";

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

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        // Setup the recycler view with it's adapter
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));

        mEmptyRecyclerView = (TextView) findViewById(R.id.tvEmptyRecylcerView);

        if ((savedInstanceState != null) &&
                (savedInstanceState.containsKey(EXERCISE_LIST_KEY)) &&
                (savedInstanceState.containsKey(SELECTED_CATEGORY_ID_KEY)) &&
                (savedInstanceState.containsKey(SELECTED_CATEGORY_ID_KEY))) {

            // Set the selected category id
            mSelectedCategoryId = savedInstanceState.getInt(SELECTED_CATEGORY_ID_KEY);

            // If there is a previous category list stored, update the menu items
            List<Category> previousCategoryList = savedInstanceState.getParcelableArrayList(CATEGORY_LIST_KEY);
            setCategoriesMenu(previousCategoryList);

            System.out.println("Saved exercises");

            // Set the stored exercise list to the recycler view adapter
            mExerciseRecyclerViewAdapter = new ExerciseRecyclerViewAdapter();

            if (mSelectedCategoryId == FAVORITE_CATEGORY_ID) {
                // Set the favorite exercises evertime the favorite category is selected
                getSupportLoaderManager().initLoader(FAVORITE_CATEGORY_ID, null, this);
            } else {
                // If there is a previous exercise list stored, update the exercise list adapter
                List<Exercise> previousExerciseList = savedInstanceState.getParcelableArrayList(EXERCISE_LIST_KEY);
                setExerciseData(previousExerciseList);
            }

        } else {
            // Set the default category id to the favorite category
            mSelectedCategoryId = FAVORITE_CATEGORY_ID;

            // Fetch the category data when no categories stored
            new FetchCategoryDataTask().execute();

            // load the favorite exercises and put it into the recycler view adapter
            mExerciseRecyclerViewAdapter = new ExerciseRecyclerViewAdapter();
            getSupportLoaderManager().initLoader(FAVORITE_CATEGORY_ID, null, this);
        }

        // Set the adapter to the recycler view
        rv.setAdapter(mExerciseRecyclerViewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSelectedCategoryId == FAVORITE_CATEGORY_ID) {
            getSupportLoaderManager().initLoader(FAVORITE_CATEGORY_ID, null, this);
        }
        else {
            new FetchExerciseDataTask().execute(mSelectedCategoryId);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Call super.onSaveInstanceState
        super.onSaveInstanceState(outState);
        // Save the exercise list in the outState Bundle
        outState.putParcelableArrayList(EXERCISE_LIST_KEY, new ArrayList<>(mExerciseRecyclerViewAdapter.getExercises()));
        // Save the category list in the outState Bundle
        outState.putParcelableArrayList(CATEGORY_LIST_KEY, new ArrayList<>(mCategoryMenuMap.values()));
        // Save the selected category id in the outState Bundle
        outState.putInt(SELECTED_CATEGORY_ID_KEY, mSelectedCategoryId);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == FAVORITE_CATEGORY_ID) {
            mSelectedCategoryId = FAVORITE_CATEGORY_ID;
            getSupportLoaderManager().initLoader(FAVORITE_CATEGORY_ID, null, this);
        } else {
            // A category selected
            Category selectedCategory = mCategoryMenuMap.get(itemId);

            System.out.println("OnNavigationItemSelected");

            if (selectedCategory != null) {
                mSelectedCategoryId = selectedCategory.getId();
                // Load category data here
                new FetchExerciseDataTask().execute(selectedCategory.getId());
            }
        }

        mDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ExerciseLoader.newAllExercisesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<Exercise> exercises = new ArrayList<>();

        // To start from the beginning every time
        cursor.moveToFirst();
        cursor.moveToPrevious();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_NAME));
            String description = cursor.getString(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION));
            int category = cursor.getInt(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_CATEGORY));
            String equipment = cursor.getString(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT));
            float difficulty = cursor.getFloat(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY));
            String videoUrl = cursor.getString(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL));
            String imgUrl = cursor.getString(cursor.getColumnIndex(FitacityContract.ExerciseEntry.COLUMN_IMG_URL));

            // Get all exercises from the cursor and put them to the exercise list
            exercises.add(new Exercise(id, name, description, category, equipment, difficulty, videoUrl, imgUrl));
        }

        if (exercises.size() > 0) {
            mEmptyRecyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            mEmptyRecyclerView.setVisibility(View.VISIBLE);
        }

        mExerciseRecyclerViewAdapter.updateExerciseData(exercises);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mExerciseRecyclerViewAdapter.updateExerciseData(new ArrayList<Exercise>());
    }

    /**
     * Method to display the category data
     *
     * @param categories - category list to display
     */
    private void setCategoriesMenu(List<Category> categories) {
        // If the provided category data is valid
        if (categories != null) {
            mCategoryMenuMap = new HashMap<>();

            // Add the category data to the main menu
            for (Category category : categories) {
                int itemId = View.generateViewId();

                mCategoryMenuMap.put(itemId, category);
                MenuItem menuItem = mNavigationView.getMenu().getItem(0).getSubMenu().add(0, itemId, 0, category.getName());

                menuItem.setCheckable(true);
                menuItem.setIcon(R.drawable.ic_dashboard);

                // If the selected category id is the actual category id, set the menu item to checked
                if (category.getId() == mSelectedCategoryId) {
                    menuItem.setChecked(true);
                }
            }
        }

        if (mSelectedCategoryId == FAVORITE_CATEGORY_ID) {
            MenuItem menuItem = mNavigationView.getMenu().getItem(1);
            menuItem.setChecked(true);
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
            // Update the exercise data
            mExerciseRecyclerViewAdapter.updateExerciseData(exercises);

            if (exercises.size() > 0) {
                mEmptyRecyclerView.setVisibility(View.INVISIBLE);
            }
            else {
                mEmptyRecyclerView.setVisibility(View.VISIBLE);
            }
        }
        else {
            mEmptyRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Class to fecth the category data
     */
    private class FetchCategoryDataTask extends AsyncTask<Void, Void, List<Category>> {

        @Override
        protected List<Category> doInBackground(Void... params) {

            // Build url to get the category data
            URL categoryDataRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonCategoryDataResponse = NetworkUtils.getResponseFromHttpUrl(categoryDataRequestUrl);

                // Parse categories from json response string
                return FitacityJsonUtils.getCategoryDataFromJson(jsonCategoryDataResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Category> categories) {
            // Set the category data to the menu
            setCategoriesMenu(categories);
        }
    }

    /**
     * Task to fetch the exercise data
     */
    private class FetchExerciseDataTask extends AsyncTask<Integer, Void, List<Exercise>> {

        @Override
        protected List<Exercise> doInBackground(Integer... params) {

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
        protected void onPostExecute(List<Exercise> exercises) {
            // Set the category data to the menu
            setExerciseData(exercises);
        }
    }
}
