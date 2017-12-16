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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

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

    public static int convertDpToPx(Context context, int dp) {
        return Math.round(
                dp
                        * (context.getResources().getDisplayMetrics().xdpi
                        / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void setImage(
            Context mContext, String imageUrl, int placeHolder, ImageView view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.width <= 0 || layoutParams.height <= 0) {
            return;
        }
        Picasso picasso =
                new Picasso.Builder(mContext)
                        .downloader(new OkHttp3Downloader(mContext))
                        .listener(
                                (picasso1, uri, exception) -> {
                                    Log.e(TAG, "setImage: ", exception);
                                })
                        .build();
        picasso.load(imageUrl)
                .placeholder(placeHolder)
                .resize(layoutParams.width, layoutParams.height)
                .noFade()
                .centerCrop()
                .into(view);
    }
}
