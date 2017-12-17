package musicplayer.krithghosh.com.musicplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    public static final String BROADCAST_MEDIA_ACTIONS = "broadcast_media_actions";
    public static final String INTENT_ACTIONS = "intent_actions";
    public static final String INTENT_VALUE = "intent_value";
    public static final String INTENT_POSITION_VALUE = "intent_position_value";
    public static final String API_SONG_LIST = "studio";
    public static final String SONG_STATE_PLAY = "play";
    public static final String SONG_STATE_PAUSE = "pause";
    public static final String SONG_STATE_RESET = "reset";
    public static final String SONG_STATE_FORWARD = "forward";

    public static boolean isThereInternetConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static Intent formIntent(String action) {
        Intent intent = new Intent(BROADCAST_MEDIA_ACTIONS);
        intent.putExtra(INTENT_ACTIONS, action);
        return intent;
    }

    public static void sendLocalBroadcast(Context mContext, Intent intent) {
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
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
