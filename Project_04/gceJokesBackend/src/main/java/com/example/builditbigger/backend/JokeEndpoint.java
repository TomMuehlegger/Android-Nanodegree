/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.builditbigger.backend;

import com.example.JokeProvider;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "jokesApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.builditbigger.example.com",
                ownerName = "backend.builditbigger.example.com",
                packagePath = ""
        )
)
public class JokeEndpoint {

    private JokeProvider jokeProvider;

    /**
     * A simple endpoint method that returns a joke from the java library javaJokesLib
     */
    @ApiMethod(name = "getJoke")
    public Joke getJoke() {
        // If no joke provider was set
        if (jokeProvider == null) {
            // create a new JokeProvider
            jokeProvider = new JokeProvider();
        }

        Joke response = new Joke();
        String joke = jokeProvider.getNextJoke();
        response.setJoke(joke);

        return response;
    }

}
