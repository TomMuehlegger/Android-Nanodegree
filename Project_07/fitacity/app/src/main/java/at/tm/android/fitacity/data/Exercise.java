package at.tm.android.fitacity.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Exercise class representing a movie
 */
public class Exercise implements Parcelable {
    private final int id;
    private final String name;
    private final String description;
    private final int category;
    private final String equipment;
    private final float difficulty;
    private final String videoUrl;
    private final String imgUrl;

    public Exercise(int id, String name, String description, int category, String equipment, float difficulty, String videoUrl, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.videoUrl = videoUrl;
        this.imgUrl = imgUrl;
    }

    private Exercise(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        category = in.readInt();
        equipment = in.readString();
        difficulty = in.readFloat();
        videoUrl = in.readString();
        imgUrl = in.readString();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCategory() {
        return category;
    }

    public String getEquipment() {
        return equipment;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(category);
        dest.writeString(equipment);
        dest.writeFloat(difficulty);
        dest.writeString(videoUrl);
        dest.writeString(imgUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    @Override
    public String toString() {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("id", getId());
            jsonObject.put("name", getName());
            jsonObject.put("description", getDescription());
            jsonObject.put("category", getCategory());
            jsonObject.put("equipment", getEquipment());
            jsonObject.put("difficulty", getDifficulty());
            jsonObject.put("videoUrl", getVideoUrl());
            jsonObject.put("imgUrl", getImgUrl());

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
