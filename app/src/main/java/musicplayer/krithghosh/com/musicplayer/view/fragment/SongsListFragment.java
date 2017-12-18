package musicplayer.krithghosh.com.musicplayer.view.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import musicplayer.krithghosh.com.musicplayer.MusicPlayerApplication;
import musicplayer.krithghosh.com.musicplayer.R;
import musicplayer.krithghosh.com.musicplayer.adapter.SongListAdapter;
import musicplayer.krithghosh.com.musicplayer.model.SongMetadata;
import musicplayer.krithghosh.com.musicplayer.presenter.SongsListPresenter;
import musicplayer.krithghosh.com.musicplayer.utils.RxSearch;
import musicplayer.krithghosh.com.musicplayer.view.contract.SongsListContract;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SongsListFragment extends Fragment implements SongsListContract.View,
        TextWatcher,
        View.OnFocusChangeListener,
        View.OnClickListener {
    private static final String TAG = "SongsListFragment";

    @Inject
    SongsListPresenter mPresenter;

    @BindView(R.id.ll_parent_layout)
    LinearLayout parentLayout;

    @BindView(R.id.et_search)
    AppCompatEditText etSearch;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private SongListAdapter mAdapter;
    private List<SongMetadata> mDataList;
    private static final int DEB_TIME = 300;
    private LinearLayoutManager mLayoutManager;
    private SongsListEventListener mEventListener;

    public interface SongsListEventListener {
        void showError(String msg);

        void showSnackBar(String msg);

        void showLoader();

        void hideLoader();

        void updateSongsList(List<SongMetadata> mSongsList);

        void showPlayer(SongMetadata songMetadata, int position);
    }

    public static SongsListFragment newInstance(Bundle bundle) {
        SongsListFragment fragment = new SongsListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectModules();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mEventListener = (SongsListEventListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_songs, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupSearchListener();
        setupSearchView();
        setupListAdapter();
        setupRecyclerView();
        loadSongsList();
    }

    private void loadSongsList() {
        mEventListener.showLoader();
        mPresenter.getSongsList(getContext());
    }

    @Override
    public void showError(String msg) {
        mEventListener.hideLoader();
        mEventListener.showError(msg);
    }

    @Override
    public void showLoader() {
        mEventListener.showLoader();
    }

    @Override
    public void hideLoader() {
        mEventListener.hideLoader();
    }

    @Override
    public void injectModules() {
        MusicPlayerApplication.getAppComponent().inject(this);
        mPresenter.setView(this);
    }

    @Override
    public void showSongsList(List<SongMetadata> songsList) {
        mPresenter.unSubscribeSongsListCall();
        if (songsList == null || songsList.size() < 1) {
            mEventListener.updateSongsList(new LinkedList<>());
            mAdapter.updateList(songsList);
            showError(getString(R.string.error_fetch_songs));
            return;
        }
        if (etSearch.getVisibility() != View.VISIBLE) {
            etSearch.setVisibility(View.VISIBLE);
        }
        mEventListener.updateSongsList(songsList);
        mAdapter.updateList(songsList);
    }

    private void setupSearchListener() {
        etSearch.clearFocus();
        RxSearch.fromEditText(etSearch)
                .debounce(DEB_TIME, TimeUnit.MILLISECONDS)
                .filter(item -> item.length() >= 2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mAdapter.clearList();
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(String searchKey) {
                        mPresenter.getFilteredList(getContext(), searchKey);
                    }
                });
        etSearch.setOnClickListener(this);
        parentLayout.setOnClickListener(this);
    }

    public void setupListAdapter() {
        if (mDataList == null) {
            mDataList = new LinkedList<>();
        }
        mAdapter = new SongListAdapter(getContext(), mDataList);
        mAdapter.setOnItemClickListener((view, position, songMetadata) -> {
            Log.d(TAG, "setOnItemClickListener: " + songMetadata.getSong());
            mEventListener.showPlayer(songMetadata, position);
        });
    }

    private void setupSearchView() {
        VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(
                getResources(), R.drawable.ic_search, null);
        etSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableCompat, null, null, null);
        etSearch.addTextChangedListener(this);
        etSearch.setOnFocusChangeListener(this);
    }

    public void setupRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_parent_layout:
                etSearch.clearFocus();
                break;

            case R.id.recycler_view:
                etSearch.clearFocus();
                break;
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() == 0) {
            mPresenter.getFilteredList(getContext(), "");
            etSearch.clearFocus();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideSoftKeyboard(v);
        }
    }

    @Override
    public void showNoInternetConnection(String msg) {
        mEventListener.showSnackBar(msg);
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
