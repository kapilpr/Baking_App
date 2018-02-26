package com.example.android.bakingapp.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeDetailActivity;
import com.example.android.bakingapp.models.Steps;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StepsDetailFragment extends Fragment implements ExoPlayer.EventListener {

    @BindView(R.id.simpleExoPlayer)
    SimpleExoPlayerView mPlayerView;

    @BindView(R.id.description_tv)
    TextView descriptionTextView;

    @BindView(R.id.recipe_thumbnail_iv)
    ImageView recipeThumbnailImageView;

    @BindView(R.id.next_button)
    Button nextButton;

    @BindView(R.id.previous_button)
    Button previousButton;

    private static final String MEDIASESSION_TAG = "steps_detail_fragment_ms";
    private static final String PLAYER_POSITION_KEY = "player_position";
    private boolean isLandscape;
    private Uri mVideoUri;
    private long mPlayerPosition = 0;
    private ArrayList<Steps> mStepsArrayList;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private static MediaSessionCompat mMediaSession;
    private int mPosition;

    public StepsDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate((R.layout.fragment_steps_detail), container, false);
        ButterKnife.bind(this, view);
        isLandscape = getContext().getResources().getBoolean(R.bool.is_landscape);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(RecipeDetailActivity.STEPS_POSITION_KEY);
            mStepsArrayList = savedInstanceState.getParcelableArrayList(RecipeDetailActivity.STEPS_LIST_KEY);
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION_KEY);
        }
        if (mStepsArrayList != null) {
            if (!RecipeDetailActivity.mTwoPane) {
                if (mPosition == 0) {
                    previousButton.setEnabled(false);
                }
                if (mPosition == mStepsArrayList.size() - 1) {
                    nextButton.setEnabled(false);
                }
            } else {
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
            }
            Steps currentStep = mStepsArrayList.get(mPosition);
            setUpStepDetailView(currentStep);
        }
        return view;
    }

    private void setUpStepDetailView(Steps currentStep) {
        descriptionTextView.setText(currentStep.getDescription());

        // Set the Thumbnail ImageView
        String imageUrl = currentStep.getThumbnailURL();
        if (imageUrl.isEmpty()) {
            Picasso.with(getActivity())
                    .load(R.drawable.baking_basics)
                    .into(recipeThumbnailImageView);
        } else {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .error(R.drawable.baking_basics)
                    .into(recipeThumbnailImageView);
        }

        // Set the Video Playback
        String videoUrl = currentStep.getVideoURL();

        if (videoUrl.isEmpty()) {
            mPlayerView.setVisibility(View.GONE);
        } else {
            mPlayerView.setVisibility(View.VISIBLE);
            initializeMediaSession();
            mVideoUri = Uri.parse(videoUrl);
            initializePlayer();
            // Show the video on Full Screen in Landscape
            if (isLandscape && !RecipeDetailActivity.mTwoPane) {
                mPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                mPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                descriptionTextView.setVisibility(View.GONE);
                recipeThumbnailImageView.setVisibility(View.GONE);
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                hideSystemUI();
            }
        }
    }

    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), MEDIASESSION_TAG);

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

        // MediaSessionCompat.Callback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
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
        });

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializePlayer() {
        if (mExoPlayer == null) {
            //Create an instance of Exoplayer
            TrackSelector trackselector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackselector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mVideoUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.seekTo(mPlayerPosition);

        }
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
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
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

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
    }

    @OnClick(R.id.previous_button)
    void displayPreviousStep() {
        if (mPosition != 0) {
            mPosition--;
            nextButton.setEnabled(true);
            releasePlayer();
            mPlayerPosition = 0;
            setUpStepDetailView(mStepsArrayList.get(mPosition));
            if (mPosition == 0) {
                previousButton.setEnabled(false);
            }
        }
    }

    @OnClick(R.id.next_button)
    void displayNextStep() {
        if (mPosition != mStepsArrayList.size() - 1) {
            mPosition++;
            previousButton.setEnabled(true);
            releasePlayer();
            mPlayerPosition = 0;
            setUpStepDetailView(mStepsArrayList.get(mPosition));
            if (mPosition == mStepsArrayList.size() - 1) {
                nextButton.setEnabled(false);
            }
        }
    }

    private void hideSystemUI() {
        // This snippet hides the system bars.
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(RecipeDetailActivity.STEPS_POSITION_KEY, mPosition);
        outState.putParcelableArrayList(RecipeDetailActivity.STEPS_LIST_KEY, mStepsArrayList);
        // Save the current player position
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
        }
        outState.putLong(PLAYER_POSITION_KEY, mPlayerPosition);
        super.onSaveInstanceState(outState);
    }

    public void setClickedPosition(int clickedPosition) {
        mPosition = clickedPosition;
    }

    public void setStepsArrayList(ArrayList<Steps> stepsArrayList) {
        mStepsArrayList = stepsArrayList;
    }
}
