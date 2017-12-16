package musicplayer.krithghosh.com.musicplayer.view.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import musicplayer.krithghosh.com.musicplayer.R;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import musicplayer.krithghosh.com.musicplayer.utils.AppUtils;
import musicplayer.krithghosh.com.musicplayer.view.fragment.SongPlayerFragment;
import musicplayer.krithghosh.com.musicplayer.view.fragment.SongsListFragment;

public class HomeActivity extends AppCompatActivity implements SongsListFragment.SongsListEventListener,
        SongPlayerFragment.SongPlayerFragmentInteractionListener {

    @BindView(R.id.ll_parent_layout)
    LinearLayout llParentLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.content_frame)
    FrameLayout contentFrame;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Snackbar snackbar;
    private Toast toast;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setFragmentManager();
        setupToolbar();
        setupToastAndProgressDialog();
        setupFragment();
    }

    public int getLayoutId() {
        return R.layout.activity_home;
    }

    private void setFragmentManager() {
        fragmentManager = getSupportFragmentManager();
    }

    private void setupToastAndProgressDialog() {
        toast = Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT);
        snackbar = Snackbar.make(llParentLayout, "", Snackbar.LENGTH_INDEFINITE);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.music_player_heading);
    }

    private void setupFragment() {
        Bundle bundle = new Bundle();
        SongsListFragment fragment = SongsListFragment.newInstance(bundle);
        AppUtils.addFragment(fragmentManager,
                fragment, R.id.content_frame,
                true,
                SongsListFragment.class.getName());
    }

    @Override
    public void showError(String msg) {
        if (!toast.getView().isShown()) {
            toast.setText(msg);
            toast.show();
        }
    }

    @Override
    public void showSnackBar(String msg) {
        if (!snackbar.isShownOrQueued()) {
            snackbar.setText(msg).show();
        }
    }

    @Override
    public void showLoader() {
        if (!progressBar.isShown()) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoader() {
        if (!progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        int backStackCount = fragmentManager.getBackStackEntryCount();
        if (backStackCount == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void showPlayer(SongMetadata songMetadata) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SongPlayerFragment.BUNDLE_SONG_METADATA, songMetadata);
        SongPlayerFragment fragment = SongPlayerFragment.newInstance(bundle);
        AppUtils.addFragment(fragmentManager,
                fragment,
                R.id.content_frame,
                true,
                SongPlayerFragment.class.getName());
    }
}