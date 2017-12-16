package musicplayer.krithghosh.com.musicplayer.repository;

import java.util.List;

import javax.inject.Inject;

import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import rx.Observable;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class DataRepository {

    private final RemoteDataRepository remoteDataRepository;

    @Inject
    public DataRepository(RemoteDataRepository remoteDataRepository) {
        this.remoteDataRepository = remoteDataRepository;
    }

    public Observable<List<SongMetadata>> getListOfSongs() {
        return remoteDataRepository.getListOfSongs();
    }
}
