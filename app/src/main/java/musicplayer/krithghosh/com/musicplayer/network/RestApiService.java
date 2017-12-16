package musicplayer.krithghosh.com.musicplayer.network;

import java.util.List;

import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public interface RestApiService {

    @GET
    Observable<List<SongMetadata>> getSongMetadata(@Url String url);
}
