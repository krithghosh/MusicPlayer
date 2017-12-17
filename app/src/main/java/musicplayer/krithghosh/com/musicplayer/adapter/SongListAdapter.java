package musicplayer.krithghosh.com.musicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import musicplayer.krithghosh.com.musicplayer.R;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import musicplayer.krithghosh.com.musicplayer.utils.AppUtils;
import musicplayer.krithghosh.com.musicplayer.utils.ImageUtils;

/**
 * Created by kritarthaghosh on 16/12/17.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongListViewHolder> {
    private static final String TAG = "SongListAdapter";

    Context mContext;
    List<SongMetadata> mSongList;
    private SongListAdapter.OnItemClickListener mItemClickListener;

    public SongListAdapter(Context mContext, List<SongMetadata> mSongList) {
        this.mContext = mContext;
        this.mSongList = mSongList;
    }

    @Override
    public SongListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(mContext).inflate(R.layout.card_view, parent, false);
        return new SongListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongListViewHolder holder, int position) {
        if (position == 0) {
            holder.setExtraTopMargin();
        }
        SongMetadata item = mSongList.get(position);
        if (!TextUtils.isEmpty(item.getSong())) {
            holder.songName.setText(item.getSong());
        }
        if (!TextUtils.isEmpty(item.getArtists())) {
            holder.artistName.setText(item.getArtists());
        }
        if (!TextUtils.isEmpty(item.getCoverImage())) {
            ImageUtils.setImage(mContext, item.getCoverImage(), R.drawable.ic_music, holder.songImage);
        }
    }

    @Override
    public int getItemCount() {
        if (mSongList == null || mSongList.size() == 0) return 0;
        return mSongList.size();
    }

    public void updateList(List<SongMetadata> mSongList) {
        this.mSongList = mSongList;
        notifyDataSetChanged();
    }

    public void clearList() {
        this.mSongList.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, SongMetadata songMetadata);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class SongListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.iv_song)
        ImageView songImage;

        @BindView(R.id.tv_song)
        TextView songName;

        @BindView(R.id.tv_artist)
        TextView artistName;

        View itemView;

        public SongListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mItemClickListener.onItemClick(view, getAdapterPosition(), mSongList.get(getAdapterPosition()));
        }

        public void setExtraTopMargin() {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int top = (int) mContext.getResources().getDimension(R.dimen.card_padding_half);
            int bottom = (int) mContext.getResources().getDimension(R.dimen.card_padding_quarter);
            layoutParams.setMargins(0, top, 0, bottom);
            itemView.setLayoutParams(layoutParams);
        }
    }
}
