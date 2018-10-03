package com.ingenuityapps.android.bakingapp.utilities;

import com.ingenuityapps.android.bakingapp.data.Recipe;
import com.ingenuityapps.android.bakingapp.data.RecipeIngredient;
import com.ingenuityapps.android.bakingapp.data.RecipeStep;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String RECIPE_ID_TAG = "id";
    private static final String RECIPE_NAME_TAG = "name";
    private static final String RECIPE_SERVINGS_TAG = "servings";
    private static final String RECIPE_INGREDIENTS_TAG = "ingredients";
    private static final String RECIPE_INGREDIENT_QUANTITY_TAG = "quantity";
    private static final String RECIPE_INGREDIENT_NAME_TAG = "ingredient";
    private static final String RECIPE_INGREDIENT_MEASURE_TAG = "measure";
    private static final String RECIPE_STEPS_TAG = "steps";
    private static final String RECIPE_STEP_ID_TAG = "id";
    private static final String RECIPE_STEP_SHORTDESC_TAG = "shortDescription";
    private static final String RECIPE_STEP_DESC_TAG = "description";
    private static final String RECIPE_STEP_VIDEOURL_TAG = "videoURL";
    private static final String RECIPE_STEP_THUMBNAILURL_TAG = "thumbnailURL";


    public static List<Recipe> parseRecipesFromJson(String json) throws JSONException {

        List<Recipe> recipes = new ArrayList<Recipe>();

        JSONArray recipeResults = new JSONArray(json);

        for (int i=0; i<recipeResults.length();i++) {

            Recipe recipe = new Recipe();
            JSONObject jsonRecipe = recipeResults.getJSONObject(i);
            recipe.setmId(jsonRecipe.optInt(RECIPE_ID_TAG));
            recipe.setmName(jsonRecipe.optString(RECIPE_NAME_TAG));
            recipe.setmServings(jsonRecipe.optInt(RECIPE_SERVINGS_TAG));

            List<RecipeIngredient> recipeIngredients = new ArrayList<>();
            JSONArray jsonIngredients = jsonRecipe.getJSONArray(RECIPE_INGREDIENTS_TAG);

            for(int j=0; j<jsonIngredients.length(); j++)
            {
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                JSONObject jsonIngredient = jsonIngredients.getJSONObject(j);
                recipeIngredient.setMeasure(jsonIngredient.optString(RECIPE_INGREDIENT_MEASURE_TAG));
                recipeIngredient.setName(jsonIngredient.optString(RECIPE_INGREDIENT_NAME_TAG));
                recipeIngredient.setQuantity(jsonIngredient.optInt(RECIPE_INGREDIENT_QUANTITY_TAG));

                recipeIngredients.add(recipeIngredient);
            }

            recipe.setIngredients(recipeIngredients);

            List<RecipeStep> recipeSteps = new ArrayList<>();
            JSONArray jsonSteps = jsonRecipe.getJSONArray(RECIPE_STEPS_TAG);

            for(int k=0; k<jsonSteps.length(); k++)
            {
                RecipeStep recipeStep = new RecipeStep();
                JSONObject jsonStep = jsonSteps.getJSONObject(k);
                recipeStep.setId(jsonStep.optInt(RECIPE_STEP_ID_TAG));
                recipeStep.setShortDescription(jsonStep.optString(RECIPE_STEP_SHORTDESC_TAG));
                recipeStep.setDescription(jsonStep.optString(RECIPE_STEP_DESC_TAG));
                recipeStep.setVideoUrl(jsonStep.optString(RECIPE_STEP_VIDEOURL_TAG));
                recipeStep.setThumbnailUrl(jsonStep.optString(RECIPE_STEP_THUMBNAILURL_TAG));

                recipeSteps.add(recipeStep);
            }

            recipe.setSteps(recipeSteps);


            recipes.add(recipe);

        }

        return recipes;
    }

}
