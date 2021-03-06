package musicplayer.krithghosh.com.musicplayer.view.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import musicplayer.krithghosh.com.musicplayer.R;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import musicplayer.krithghosh.com.musicplayer.service.MediaPlayerService;
import musicplayer.krithghosh.com.musicplayer.utils.AppUtils;
import musicplayer.krithghosh.com.musicplayer.view.fragment.SongPlayerFragment;
import musicplayer.krithghosh.com.musicplayer.view.fragment.SongsListFragment;

import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.BROADCAST_MEDIA_ACTIONS;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.INTENT_ACTIONS;
import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.INTENT_VALUE;
import static musicplayer.krithghosh.com.musicplayer.view.fragment.SongPlayerFragment.BUNDLE_SONG_DURATION;
import static musicplayer.krithghosh.com.musicplayer.view.fragment.SongPlayerFragment.BUNDLE_SONG_IS_PLAYING;
import static musicplayer.krithghosh.com.musicplayer.view.fragment.SongPlayerFragment.BUNDLE_SONG_POSITION;

public class HomeActivity extends AppCompatActivity implements SongsListFragment.SongsListEventListener,
        SongPlayerFragment.SongPlayerFragmentInteractionListener, MediaController.MediaPlayerControl {

    @BindView(R.id.rl_parent_layout)
    RelativeLayout rlParentLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content_frame)
    FrameLayout contentFrame;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.fl_footer_layout)
    FrameLayout flFooterLayout;

    @BindView(R.id.tv_song)
    TextView tvSongName;

    @BindView(R.id.tv_artist)
    TextView tvArtistName;

    @BindView(R.id.iv_home_action)
    ImageView ivHomeAction;

    @BindView(R.id.progress_bar_action)
    ProgressBar progressBarAction;

    private Toast toast;
    private Intent playIntent;
    private boolean isLoading = false;
    private int mCurrentSongPosition = 0;
    private Snackbar snackbar;
    private boolean mediaBound = false;
    private FragmentManager fragmentManager;
    private SongMetadata mSongMetadata = null;
    private MediaPlayerService mediaPlayerService;
    private SongPlayerFragment mSongPlayerFragment = null;
    private List<SongMetadata> mSongsList = null;
    private boolean paused = false, playbackPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setFragmentManager();
        setupToolbar();
        setupToastAndProgressDialog();
        setupFragment();
    }

    public int getLayoutId() {
        return R.layout.activity_home;
    }

    private void setFragmentManager() {
        fragmentManager = getSupportFragmentManager();
    }

    private void setupToastAndProgressDialog() {
        toast = Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT);
        snackbar = Snackbar.make(rlParentLayout, "", Snackbar.LENGTH_INDEFINITE);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.music_player_heading);
    }

    private void setupFragment() {
        Bundle bundle = new Bundle();
        SongsListFragment fragment = SongsListFragment.newInstance(bundle);
        AppUtils.addFragment(fragmentManager,
                fragment,
                R.id.content_frame,
                true,
                SongsListFragment.class.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(handleMediaAction, new IntentFilter(BROADCAST_MEDIA_ACTIONS));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(handleMediaAction);
    }

    @Override
    public void showError(String msg) {
        if (!toast.getView().isShown()) {
            toast.setText(msg);
            toast.show();
        }
    }

    @Override
    public void showSnackBar(String msg) {
        if (!snackbar.isShownOrQueued()) {
            snackbar.setText(msg).show();
        }
    }

    @Override
    public void showLoader() {
        if (!progressBar.isShown()) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoader() {
        if (!progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateSongsList(List<SongMetadata> mSongsList) {
        this.mSongsList = mSongsList;
    }

    private void showFooterLoader() {
        progressBarAction.setVisibility(View.VISIBLE);
        ivHomeAction.setVisibility(View.INVISIBLE);
    }

    private void hideFooterLoader() {
        progressBarAction.setVisibility(View.INVISIBLE);
        ivHomeAction.setVisibility(View.VISIBLE);
    }

    @Override
    public void seekTo(int position) {
        mediaPlayerService.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayerService != null && mediaBound) {
            return mediaPlayerService.isPlaying();
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void play() {
        playbackPaused = false;
        footerPlay();
        mediaPlayerService.play();
    }

    @Override
    public void start() {
        mediaPlayerService.play();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        footerPause();
        mediaPlayerService.pause();
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayerService != null && mediaBound && mediaPlayerService.isPlaying()) {
            return mediaPlayerService.getPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if (mediaPlayerService != null && mediaBound && mediaPlayerService.isPlaying()) {
            return mediaPlayerService.getDuration();
        }
        return 0;
    }

    @Override
    public void startPlaySong() {
        isLoading = true;
        mediaPlayerService.setSongUrl(mSongMetadata.getUrl());
        mediaPlayerService.initializePlaySong();
    }

    public void changeTrack() {
        isLoading = true;
        mediaPlayerService.setSongUrl(mSongMetadata.getUrl());
        stopPlayingSongs();
        mediaPlayerService.initializePlaySong();
    }

    public void stopPlayingSongs() {
        mediaPlayerService.stop();
        mediaPlayerService.reset();
    }

    @Override
    public void rewind() {
        if (getCurrentPosition() < 20000) {
            mCurrentSongPosition = mCurrentSongPosition - 1;
            if (mSongsList != null && mSongsList.size() > 0 && mCurrentSongPosition >= 0 && mCurrentSongPosition < (mSongsList.size() - 1)) {
                this.mSongMetadata = mSongsList.get(mCurrentSongPosition);
                mSongPlayerFragment.updateSongMetadata(this.mSongMetadata);
                changeTrack();
            } else {
                showError(getString(R.string.no_more_tracks));
                mCurrentSongPosition = mCurrentSongPosition + 1;
            }
            return;
        }
        mediaPlayerService.seekTo(0);
    }

    @Override
    public void forward() {
        mCurrentSongPosition = mCurrentSongPosition + 1;
        if (mSongsList != null && mSongsList.size() > 0 && mCurrentSongPosition > 0 && mCurrentSongPosition < mSongsList.size()) {
            this.mSongMetadata = mSongsList.get(mCurrentSongPosition);
            mSongPlayerFragment.updateSongMetadata(this.mSongMetadata);
            changeTrack();
        } else {
            showError(getString(R.string.no_more_tracks));
            mCurrentSongPosition = mCurrentSongPosition - 1;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MediaPlayerService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(playIntent);
        mediaPlayerService = null;
        super.onDestroy();
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MusicBinder binder = (MediaPlayerService.MusicBinder) service;
            mediaPlayerService = binder.getService();
            mediaBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaBound = false;
        }
    };

    public Bundle getMediaStatusInBundle(Bundle bundle) {
        if (isPlaying()) {
            bundle.putBoolean(BUNDLE_SONG_IS_PLAYING, isPlaying());
        }
        bundle.putInt(BUNDLE_SONG_POSITION, getCurrentPosition());
        bundle.putInt(BUNDLE_SONG_DURATION, getDuration());
        return bundle;
    }

    @Override
    public void onBackPressed() {
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount == 1) {
            finish();
            return;
        }
        mSongPlayerFragment = null;
        if (flFooterLayout.getVisibility() == View.GONE) {
            showFooterPlayer();
        }
        super.onBackPressed();
    }

    private void showFooterPlayer() {
        if (mSongMetadata == null) {
            tvSongName.setText("");
            tvArtistName.setText("");
            flFooterLayout.setVisibility(View.GONE);
            return;
        }
        flFooterLayout.setVisibility(View.VISIBLE);
        tvSongName.setText(mSongMetadata.getSong());
        tvArtistName.setText(mSongMetadata.getArtists());
        if (isLoading) {
            showFooterLoader();
        }
        ivHomeAction.setOnClickListener(v -> {
            if (isPlaying()) {
                if (mSongPlayerFragment != null) {
                    mSongPlayerFragment.stateChangePause();
                }
                pause();
            } else if (playbackPaused) {
                if (mSongPlayerFragment != null) {
                    mSongPlayerFragment.stateChangePlay();
                }
                play();
            } else {
                showFooterLoader();
            }
        });
    }

    private void hideFooterPlayer() {
        flFooterLayout.setVisibility(View.GONE);
    }

    private void footerPause() {
        progressBarAction.setVisibility(View.GONE);
        ivHomeAction.setImageResource(R.drawable.ic_home_play);
    }

    private void footerPlay() {
        progressBarAction.setVisibility(View.GONE);
        ivHomeAction.setImageResource(R.drawable.ic_home_pause);
    }

    @Override
    public void showPlayer(SongMetadata mSongMetadata, int position) {
        pause();
        Bundle bundle = new Bundle();
        this.mSongMetadata = mSongMetadata;
        this.mCurrentSongPosition = position;
        hideFooterPlayer();
        bundle.putParcelable(SongPlayerFragment.BUNDLE_SONG_METADATA, mSongMetadata);
        bundle = getMediaStatusInBundle(bundle);
        mSongPlayerFragment = SongPlayerFragment.newInstance(bundle);
        AppUtils.addFragment(fragmentManager,
                mSongPlayerFragment,
                R.id.content_frame,
                true,
                SongPlayerFragment.class.getName());
    }

    private BroadcastReceiver handleMediaAction =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getStringExtra(INTENT_ACTIONS);
                    int value = intent.getIntExtra(INTENT_VALUE, 0);
                    if (mSongPlayerFragment == null) {
                        return;
                    }
                    switch (action) {
                        case "MEDIA_PLAYER_PREPARED":
                            isLoading = false;
                            hideFooterLoader();
                            ivHomeAction.setImageResource(R.drawable.ic_home_pause);
                            mSongPlayerFragment.mediaPlayerPrepared();
                            break;
                        case "POSITION_CHANGED":
                            mSongPlayerFragment.onPositionChanged(value);
                            break;
                        case "DURATION_CHANGED":
                            mSongPlayerFragment.onDurationChanged(value);
                            break;
                        case "PLAYBACK_COMPLETED":
                            mSongPlayerFragment.onPlaybackCompleted();
                            break;
                    }
                }
            };
}