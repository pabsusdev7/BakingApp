package com.ingenuityapps.android.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ingenuityapps.android.bakingapp.data.Recipe;
import com.ingenuityapps.android.bakingapp.data.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_INTENT;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_STEP_INTENT;
import static com.ingenuityapps.android.bakingapp.MainActivity.TWO_PANE_INTENT;


public class StepDetailFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String TAG = StepDetailFragment.class.getSimpleName();
    private static final String PLAYBACK_POSITION_INDEX = "playBackPositionIndex";
    private static final String WINDOW_INDEX = "windowIndex";


    private RecipeStep mRecipeStep;
    private Recipe mRecipe;
    private boolean mTwoPane;
    private SimpleExoPlayer mExoPlayer;
    private long mPlayBackPosition;
    private int mWindowIndex;
    private boolean mShouldVideoGoFullScreen;
    @BindView(R.id.pv_videostep)
    SimpleExoPlayerView mPlayerView;
    @Nullable
    @BindView(R.id.tv_recipe_step_description)
    TextView mStepDescription;
    @Nullable
    @BindView(R.id.btn_next_step)
    Button nextStepButton;
    @Nullable
    @BindView(R.id.btn_previous_step)
    Button prevStepButton;


    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public StepDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_step_detail, container, false);

        ButterKnife.bind(this, rootView);

        initializeMediaSession();



        if(savedInstanceState==null) {

            mPlayBackPosition = 0;
            mWindowIndex = 0;

            Intent intentThatStartedThisActivity = getActivity().getIntent();

            if (intentThatStartedThisActivity != null) {

                mTwoPane = intentThatStartedThisActivity.getBooleanExtra(TWO_PANE_INTENT,true);
                if(mRecipeStep == null) {
                    mRecipe = intentThatStartedThisActivity.getParcelableExtra(RECIPE_INTENT);
                    mRecipeStep = intentThatStartedThisActivity.hasExtra(RECIPE_STEP_INTENT) ? (RecipeStep) intentThatStartedThisActivity.getParcelableExtra("recipeStep") : mRecipe.getSteps().get(0);
                }

            }
        }else{

            mPlayBackPosition = savedInstanceState.getLong(PLAYBACK_POSITION_INDEX);
            mWindowIndex = savedInstanceState.getInt(WINDOW_INDEX);
            mRecipe = savedInstanceState.getParcelable(RECIPE_INTENT);
            mRecipeStep = savedInstanceState.getParcelable(RECIPE_STEP_INTENT);
        }

        mShouldVideoGoFullScreen = getActivity().getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE && !mTwoPane;

        loadStepDetail();
        loadControls(mRecipe.getSteps().indexOf(mRecipeStep));

        if(!mShouldVideoGoFullScreen) {

            ((AppCompatActivity) getActivity()).getSupportActionBar().show();

            View.OnClickListener stepButtonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPlayBackPosition = 0;
                    mWindowIndex = 0;
                    int nextStepIndex = mRecipe.getSteps().indexOf(mRecipeStep) + (v.getId() == nextStepButton.getId() ? 1 : -1);
                    mRecipeStep = mRecipe.getSteps().get(nextStepIndex);
                    releasePlayer();
                    loadStepDetail();
                    loadControls(nextStepIndex);

                }
            };

            nextStepButton.setOnClickListener(stepButtonListener);
            prevStepButton.setOnClickListener(stepButtonListener);
        }
        else
        {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }




        return rootView;


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(RECIPE_INTENT,mRecipe);
        outState.putParcelable(RECIPE_STEP_INTENT,mRecipeStep);
        if(mExoPlayer!=null)
        {
            outState.putLong(PLAYBACK_POSITION_INDEX,mExoPlayer.getCurrentPosition());
            outState.putInt(WINDOW_INDEX,mExoPlayer.getCurrentWindowIndex());
        }
    }



    private void loadControls(int stepIndex) {

        if(!mShouldVideoGoFullScreen) {
            if (stepIndex + 1 >= mRecipe.getSteps().size())
                nextStepButton.setVisibility(View.INVISIBLE);
            else
                nextStepButton.setVisibility(View.VISIBLE);
            if (stepIndex - 1 < 0)
                prevStepButton.setVisibility(View.INVISIBLE);
            else
                prevStepButton.setVisibility(View.VISIBLE);
        }
    }

    private void loadStepDetail() {


        if(!mShouldVideoGoFullScreen)mStepDescription.setText(mRecipeStep.getDescription());

        if(!mRecipeStep.getVideoUrl().isEmpty())
        {
            mPlayerView.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(mRecipeStep.getVideoUrl());
            initializePlayer(videoUri);
        }
        else {
            mPlayerView.setVisibility(View.GONE);
        }
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.seekTo(mWindowIndex,mPlayBackPosition);
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

    public void setRecipeStepDetails(Recipe recipe, RecipeStep recipeStep)
    {
        mRecipe = recipe;
        mRecipeStep = recipeStep;
    }

}
