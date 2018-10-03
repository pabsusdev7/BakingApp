package com.ingenuityapps.android.bakingapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ingenuityapps.android.bakingapp.BakingIngredientsWidgetProvider;
import com.ingenuityapps.android.bakingapp.R;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ingenuityapps.android.bakingapp.MainActivity.APP_PREFERENCES;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_ID_PREF;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_NAME_PREF;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder> {

    private List<Recipe> mRecipesData;

    private final RecipeAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private static final String TAG = RecipeAdapter.class.getSimpleName();

    public interface RecipeAdapterOnClickHandler{
        void onClick(Recipe recipe);

    }

    public RecipeAdapter(RecipeAdapterOnClickHandler clickHandler, Context context)
    {
        mClickHandler = clickHandler;
        mContext = context;
    }

    public class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe)
        public TextView mRecipeTextView;
        @BindView(R.id.iv_favorite_recipe)
        public ImageView mFavoriteRecipeImageView;

        public RecipeAdapterViewHolder(View view)
        {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = mRecipesData.get(adapterPosition);
            mClickHandler.onClick(recipe);

        }
    }


    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_recipe;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new RecipeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, final int position) {

        final Recipe recipe = mRecipesData.get(position);
        holder.mRecipeTextView.setText(recipe.getmName());

        SharedPreferences preferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        int prefRecipeId = preferences.getInt(RECIPE_ID_PREF,-1);
        holder.mFavoriteRecipeImageView.setImageResource(recipe.getmId() != prefRecipeId ? R.drawable.ic_empty_star : R.drawable.ic_star_full);

        holder.mFavoriteRecipeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView)v;

                SharedPreferences preferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

                int prefRecipeId = preferences.getInt(RECIPE_ID_PREF,-1);

                if(recipe.getmId() != prefRecipeId)
                {
                    preferences.edit()
                            .putInt(RECIPE_ID_PREF,recipe.getmId())
                            .putString(RECIPE_NAME_PREF,recipe.getmName())
                            .commit();

                    imageView.setImageResource(R.drawable.ic_star_full);
                }
                else
                {
                    preferences.edit().clear().commit();
                    imageView.setImageResource(R.drawable.ic_empty_star);
                }

                notifyDataSetChanged();
                BakingIngredientsWidgetProvider.sendRefreshBroadcast(mContext);
            }
        });



    }

    @Override
    public int getItemCount() {
        if(mRecipesData == null) return 0;

        return mRecipesData.size();
    }

    public void setmRecipesData(List<Recipe> recipesData)
    {
        mRecipesData = recipesData;
        notifyDataSetChanged();
    }



}