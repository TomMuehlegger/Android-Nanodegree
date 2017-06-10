package at.tm.android.fitacity.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Category class representing a exercise category
 */
public class Category implements Parcelable {
    private final int id;
    private final String name;
    private final String description;
    private final int mainCategory;

    public Category(int id, String name, String description, int mainCategory) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.mainCategory = mainCategory;
    }

    private Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        mainCategory = in.readInt();
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

    private int getMainCategory() {
        return mainCategory;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(mainCategory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public String toString() {
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("id", getId());
            jsonObject.put("name", getName());
            jsonObject.put("description", getDescription());
            jsonObject.put("main_category", getMainCategory());

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
