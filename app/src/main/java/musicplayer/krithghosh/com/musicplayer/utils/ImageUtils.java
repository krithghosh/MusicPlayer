package musicplayer.krithghosh.com.musicplayer.utils;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

/**
 * Created by kritarthaghosh on 17/12/17.
 */

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    private static Picasso picassoInstance;

    public static Picasso getPicassoInstance(Context mContext) {
        if (picassoInstance == null) {
            picassoInstance =
                    new Picasso.Builder(mContext)
                            .downloader(new OkHttp3Downloader(mContext))
                            .listener(
                                    (picasso1, uri, exception) -> {
                                        Log.e(TAG, "setImage: ", exception);
                                    })
                            .build();
        }
        return picassoInstance;
    }

    public static void setImage(
            Context mContext, String imageUrl, int placeHolder, ImageView view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.width <= 0 || layoutParams.height <= 0) {
            return;
        }
        getPicassoInstance(mContext).load(imageUrl)
                .placeholder(placeHolder)
                .resize(layoutParams.width, layoutParams.height)
                .noFade()
                .centerCrop()
                .into(view);
    }
}
