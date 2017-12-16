package musicplayer.krithghosh.com.musicplayer.scheduler;

import rx.Scheduler;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public interface IScheduler {

    Scheduler mainThread();

    Scheduler backgroundThread();
}
