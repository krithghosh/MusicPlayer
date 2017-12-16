package musicplayer.krithghosh.com.musicplayer.adapter;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public interface PlayerAdapter {

    void loadMedia(String url);

    void release();

    boolean isPlaying();

    void play();

    void reset();

    void pause();

    void initializeProgressCallback();

    void seekTo(int position);
}