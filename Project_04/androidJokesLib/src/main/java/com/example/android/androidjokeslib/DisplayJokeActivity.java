package com.example.android.androidjokeslib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayJokeActivity extends AppCompatActivity {

    public static String EXTRA_JOKE = "joke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_joke);

        // Get the intent to retrieve the joke data
        Intent intent = getIntent();

        TextView jokeTextView = (TextView)findViewById(R.id.joke_text_view);

        // If the intent is not null and the joke data provided
        if ( (intent != null) && (intent.hasExtra(EXTRA_JOKE)) ) {
            // Get the joke data from the intent
            String joke = intent.getStringExtra(EXTRA_JOKE);

            // Display the joke
            jokeTextView.setText(joke);
        }
        else {
            // Otherwise display error message that no joke was provided
            jokeTextView.setText(R.string.no_joke_error_message);
        }
    }
}
