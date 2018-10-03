package com.ingenuityapps.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ingenuityapps.android.bakingapp.data.Recipe;
import com.ingenuityapps.android.bakingapp.data.RecipeIngredient;
import com.ingenuityapps.android.bakingapp.utilities.JsonUtils;
import com.ingenuityapps.android.bakingapp.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.ingenuityapps.android.bakingapp.MainActivity.APP_PREFERENCES;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_ID_PREF;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_INTENT;

public class BakingIngredientsWidgetService extends RemoteViewsService{


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingIngredientsRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class BakingIngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{

    Context mContext;
    Recipe mRecipe;
    List<RecipeIngredient> mIngredientList;

    public BakingIngredientsRemoteViewsFactory(Context applicationContext, Intent intent)
    {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

        mRecipe = new Recipe();
        mIngredientList = new ArrayList<>();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDataSetChanged() {

        mRecipe = getFavoriteRecipe(mContext);
        mIngredientList = mRecipe.getIngredients();

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mIngredientList == null) return 0;
        return mIngredientList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(mIngredientList == null || mIngredientList.size()==0) return null;

        RecipeIngredient ingredient = mIngredientList.get(position);

        String ingredientText = ingredient.getName();

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_ingredient);

        views.setTextViewText(R.id.tv_ingredient, ingredientText.substring(0,1).toUpperCase() + ingredientText.substring(1) + " (" + ingredient.getQuantity() + " " + ingredient.getMeasure() + ")");

        Bundle extras = new Bundle();
        extras.putParcelable(RECIPE_INTENT,mRecipe);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.tv_ingredient,fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Recipe getFavoriteRecipe(Context context)
    {
        URL recipesRequestURL = NetworkUtils.buildRecipesUrl();
        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        final int prefRecipeId = preferences.getInt(RECIPE_ID_PREF,0);

        try
        {
            String jsonRecipesResponse = NetworkUtils.getResponseFromHttpUrl(recipesRequestURL);

            List<Recipe> recipes = JsonUtils.parseRecipesFromJson(jsonRecipesResponse);

            Recipe favoriteRecipe = recipes.stream()
                    .filter(recipe -> prefRecipeId == recipe.getmId())
                    .findAny()
                    .orElse(null);

            return favoriteRecipe;


        }catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

}
