package at.tm.android.fitacity.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

import at.tm.android.fitacity.data.Category;
import at.tm.android.fitacity.data.Exercise;
import at.tm.android.fitacity.data.FitacityContract;

/**
 * The FitacityJsonUtils class will provide methods to get Fitacity-Objects (categories and exercises) from JSON
 */
public final class FitacityJsonUtils {

    /**
     * Get a category list from a json string
     *
     * @param categoryDataJsonString json string representing category data
     * @return list of categories
     * @throws JSONException
     * @throws ParseException
     */
    public static ArrayList<Category> getCategoryDataFromJson(String categoryDataJsonString) throws JSONException, ParseException {

        JSONArray categoriesJSON = new JSONArray(categoryDataJsonString);

        ArrayList<Category> categories = new ArrayList<>();

        // Parse each element of the results array and add it to the movies list
        for (int i = 0; i < categoriesJSON.length(); i++) {

            JSONObject categoryJSON = categoriesJSON.getJSONObject(i);

            int id = categoryJSON.getInt(FitacityContract.CategoryEntry.COLUMN_ID);
            String name = categoryJSON.getString(FitacityContract.CategoryEntry.COLUMN_NAME);
            String description = categoryJSON.getString(FitacityContract.CategoryEntry.COLUMN_DESCRIPTION);
            int mainCategory = categoryJSON.getInt(FitacityContract.CategoryEntry.COLUMN_MAIN_CATEGORY);

            categories.add(new Category(id, name, description, mainCategory));
        }

        return categories;

    }

    /**
     * Get a exercise list from a json string
     *
     * @param exerciseDataJsonString json string representing exercise data
     * @return list of exercises
     * @throws JSONException
     * @throws ParseException
     */
    public static ArrayList<Exercise> getExerciseDataFromJson(String exerciseDataJsonString) throws JSONException, ParseException {

        JSONArray exercisesJSON = new JSONArray(exerciseDataJsonString);

        ArrayList<Exercise> exercises = new ArrayList<>();

        // Parse each element of the results array and add it to the movies list
        for (int i = 0; i < exercisesJSON.length(); i++) {

            JSONObject exerciseJSON = exercisesJSON.getJSONObject(i);

            int id = exerciseJSON.getInt(FitacityContract.ExerciseEntry.COLUMN_ID);
            String name = exerciseJSON.getString(FitacityContract.ExerciseEntry.COLUMN_NAME);
            String description = exerciseJSON.getString(FitacityContract.ExerciseEntry.COLUMN_DESCRIPTION);
            int category = exerciseJSON.getInt(FitacityContract.ExerciseEntry.COLUMN_CATEGORY);
            String equipment = exerciseJSON.getString(FitacityContract.ExerciseEntry.COLUMN_EQUIPMENT);
            int difficulty = exerciseJSON.getInt(FitacityContract.ExerciseEntry.COLUMN_DIFFICULTY);
            String videoUrl = exerciseJSON.getString(FitacityContract.ExerciseEntry.COLUMN_VIDEO_URL);
            String imgUrl = exerciseJSON.getString(FitacityContract.ExerciseEntry.COLUMN_IMG_URL);

            exercises.add(new Exercise(id, name, description, category, equipment, difficulty, videoUrl, imgUrl));
        }

        return exercises;

    }
}