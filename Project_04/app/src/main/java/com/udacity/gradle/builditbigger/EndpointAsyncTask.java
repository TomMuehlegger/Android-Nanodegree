package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import com.example.builditbigger.backend.jokesApi.JokesApi;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * EndpointsAsyncTask class to get data from the GCE jokesApi asynchronously
 */
public class EndpointAsyncTask extends AsyncTask<OnJokeTaskCompleted, Void, String> {
    private static JokesApi jokesApiService = null;
    private OnJokeTaskCompleted caller;

    @Override
    protected String doInBackground(OnJokeTaskCompleted... params) {
        if(jokesApiService == null) {  // Only do this once
            JokesApi.Builder builder = new JokesApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            jokesApiService = builder.build();
        }

        // Set the context to start the detail activity from
        if ( (params != null) || (params[0] == null) ) {
            caller = params[0];
        }

        try {
            // Get a joke from the Jokes API
            return jokesApiService.getJoke().execute().getJoke();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (caller != null) {
            caller.onTaskCompleted(result);
        }
    }
}