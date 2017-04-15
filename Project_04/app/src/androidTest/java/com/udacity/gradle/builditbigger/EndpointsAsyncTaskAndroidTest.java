package com.udacity.gradle.builditbigger;

import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import com.example.JokeProvider;

/**
 * Class to test the EndpointsAsyncTask
 * The gceJokesBackend must be started when testing otherwise the tests will fail
 */
public class EndpointsAsyncTaskAndroidTest extends AndroidTestCase implements OnJokeTaskCompleted {

    private String joke;
    private final CountDownLatch signal = new CountDownLatch(1);

    public void testNonEmptyString() throws ExecutionException, InterruptedException {
        // Excecute the async task
        new EndpointAsyncTask().execute(this);

        // Wait for the signal to count down
        signal.await();

        assertEquals("Joke", joke);
        // Check if the joke is empty
        assertFalse(joke.isEmpty());
        // Check if the joke is valid
        assertTrue(new JokeProvider().getJokes().contains(joke));
    }

    @Override
    public void onTaskCompleted(String joke) {
        // Set the joke
        this.joke = joke;
        // Count down the latch
        signal.countDown();
    }
}