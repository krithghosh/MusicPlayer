package musicplayer.krithghosh.com.musicplayer.view.contract;

import android.content.Context;

import java.util.List;

import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public interface SongsListContract {
    interface View {
        void showError(String msg);

        void showLoader();

        void hideLoader();

        void injectModules();

        void showSongsList(List<SongMetadata> songsList);

        void showNoInternetConnection(String msg);
    }

    interface Presenter {

        void unSubscribeSongsListCall();

        void getSongsList(Context mContext);

        void setView(SongsListContract.View mView);
    }
}
