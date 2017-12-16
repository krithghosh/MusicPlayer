package musicplayer.krithghosh.com.musicplayer.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import musicplayer.krithghosh.com.musicplayer.R;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import musicplayer.krithghosh.com.musicplayer.repository.DataRepository;
import musicplayer.krithghosh.com.musicplayer.scheduler.AppScheduler;
import musicplayer.krithghosh.com.musicplayer.utils.AppUtils;
import musicplayer.krithghosh.com.musicplayer.view.contract.SongsListContract;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class SongsListPresenter implements SongsListContract.Presenter {
    private static final String TAG = "SongsListPresenter";

    private Gson gson;
    private AppScheduler mIScheduler;
    private SongsListContract.View mView;
    private DataRepository mDataRepository;
    private Subscription mSongsSubscription;
    private List<SongMetadata> mSongsList;

    @Inject
    public SongsListPresenter(DataRepository mDataRepository, AppScheduler mIScheduler) {
        gson = new Gson();
        this.mIScheduler = mIScheduler;
        this.mDataRepository = mDataRepository;
    }

    @Override
    public void setView(SongsListContract.View mView) {
        this.mView = mView;
    }

    public void getFilteredList(Context context, String searchKey) {
        if (mSongsList == null || mSongsList.size() < 1) {
            mView.showError(context.getString(R.string.search_error));
            return;
        }
        if (TextUtils.isEmpty(searchKey)) {
            mView.showSongsList(mSongsList);
            return;
        }
        List<SongMetadata> filteredList = new LinkedList<>();
        for (SongMetadata item : mSongsList) {
            if (!TextUtils.isEmpty(item.getSong()) && item.getSong().toLowerCase().startsWith(searchKey)) {
                filteredList.add(item);
            }
        }
        mView.showSongsList(filteredList);
    }

    @Override
    public void unSubscribeSongsListCall() {
        if (!(mSongsSubscription == null || mSongsSubscription.isUnsubscribed())) {
            mSongsSubscription.unsubscribe();
        }
    }

    @Override
    public void getSongsList(Context mContext) {
        if (!AppUtils.isThereInternetConnection(mContext)) {
            mView.showNoInternetConnection(mContext.getString(R.string.no_internet));
            return;
        }

        mView.showLoader();
        mSongsSubscription = mDataRepository.getListOfSongs().subscribeOn(mIScheduler.backgroundThread())
                .observeOn(mIScheduler.mainThread())
                .subscribe(new Subscriber<List<SongMetadata>>() {
                    List<SongMetadata> mDataList = null;

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                        mView.hideLoader();
                        mSongsList = mDataList;
                        mView.showSongsList(mDataList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        mView.showError(mContext.getString(R.string.error_fetch_songs));
                    }

                    @Override
                    public void onNext(List<SongMetadata> mDataList) {
                        this.mDataList = mDataList;
                    }
                });
    }
}
