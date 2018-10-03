package com.ingenuityapps.android.bakingapp.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ingenuityapps.android.bakingapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.StepListAdapterViewHolder> {

    private List<RecipeStep> mRecipeStepsData;

    private final StepListAdapter.StepListAdapterOnClickHandler mClickHandler;
    private static final String TAG = StepListAdapter.class.getSimpleName();

    public interface StepListAdapterOnClickHandler{
        void onClick(RecipeStep recipeStep);

    }

    public StepListAdapter(StepListAdapter.StepListAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    public class StepListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //@BindView(R.id.tv_step_description)
        public TextView mRecipeStepTextView;

        public StepListAdapterViewHolder(View view)
        {
            super(view);
            //ButterKnife.bind(this, view);
            mRecipeStepTextView = (TextView) view.findViewById(R.id.tv_step_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            RecipeStep recipeStep = mRecipeStepsData.get(adapterPosition);
            mClickHandler.onClick(recipeStep);

        }
    }


    @Override
    public StepListAdapter.StepListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_step;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = layoutInflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new StepListAdapter.StepListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepListAdapter.StepListAdapterViewHolder holder, int position) {

        RecipeStep recipeStep = mRecipeStepsData.get(position);
        holder.mRecipeStepTextView.setText(recipeStep.getShortDescription());



    }

    @Override
    public int getItemCount() {
        if(mRecipeStepsData == null) return 0;

        return mRecipeStepsData.size();
    }

    public void setmRecipeStepsData(List<RecipeStep> recipeStepsData)
    {
        mRecipeStepsData = recipeStepsData;
        notifyDataSetChanged();
    }



}
