package musicplayer.krithghosh.com.musicplayer.repository;

import java.util.List;

import javax.inject.Inject;

import musicplayer.krithghosh.com.musicplayer.MusicPlayerApplication;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import musicplayer.krithghosh.com.musicplayer.utils.AppUtils;
import musicplayer.krithghosh.com.musicplayer.network.RestApiService;
import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class RemoteDataRepository {

    @Inject
    Retrofit retrofit;

    public RemoteDataRepository() {
        MusicPlayerApplication.getAppComponent().inject(this);
    }

    public Observable<List<SongMetadata>> getListOfSongs() {
        return retrofit.create(RestApiService.class).getSongMetadata(AppUtils.API_SONG_LIST);
    }
}