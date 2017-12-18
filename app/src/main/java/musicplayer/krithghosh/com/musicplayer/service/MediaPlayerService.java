package musicplayer.krithghosh.com.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import musicplayer.krithghosh.com.musicplayer.adapter.PlayerAdapter;
import musicplayer.krithghosh.com.musicplayer.utils.AppUtils;

import static musicplayer.krithghosh.com.musicplayer.utils.AppUtils.INTENT_VALUE;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class MediaPlayerService extends Service implements PlayerAdapter, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "MediaPlayerService";

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    private MediaPlayer mMediaPlayer;
    private String mStreamUrl;
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekbarPositionUpdateTask;
    private final IBinder musicBind = new MusicBinder();

    public MediaPlayerService() {
    }

    public class MusicBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeMediaPlayer();
    }

    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
        }
        Log.d(TAG, "initializeMediaPlayer: finished");
    }

    @Override
    public void initializePlaySong() {
        try {
            if (TextUtils.isEmpty(mStreamUrl)) {
                return;
            }
            mMediaPlayer.setDataSource(mStreamUrl);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "loadMedia: ", e);
        }
        Log.d(TAG, "loadMedia: finished");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        initializeProgressCallback();
        Intent intent = AppUtils.formIntent("MEDIA_PLAYER_PREPARED");
        AppUtils.sendLocalBroadcast(getApplicationContext(), intent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopUpdatingCallbackWithPosition(true);
        Intent intent = AppUtils.formIntent("PLAYBACK_COMPLETED");
        AppUtils.sendLocalBroadcast(getApplicationContext(), intent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError: " + what);
        mp.reset();
        return false;
    }

    @Override
    public void setSongUrl(String mStreamUrl) {
        this.mStreamUrl = mStreamUrl;
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public int getPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
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
            startUpdatingCallbackWithPosition();
        }
    }

    @Override
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            stopUpdatingCallbackWithPosition(true);
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
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
            mSeekbarPositionUpdateTask = () -> updateProgressCallbackTask();
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
            if (resetUIPlaybackPosition) {
                Intent intent = AppUtils.formIntent("POSITION_CHANGED");
                intent.putExtra(INTENT_VALUE, 0);
                AppUtils.sendLocalBroadcast(getApplicationContext(), intent);
            }
        }
    }

    private void updateProgressCallbackTask() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            Intent intent = AppUtils.formIntent("POSITION_CHANGED");
            intent.putExtra(INTENT_VALUE, currentPosition);
            AppUtils.sendLocalBroadcast(getApplicationContext(), intent);
        }
    }

    @Override
    public void initializeProgressCallback() {
        Log.d(TAG, "initializeProgressCallback");
        final int duration = mMediaPlayer.getDuration();
        Intent intent = AppUtils.formIntent("DURATION_CHANGED");
        intent.putExtra(INTENT_VALUE, duration);
        AppUtils.sendLocalBroadcast(getApplicationContext(), intent);

        intent = AppUtils.formIntent("POSITION_CHANGED");
        intent.putExtra(INTENT_VALUE, 0);
        AppUtils.sendLocalBroadcast(getApplicationContext(), intent);
    }
}
