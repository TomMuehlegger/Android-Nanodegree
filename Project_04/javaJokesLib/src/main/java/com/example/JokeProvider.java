package com.example;

import java.util.ArrayList;
import java.util.List;

public class JokeProvider {
    private List<String> jokes;
    private int jokeIdx;

    public  JokeProvider() {
        jokes = new ArrayList<>();
        jokeIdx = 0;

        jokes.add("Joke 01");
        jokes.add("Joke 02");
        jokes.add("Joke 03");
        jokes.add("Joke 04");
        jokes.add("Joke 05");
        jokes.add("Joke 06");
    }

    public String getNextJoke() {
        String joke = jokes.get(jokeIdx);
        jokeIdx++;

        return joke;
    }
}