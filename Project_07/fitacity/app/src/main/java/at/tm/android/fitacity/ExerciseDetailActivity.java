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
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import at.tm.android.fitacity.data.Exercise;
import at.tm.android.fitacity.data.FitacityContract;

public class ExerciseDetailActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String EXERCISE_EXTRA_NAME = "exercise";
    private Exercise exercise;
    private FloatingActionButton fav_fab;
    private boolean exerciseIsFavorite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        exercise = intent.getParcelableExtra(EXERCISE_EXTRA_NAME);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(exercise.getName());
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        fav_fab = (FloatingActionButton)findViewById(R.id.fav_fab);

        final ContentResolver resolver = getContentResolver();
        String[] projectionColumns = {FitacityContract.ExerciseEntry.COLUMN_ID};

        Cursor cursor = resolver.query(FitacityContract.ExerciseEntry.buildExerciseUriWithId(exercise.getId()),
                projectionColumns,
                null,
                null,
                null);

        // The actual exercise is already a favorite
        if (cursor.moveToFirst()) {
            this.exerciseIsFavorite = true;
            fav_fab.setImageResource(R.drawable.ic_favorite);
        } else {
            this.exerciseIsFavorite = false;
            fav_fab.setImageResource(R.drawable.ic_unfavorite);
        }

        fav_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseIsFavorite = !exerciseIsFavorite;

                if (exerciseIsFavorite) {
                    //Remove icon
                    fav_fab.setImageResource(R.drawable.ic_favorite);

                    // Add exercise to local favorites
                    ContentValues exerciseValues = new ContentValues();
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_ID, exercise.getId());
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_NAME, exercise.getName());
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION, exercise.getDescription());
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT, exercise.getEquipment());
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_CATEGORY, exercise.getCategory());
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY, exercise.getDifficulty());
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL, exercise.getVideoUrl());
                    exerciseValues.put(FitacityContract.ExerciseEntry.COLUMN_IMG_URL, exercise.getImgUrl());
                    resolver.insert(FitacityContract.ExerciseEntry.CONTENT_URI, exerciseValues);
                } else {
                    //Add icon
                    fav_fab.setImageResource(R.drawable.ic_unfavorite);

                    // Remove movie from local favorites
                    resolver.delete(FitacityContract.ExerciseEntry.buildExerciseUriWithId(exercise.getId()),
                            null,
                            null);
                }
            }
        });

        YouTubePlayerView videoView = (YouTubePlayerView) findViewById(R.id.video);
        TextView description = (TextView) findViewById(R.id.description);
        TextView equipment = (TextView) findViewById(R.id.equipment);
        TextView difficulty = (TextView) findViewById(R.id.difficulty);

        videoView.initialize(exercise.getVideoUrl(), this);
        description.setText(exercise.getDescription());
        equipment.setText(exercise.getEquipment());
        difficulty.setText(Float.toString(exercise.getDifficulty()));

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(exercise.getName());

        loadBackdrop();
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(exercise.getImgUrl()).centerCrop().into(imageView);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if(youTubePlayer == null) {
            return;
        }

        // Start buffering
        if (!wasRestored) {
            youTubePlayer.cueVideo(exercise.getVideoUrl());
        }
        youTubePlayer.play();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Failed to initialize." + youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
    }
}
