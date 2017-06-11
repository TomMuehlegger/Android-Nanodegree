package at.tm.android.fitacity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import at.tm.android.fitacity.data.Exercise;
import at.tm.android.fitacity.data.FitacityContract;
import at.tm.android.fitacity.utilities.AnalyticsApplication;

/**
 * Exercise detail activity providing details about an exercise
 */
public class ExerciseDetailActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String EXERCISE_EXTRA_NAME = "exercise";
    private Exercise exercise;
    private FloatingActionButton fav_fab;
    private boolean exerciseIsFavorite;
    private Tracker mTracker;

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

        fav_fab = (FloatingActionButton) findViewById(R.id.fav_fab);

        // Obtain the shared Tracker instance for Google Analytics
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

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
            fav_fab.setContentDescription(getString(R.string.detail_activity_fab_remove_content_description));
        } else {
            this.exerciseIsFavorite = false;
            fav_fab.setImageResource(R.drawable.ic_unfavorite);
            fav_fab.setContentDescription(getString(R.string.detail_activity_fab_add_content_description));
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

                    // Send Google Analytics event to like the shown exercise
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_action))
                            .setAction(getString(R.string.analytics_action_liked) + exercise.getName())
                            .build());
                } else {
                    //Add icon
                    fav_fab.setImageResource(R.drawable.ic_unfavorite);

                    // Remove exercise from local favorites
                    resolver.delete(FitacityContract.ExerciseEntry.buildExerciseUriWithId(exercise.getId()),
                            null,
                            null);

                    // Send Google Analytics event to dislike the shown exercise
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.analytics_action))
                            .setAction(getString(R.string.analytics_action_disliked) + exercise.getName())
                            .build());
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
        if (youTubePlayer == null) {
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
        Toast.makeText(this, getString(R.string.youtube_init_failed) + youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
    }
}
