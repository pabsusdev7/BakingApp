package com.ingenuityapps.android.bakingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ingenuityapps.android.bakingapp.data.Recipe;
import com.ingenuityapps.android.bakingapp.data.RecipeAdapter;
import com.ingenuityapps.android.bakingapp.utilities.JsonUtils;
import com.ingenuityapps.android.bakingapp.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String RECIPE_INTENT = "recipe";
    public static final String RECIPE_STEP_INTENT = "recipeStep";
    public static final String TWO_PANE_INTENT = "twoPane";
    public static final String APP_PREFERENCES = "prefs";
    public static final String RECIPE_ID_PREF = "prefRecipeId";
    public static final String RECIPE_NAME_PREF = "prefRecipeName";



    @BindView(R.id.rv_recipes)
    RecyclerView mRecipesRecyclerView;
    @BindView(R.id.tv_error_message_display_recipes)
    TextView mRecipesErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator_recipes)
    ProgressBar mRecipesLoadingIndicator;

    private RecipeAdapter mRecipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        LinearLayoutManager recipesLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecipesRecyclerView.setLayoutManager(recipesLayoutManager);
        mRecipesRecyclerView.setHasFixedSize(true);
        mRecipeAdapter = new RecipeAdapter(this, getApplicationContext());
        mRecipesRecyclerView.setAdapter(mRecipeAdapter);

        loadRecipesData();


    }

    private void loadRecipesData()
    {
        new FetchRecipesTask().execute();
    }

    @Override
    public void onClick(Recipe recipe) {

        Intent recipeDetailIntent = new Intent(this,DetailActivity.class);
        recipeDetailIntent.putExtra(RECIPE_INTENT, (Parcelable) recipe);
        startActivity(recipeDetailIntent);

    }

    public class FetchRecipesTask extends AsyncTask<Void, Void, List<Recipe>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mRecipesLoadingIndicator.setVisibility(View.VISIBLE);
        }


        @Override
        protected List<Recipe> doInBackground(Void... voids) {

            URL recipesRequestURL = NetworkUtils.buildRecipesUrl();

            try
            {
                String jsonRecipesResponse = NetworkUtils.getResponseFromHttpUrl(recipesRequestURL);

                List<Recipe> recipes = JsonUtils.parseRecipesFromJson(jsonRecipesResponse);

                return recipes;



            }catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Recipe> recipesData) {
            mRecipesLoadingIndicator.setVisibility(View.INVISIBLE);
            if(recipesData!=null)
            {
                showRecipesDataView();
                mRecipeAdapter.setmRecipesData(recipesData);
            }
            else
            {
                showRecipesErrorMessage();
            }
        }
    }

    private void showRecipesErrorMessage() {
        mRecipesRecyclerView.setVisibility(View.INVISIBLE);
        mRecipesErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showRecipesDataView() {
        mRecipesErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecipesRecyclerView.setVisibility(View.VISIBLE);
    }
}
