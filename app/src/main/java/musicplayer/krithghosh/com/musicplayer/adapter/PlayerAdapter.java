package musicplayer.krithghosh.com.musicplayer.adapter;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public interface PlayerAdapter {

    void initializePlaySong();

    void release();

    int getPosition();

    int getDuration();

    boolean isPlaying();

    void play();

    void reset();

    void pause();

    void initializeProgressCallback();

    void seekTo(int position);

    void setSongUrl(String streamUrl);
}