package com.ingenuityapps.android.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeIngredient implements Parcelable{

    private int mQuantity;
    private String mMeasure;
    private String mName;

    public RecipeIngredient(){}

    private RecipeIngredient(Parcel in)
    {
        mQuantity = in.readInt();
        mMeasure = in.readString();
        mName = in.readString();
    }


    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public void setMeasure(String mMeasure) {
        this.mMeasure = mMeasure;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(mQuantity);
        dest.writeString(mMeasure);
        dest.writeString(mName);

    }

    public static final Parcelable.Creator<RecipeIngredient> CREATOR = new Parcelable.Creator<RecipeIngredient>() {
        @Override
        public RecipeIngredient createFromParcel(Parcel parcel) {
            return new RecipeIngredient(parcel);
        }

        @Override
        public RecipeIngredient[] newArray(int i) {
            return new RecipeIngredient[i];
        }

    };
}
