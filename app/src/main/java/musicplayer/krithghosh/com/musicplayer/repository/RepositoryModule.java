package musicplayer.krithghosh.com.musicplayer.repository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kritarthaghosh on 16/12/17.
 */
@Module
public class RepositoryModule {

    @Provides
    @Singleton
    RemoteDataRepository providesRemoteDataRepository() {
        return new RemoteDataRepository();
    }
}
