package musicplayer.krithghosh.com.musicplayer.view.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import musicplayer.krithghosh.com.musicplayer.adapter.PlayerAdapter;
import musicplayer.krithghosh.com.musicplayer.view.fragment.PlaybackInfoListener;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class MediaPlayerHolder implements PlayerAdapter {
    private static final String TAG = "MediaPlayerHolder";

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mStreamUrl;
    private PlaybackInfoListener mPlaybackInfoListener;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;

    public MediaPlayerHolder(Context context) {
        mContext = context.getApplicationContext();
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopUpdatingCallbackWithPosition(true);
                    if (mPlaybackInfoListener != null) {
                        mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                        mPlaybackInfoListener.onPlaybackCompleted();
                    }
                }
            });
        }
    }

    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        mPlaybackInfoListener = listener;
    }

    // Implements PlaybackControl.
    @Override
    public void loadMedia(String mStreamUrl) {
        this.mStreamUrl = mStreamUrl;
        initializeMediaPlayer();
        try {
            mMediaPlayer.setDataSource(mStreamUrl);
        } catch (Exception e) {
            Log.e(TAG, "loadMedia: ", e);
        }
        try {
            mMediaPlayer.prepare();
        } catch (Exception e) {
            Log.e(TAG, "loadMedia: ", e);
        }
        initializeProgressCallback();
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PLAYING);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            loadMedia(mStreamUrl);
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.RESET);
            }
            stopUpdatingCallbackWithPosition(true);
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onStateChanged(PlaybackInfoListener.State.PAUSED);
            }
        }
    }

    @Override
    public void seekTo(int position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekbarPositionUpdateTask == null) {
            mSeekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        mExecutor.scheduleAtFixedRate(
                mSeekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition) {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(0);
            }
        }
    }

    private void updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void initializeProgressCallback() {
        final int duration = mMediaPlayer.getDuration();
        if (mPlaybackInfoListener != null) {
            mPlaybackInfoListener.onDurationChanged(duration);
            mPlaybackInfoListener.onPositionChanged(0);
        }
    }
}
