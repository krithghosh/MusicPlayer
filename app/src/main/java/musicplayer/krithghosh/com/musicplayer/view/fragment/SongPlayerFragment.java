package musicplayer.krithghosh.com.musicplayer.view.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import musicplayer.krithghosh.com.musicplayer.R;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import musicplayer.krithghosh.com.musicplayer.utils.ImageUtils;

import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_FORWARD;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_LOADING;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_PAUSE;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_PLAY;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_REWIND;

public class SongPlayerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SongPlayerFragment";

    @BindView(R.id.fl_parent_layout)
    FrameLayout parentLayout;

    @BindView(R.id.card_view)
    CardView cardView;

    @BindView(R.id.iv_song_image)
    ImageView songImage;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tv_song_name)
    TextView songName;

    @BindView(R.id.iv_play)
    ImageView ivButtonPlay;

    @BindView(R.id.iv_pause)
    ImageView ivButtonPause;

    @BindView(R.id.iv_rewind)
    ImageView ivButtonRewind;

    @BindView(R.id.iv_forward)
    ImageView ivButtonForward;

    @BindView(R.id.seekbar_audio)
    SeekBar mSeekbarAudio;

    private boolean mUserIsSeeking = false;
    private SongMetadata mSongMetadata = null;
    private boolean mIsPlaying = false;
    private int mCurrentPosition = 0;
    private int mDuration = 0;
    private SongPlayerFragmentInteractionListener mEventListener;
    public static final String BUNDLE_SONG_METADATA = "bundle_song_metadata";
    public static final String BUNDLE_SONG_IS_PLAYING = "bundle_song_is_playing";
    public static final String BUNDLE_SONG_POSITION = "bundle_song_position";
    public static final String BUNDLE_SONG_DURATION = "bundle_song_duration";

    public interface SongPlayerFragmentInteractionListener {
        void seekTo(int position);

        void startPlaySong();

        void changeTrack();

        void play();

        void pause();

        void rewind();

        void forward();
    }

    public static SongPlayerFragment newInstance(Bundle bundle) {
        SongPlayerFragment fragment = new SongPlayerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mEventListener = (SongPlayerFragmentInteractionListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            return;
        }
        if (getArguments().getParcelable(BUNDLE_SONG_METADATA) == null) {
            return;
        }
        Bundle bundle = getArguments();
        mSongMetadata = bundle.getParcelable(BUNDLE_SONG_METADATA);
        mIsPlaying = bundle.getBoolean(BUNDLE_SONG_IS_PLAYING);
        mCurrentPosition = bundle.getInt(BUNDLE_SONG_POSITION);
        mDuration = bundle.getInt(BUNDLE_SONG_DURATION);
        Log.d(TAG, "onCreate: finished");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_song_player, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: finished");
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeUI();
        initializeSeekbar();
        if (mIsPlaying) {
            songPlayingState();
        } else {
            mEventListener.changeTrack();
        }
        Log.d(TAG, "onStart: finished");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: finished");
    }

    private void initializeUI() {
        if (mSongMetadata != null && !TextUtils.isEmpty(mSongMetadata.getSong())) {
            songName.setText(mSongMetadata.getSong());
        }
        ImageUtils.setImage(
                getContext(),
                mSongMetadata.getCoverImage(),
                R.drawable.ic_music_player,
                songImage);
        buttonState(SONG_STATE_LOADING);
        cardView.setOnClickListener(this);
        parentLayout.setOnClickListener(this);
        ivButtonPlay.setOnClickListener(this);
        ivButtonPause.setOnClickListener(this);
        ivButtonRewind.setOnClickListener(this);
        ivButtonForward.setOnClickListener(this);
        Log.d(TAG, "initializeUI: finished");
    }

    private void initializeSeekbar() {
        mSeekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mUserIsSeeking = false;
                        mEventListener.seekTo(userSelectedPosition);
                    }
                });
        Log.d(TAG, "initializeSeekbar: finished");
    }

    public void updateSongMetadata(SongMetadata mSongMetadata) {
        this.mSongMetadata = mSongMetadata;
        if (mSongMetadata != null && !TextUtils.isEmpty(mSongMetadata.getSong())) {
            songName.setText(mSongMetadata.getSong());
        }
        ImageUtils.setImage(
                getContext(),
                mSongMetadata.getCoverImage(),
                R.drawable.ic_music_player,
                songImage);
        buttonState(SONG_STATE_LOADING);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_view:
                break;
            case R.id.fl_parent_layout:
                break;
            case R.id.iv_play:
                buttonState(SONG_STATE_PLAY);
                mEventListener.play();
                break;

            case R.id.iv_pause:
                buttonState(SONG_STATE_PAUSE);
                mEventListener.pause();
                break;

            case R.id.iv_rewind:
                buttonState(SONG_STATE_REWIND);
                mEventListener.rewind();
                break;

            case R.id.iv_forward:
                buttonState(SONG_STATE_FORWARD);
                mEventListener.forward();
                break;
        }
    }

    public void songPlayingState() {
        buttonState(SONG_STATE_PLAY);
        onDurationChanged(mDuration);
    }

    public void stateChangePlay() {
        buttonState(SONG_STATE_PLAY);
    }

    public void stateChangePause() {
        buttonState(SONG_STATE_PAUSE);
    }

    public void buttonState(String state) {
        switch (state) {
            case SONG_STATE_PLAY:
                progressBar.setVisibility(View.INVISIBLE);
                ivButtonPlay.setVisibility(View.INVISIBLE);
                ivButtonPause.setVisibility(View.VISIBLE);
                ivButtonRewind.setVisibility(View.VISIBLE);
                ivButtonForward.setVisibility(View.VISIBLE);
                mSeekbarAudio.setVisibility(View.VISIBLE);
                break;

            case SONG_STATE_PAUSE:
                progressBar.setVisibility(View.INVISIBLE);
                ivButtonPlay.setVisibility(View.VISIBLE);
                ivButtonPause.setVisibility(View.INVISIBLE);
                ivButtonRewind.setVisibility(View.VISIBLE);
                ivButtonForward.setVisibility(View.VISIBLE);
                mSeekbarAudio.setVisibility(View.VISIBLE);
                break;

            case SONG_STATE_REWIND:
                progressBar.setVisibility(View.INVISIBLE);
                ivButtonPlay.setVisibility(View.INVISIBLE);
                ivButtonPause.setVisibility(View.VISIBLE);
                ivButtonRewind.setVisibility(View.VISIBLE);
                ivButtonForward.setVisibility(View.VISIBLE);
                mSeekbarAudio.setVisibility(View.VISIBLE);
                break;

            case SONG_STATE_FORWARD:
                progressBar.setVisibility(View.INVISIBLE);
                ivButtonPlay.setVisibility(View.INVISIBLE);
                ivButtonPause.setVisibility(View.VISIBLE);
                ivButtonRewind.setVisibility(View.VISIBLE);
                ivButtonForward.setVisibility(View.VISIBLE);
                mSeekbarAudio.setVisibility(View.VISIBLE);
                break;

            case SONG_STATE_LOADING:
                progressBar.setVisibility(View.VISIBLE);
                ivButtonPlay.setVisibility(View.INVISIBLE);
                ivButtonPause.setVisibility(View.INVISIBLE);
                ivButtonRewind.setVisibility(View.INVISIBLE);
                ivButtonForward.setVisibility(View.INVISIBLE);
                mSeekbarAudio.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void onDurationChanged(int duration) {
        mSeekbarAudio.setMax(duration);
        Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
    }

    public void onPositionChanged(int position) {
        if (!mUserIsSeeking) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mSeekbarAudio.setProgress(position, true);
            } else {
                mSeekbarAudio.setProgress(position);
            }
            Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
        }
    }

    public void onPlaybackCompleted() {
        buttonState(SONG_STATE_PAUSE);
    }

    public void mediaPlayerPrepared() {
        buttonState(SONG_STATE_PLAY);
        ivButtonPlay.performClick();
    }
}
