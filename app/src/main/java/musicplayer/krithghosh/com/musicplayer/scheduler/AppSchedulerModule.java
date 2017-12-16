package musicplayer.krithghosh.com.musicplayer.scheduler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by kritarthaghosh on 16/12/17.
 */
@Module
public class AppSchedulerModule {

    @Provides
    @Singleton
    AppScheduler providesAppScheduler() {
        return new AppScheduler();
    }
}
