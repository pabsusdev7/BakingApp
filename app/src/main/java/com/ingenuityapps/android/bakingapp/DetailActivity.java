package com.ingenuityapps.android.bakingapp;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.ingenuityapps.android.bakingapp.data.Recipe;
import com.ingenuityapps.android.bakingapp.data.RecipeStep;

import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_INTENT;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_STEP_INTENT;
import static com.ingenuityapps.android.bakingapp.MainActivity.TWO_PANE_INTENT;

public class DetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListenter {


    private static final String TAG = DetailActivity.class.getSimpleName();
    private boolean mTwoPane;
    private StepDetailFragment mStepDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /*mStepDetailFragment = (StepDetailFragment) getSupportFragmentManager().findFragmentById(R.id.step_detail_fragment);

        mTwoPane = mStepDetailFragment != null;*/

        if(findViewById(R.id.step_detail_fragment) != null)
        {
            mTwoPane = true;

            if(savedInstanceState == null)
            {
                FragmentManager fragmentManager = getSupportFragmentManager();

                StepDetailFragment stepDetailFragment = new StepDetailFragment();
                fragmentManager.beginTransaction()
                        .add(R.id.step_detail_fragment, stepDetailFragment)
                        .commit();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onStepSelected(RecipeStep recipeStep, Recipe recipe)
    {
        if(mTwoPane)
        {
            /*mStepDetailFragment.setRecipeStepDetails(recipe, recipeStep);
            getSupportFragmentManager().beginTransaction()
                    .
                    .detach(mStepDetailFragment)
                    .attach(mStepDetailFragment)
                    .commit();*/

            StepDetailFragment stepDetailFragment = new StepDetailFragment();
            stepDetailFragment.setRecipeStepDetails(recipe, recipeStep);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_fragment, stepDetailFragment)
                    .commit();

        }else{

            Bundle b = new Bundle();
            b.putParcelable(RECIPE_STEP_INTENT, recipeStep);
            b.putParcelable(RECIPE_INTENT, recipe);
            b.putBoolean(TWO_PANE_INTENT,mTwoPane);

            final Intent intent = new Intent(this, StepDetailActivity.class);
            intent.putExtras(b);

            startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
