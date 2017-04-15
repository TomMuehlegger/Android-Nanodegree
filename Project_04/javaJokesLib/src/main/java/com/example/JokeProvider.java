package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Class providing some jokes
 */
public class JokeProvider {
    private List<String> jokes;
    private int jokeIdx;

    /**
     * Constructor for the joke provider
     * Set up some funny jokes
     */
    public JokeProvider() {
        jokes = new ArrayList<>();
        jokeIdx = 0;

        jokes.add("Joke 01");
        jokes.add("Joke 02");
        jokes.add("Joke 03");
        jokes.add("Joke 04");
        jokes.add("Joke 05");
        jokes.add("Joke 06");
    }

    /**
     * Method to get the next joke
     *
     * @return String containing the joke
     */
    public String getNextJoke() {
        String joke = jokes.get(jokeIdx);
        jokeIdx++;

        if (jokeIdx >= jokes.size()) {
            jokeIdx = 0;
        }

        return joke;
    }

    /**
     * Get all provided jokes (for testing purposes)
     *
     * @return List of jokes
     */
    public List<String> getJokes() {
        return jokes;
    }
}