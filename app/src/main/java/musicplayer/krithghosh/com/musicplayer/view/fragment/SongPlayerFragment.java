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
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import musicplayer.krithghosh.com.musicplayer.R;
import musicplayer.krithghosh.com.musicplayer.adapter.PlayerAdapter;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;

import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_PAUSE;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_PLAY;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.SONG_STATE_RESET;

public class SongPlayerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "SongPlayerFragment";

    @BindView(R.id.fl_parent_layout)
    FrameLayout parentLayout;

    @BindView(R.id.card_view)
    CardView cardView;

    @BindView(R.id.tv_song_name)
    TextView songName;

    @BindView(R.id.iv_play)
    ImageView ivButtonPlay;

    @BindView(R.id.iv_pause)
    ImageView ivButtonPause;

    @BindView(R.id.iv_reset)
    ImageView ivButtonReset;

    @BindView(R.id.seekbar_audio)
    SeekBar mSeekbarAudio;

    private SongMetadata songMetadata = null;
    private PlayerAdapter mPlayerAdapter;
    private boolean mUserIsSeeking = false;
    private SongPlayerFragmentInteractionListener mEventListener;
    public static final String BUNDLE_SONG_METADATA = "bundle_song_metadata";

    public interface SongPlayerFragmentInteractionListener {
        void onBackPressed();
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
        songMetadata = getArguments().getParcelable(BUNDLE_SONG_METADATA);
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
        initializeUI();
        initializeSeekbar();
        initializePlaybackController();
        mPlayerAdapter.loadMedia(songMetadata.getUrl());
        ivButtonPlay.performClick();
    }

    private void initializeUI() {
        if (songMetadata != null && !TextUtils.isEmpty(songMetadata.getSong())) {
            songName.setText(songMetadata.getSong());
        }
        cardView.setOnClickListener(this);
        parentLayout.setOnClickListener(this);
        ivButtonPlay.setOnClickListener(this);
        ivButtonPause.setOnClickListener(this);
        ivButtonReset.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPlayerAdapter.release();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_view:
                break;
            case R.id.fl_parent_layout:
                mEventListener.onBackPressed();
                break;
            case R.id.iv_play:
                mPlayerAdapter.play();
                ButtonState(SONG_STATE_PLAY);
                break;

            case R.id.iv_pause:
                mPlayerAdapter.pause();
                ButtonState(SONG_STATE_PAUSE);
                break;

            case R.id.iv_reset:
                //mPlayerAdapter.reset();
                mPlayerAdapter.seekTo(0);
                ButtonState(SONG_STATE_RESET);
                break;
        }
    }

    public void ButtonState(String state) {
        switch (state) {
            case SONG_STATE_PLAY:
                ivButtonPlay.setVisibility(View.INVISIBLE);
                ivButtonPause.setVisibility(View.VISIBLE);
                ivButtonReset.setVisibility(View.VISIBLE);
                break;

            case SONG_STATE_PAUSE:
                ivButtonPlay.setVisibility(View.VISIBLE);
                ivButtonPause.setVisibility(View.INVISIBLE);
                ivButtonReset.setVisibility(View.INVISIBLE);
                break;

            case SONG_STATE_RESET:
                ivButtonPlay.setVisibility(View.INVISIBLE);
                ivButtonPause.setVisibility(View.VISIBLE);
                ivButtonReset.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mMediaPlayerHolder = new MediaPlayerHolder(getContext());
        mMediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        mPlayerAdapter = mMediaPlayerHolder;
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
                        mPlayerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    public class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onDurationChanged(int duration) {
            mSeekbarAudio.setMax(duration);
            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @Override
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

        @Override
        public void onStateChanged(@State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
        }

        @Override
        public void onPlaybackCompleted() {
        }
    }
}
