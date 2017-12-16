package musicplayer.krithghosh.com.musicplayer.component;

import javax.inject.Singleton;

import dagger.Component;
import musicplayer.krithghosh.com.musicplayer.scheduler.AppScheduler;
import musicplayer.krithghosh.com.musicplayer.scheduler.AppSchedulerModule;
import musicplayer.krithghosh.com.musicplayer.module.ApplicationModule;
import musicplayer.krithghosh.com.musicplayer.view.fragment.SongsListFragment;
import musicplayer.krithghosh.com.musicplayer.network.NetworkModule;
import musicplayer.krithghosh.com.musicplayer.repository.DataRepository;
import musicplayer.krithghosh.com.musicplayer.repository.RemoteDataRepository;
import musicplayer.krithghosh.com.musicplayer.repository.RepositoryModule;
import retrofit2.Retrofit;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                NetworkModule.class,
                AppSchedulerModule.class,
                RepositoryModule.class
        }
)
public interface ApplicationComponent {

    AppScheduler scheduler();

    Retrofit retrofit();

    DataRepository dataRepository();

    void inject(RemoteDataRepository remoteDataRepository);

    void inject(SongsListFragment songsListFragment);
}
