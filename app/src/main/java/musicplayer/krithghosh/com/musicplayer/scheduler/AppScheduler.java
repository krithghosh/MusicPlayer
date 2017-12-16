package musicplayer.krithghosh.com.musicplayer.scheduler;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class AppScheduler implements IScheduler {

    @Override
    public Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public Scheduler backgroundThread() {
        return Schedulers.io();
    }
}