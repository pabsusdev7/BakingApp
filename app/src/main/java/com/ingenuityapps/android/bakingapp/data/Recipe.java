package com.ingenuityapps.android.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable{

    private int mId;
    private String mName;
    private int mServings;
    private List<RecipeIngredient> mIngredients;
    private List<RecipeStep> mSteps;

    public Recipe(){}

    public Recipe (int id, String name, int servings)
    {
        setmId(id);
        setmName(name);
        setmServings(servings);
    }

    private Recipe(Parcel in)
    {
        mId = in.readInt();
        mName = in.readString();
        mServings = in.readInt();
        mIngredients = new ArrayList<RecipeIngredient>();
        in.readTypedList(mIngredients, RecipeIngredient.CREATOR);
        mSteps = new ArrayList<RecipeStep>();
        in.readTypedList(mSteps, RecipeStep.CREATOR);
    }


    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public int getmServings() {
        return mServings;
    }

    public void setmServings(int mServings) {
        this.mServings = mServings;
    }

    public List<RecipeIngredient> getIngredients() {
        return mIngredients;
    }

    public void setIngredients(List<RecipeIngredient> mIngredients) {
        this.mIngredients = mIngredients;
    }

    public List<RecipeStep> getSteps() {
        return mSteps;
    }

    public void setSteps(List<RecipeStep> mSteps) {
        this.mSteps = mSteps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeInt(mServings);
        dest.writeTypedList(mIngredients);
        dest.writeTypedList(mSteps);
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            return new Recipe(parcel);
        }

        @Override
        public Recipe[] newArray(int i) {
            return new Recipe[i];
        }

    };
}
