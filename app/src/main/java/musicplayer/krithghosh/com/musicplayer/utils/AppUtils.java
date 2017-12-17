package musicplayer.krithghosh.com.musicplayer.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    public static final String API_SONG_LIST = "studio";
    public static final String SONG_STATE_PLAY = "play";
    public static final String SONG_STATE_PAUSE = "pause";
    public static final String SONG_STATE_RESET = "reset";

    public static boolean isThereInternetConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static void addFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull Fragment fragment,
            int frameId,
            boolean shouldAddToBackStack,
            String fragmentTag) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment, fragmentTag);
        if (shouldAddToBackStack) {
            transaction.addToBackStack(fragmentTag);
        }
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragment(
            @NonNull FragmentManager fragmentManager,
            @NonNull Fragment fragment,
            int frameId,
            boolean shouldAddToBackStack,
            String fragmentTag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment, fragmentTag);
        if (shouldAddToBackStack) {
            transaction.addToBackStack(fragmentTag);
        }
        transaction.commitAllowingStateLoss();
    }

    public static Drawable getDrawableResource(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
