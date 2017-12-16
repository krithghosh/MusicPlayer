package musicplayer.krithghosh.com.musicplayer;

import android.app.Application;

import musicplayer.krithghosh.com.musicplayer.component.ApplicationComponent;
import musicplayer.krithghosh.com.musicplayer.component.DaggerApplicationComponent;
import musicplayer.krithghosh.com.musicplayer.module.ApplicationModule;
import musicplayer.krithghosh.com.musicplayer.network.NetworkModule;
import musicplayer.krithghosh.com.musicplayer.repository.RepositoryModule;
import musicplayer.krithghosh.com.musicplayer.scheduler.AppSchedulerModule;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class MusicPlayerApplication extends Application {

    protected static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplicationComponent();
    }

    private void initApplicationComponent() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .appSchedulerModule(new AppSchedulerModule())
                .networkModule(new NetworkModule(BuildConfig.BASE_URL))
                .repositoryModule(new RepositoryModule())
                .build();
    }

    public static ApplicationComponent getAppComponent() {
        return applicationComponent;
    }
}
