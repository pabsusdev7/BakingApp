package com.ingenuityapps.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.ingenuityapps.android.bakingapp.data.Recipe;
import com.ingenuityapps.android.bakingapp.data.RecipeIngredient;
import com.ingenuityapps.android.bakingapp.data.RecipeStep;
import com.ingenuityapps.android.bakingapp.data.StepListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_INTENT;


public class RecipeDetailFragment extends Fragment implements StepListAdapter.StepListAdapterOnClickHandler {

    OnStepClickListenter mCallback;

    private static final String TAG = RecipeDetailFragment.class.getSimpleName();

    private Recipe mRecipe;
    @BindView(R.id.rv_steps)
    RecyclerView mRecyclerView;
    @BindView(R.id.rv_ingredients)
    ListView mIngredientsListView;
    @BindView(R.id.tv_recipe_title)
    TextView mRecipeTitle;


    public interface OnStepClickListenter{
        void onStepSelected(RecipeStep recipeStep, Recipe recipe);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (OnStepClickListenter) context;
        }catch (ClassCastException ex)
        {
            throw new ClassCastException(context.toString()
                    + "must implement OnStepClickListener");
        }
    }

    public RecipeDetailFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        ButterKnife.bind(this,rootView);

        LinearLayoutManager recipeStepsLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(recipeStepsLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        StepListAdapter stepListAdapter = new StepListAdapter(this);
        mRecyclerView.setAdapter(stepListAdapter);

        Intent intentThatStartedThisActivity = getActivity().getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(RECIPE_INTENT)) {

                mRecipe = intentThatStartedThisActivity.getParcelableExtra(RECIPE_INTENT);
                if(mRecipe!=null) {
                    Log.d(TAG, "Recipe selected: " + mRecipe.getmName());

                    mRecipeTitle.setText(mRecipe.getmName());

                    stepListAdapter.setmRecipeStepsData(mRecipe.getSteps());

                    List<String> recipeIngredients = new ArrayList<>();

                    for(RecipeIngredient ingredient:mRecipe.getIngredients())
                    {
                        String ingredientName = ingredient.getName();
                        recipeIngredients.add(ingredientName.substring(0,1).toUpperCase() + ingredientName.substring(1) + " (" + ingredient.getQuantity() + " " + ingredient.getMeasure() + ")");
                    }

                    mIngredientsListView.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.list_item_ingredient,recipeIngredients));

                } else{
                    Log.d(TAG, "No recipe selected data");
                }

            }
        }

        return rootView;
    }

    @Override
    public void onClick(RecipeStep recipeStep) {
        mCallback.onStepSelected(recipeStep, mRecipe);
    }
}
