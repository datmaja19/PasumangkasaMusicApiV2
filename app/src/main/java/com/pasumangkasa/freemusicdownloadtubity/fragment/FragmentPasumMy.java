package com.pasumangkasa.freemusicdownloadtubity.fragment;

import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasumangkasa.freemusicdownloadtubity.PasumMainActivity;
import com.pasumangkasa.freemusicdownloadtubity.R;
import com.pasumangkasa.freemusicdownloadtubity.abtractclass.fragment.DBFragment;
import com.pasumangkasa.freemusicdownloadtubity.adapter.TrackAdapter;
import com.pasumangkasa.freemusicdownloadtubity.constants.PasumConstants;
import com.pasumangkasa.freemusicdownloadtubity.executor.DBExecutorSupplier;
import com.pasumangkasa.freemusicdownloadtubity.model.ConfigureModel;
import com.pasumangkasa.freemusicdownloadtubity.model.TrackModel;
import com.pasumangkasa.freemusicdownloadtubity.view.CircularProgressBar;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.freemusicdownloadtubity.com
 * @Project:MusicPlayer
 * @Date:Dec 25, 2014
 */
public class FragmentPasumMy extends DBFragment implements PasumConstants {

    public static final String TAG = FragmentPasumMy.class.getSimpleName();

    private PasumMainActivity mContext;

    @BindView(R.id.tv_no_result)
    TextView mTvNoResult;

    @BindView(R.id.list_datas)
    RecyclerView mRecyclerViewTrack;

    @BindView(R.id.progressBar1)
    CircularProgressBar mProgressBar;

    private ArrayList<TrackModel> mListTracks;

    private Handler mHandler = new Handler();

    private TrackAdapter mTrackAdapter;
    private boolean isSearching;
    private int mTypeUI;

    @Override
    public View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void findView() {
        mContext = (PasumMainActivity) getActivity();
        ConfigureModel configureModel= mContext.mTotalMng.getConfigureModel();
        mTypeUI=configureModel!=null?configureModel.getTypeMyMusic():TYPE_UI_LIST;
        if(mTypeUI==TYPE_UI_LIST){
            mContext.setUpRecyclerViewAsListView(mRecyclerViewTrack,null);
        }
        else{
            mContext.setUpRecyclerViewAsGridView(mRecyclerViewTrack,2);
        }
        if (isFirstInTab()) {
            startLoadData();
        }

    }

    @Override
    public void startLoadData() {
        super.startLoadData();
        if ((!isLoadingData() || isSearching) && mContext != null) {
            setLoadingData(true);
            isSearching = false;
            startGetData();
        }
    }

    private void startGetData() {
        mTvNoResult.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        DBExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            ArrayList<TrackModel> mTracks = mContext.mTotalMng.getListLibraryTrackObjects();
            if (mTracks == null) {
                mContext.mTotalMng.readLibraryTrack(mContext);
                mTracks = mContext.mTotalMng.getListLibraryTrackObjects();
            }
            final ArrayList<TrackModel> mListLibrary = mTracks!=null? (ArrayList<TrackModel>) mTracks.clone() :null;
            mContext.runOnUiThread(() -> {
                mProgressBar.setVisibility(View.GONE);
                setUpInfo(mListLibrary);
            });
        });
    }

    public void startSearchData(final String keyword) {
        if (mContext != null && mRecyclerViewTrack != null) {
            isSearching = true;
            DBExecutorSupplier.getInstance().forLightWeightBackgroundTasks().execute(() -> {
                final ArrayList<TrackModel> mTracks = mContext.mTotalMng.startSearchSong(keyword);
                mContext.runOnUiThread(() -> setUpInfo(mTracks));
            });
        }

    }


    private void setUpInfo(ArrayList<TrackModel> mListTracks) {
        mRecyclerViewTrack.setAdapter(null);
        if (this.mListTracks != null) {
            this.mListTracks.clear();
            this.mListTracks = null;
        }
        this.mListTracks = mListTracks;
        if (mListTracks != null && mListTracks.size() > 0) {
            mTrackAdapter = new TrackAdapter(mContext, mListTracks,mTypeUI);
            mRecyclerViewTrack.setAdapter(mTrackAdapter);
            mTrackAdapter.setOnTrackListener(new TrackAdapter.OnTrackListener() {
                @Override
                public void onListenTrack(TrackModel mTrackObject) {
                    mContext.hiddenKeyBoardForSearchView();
                    mContext.startPlayingList(mTrackObject, mContext.mTotalMng.getListLibraryTrackObjects());
                }

                @Override
                public void onShowMenu(View mView, TrackModel mTrackObject) {
                    mContext.showPopupMenu(mView, mTrackObject);
                }
            });
        }
        updateInfo();

    }


    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }


    private void updateInfo() {
        if (mTvNoResult != null) {
            boolean b = mListTracks != null && mListTracks.size() > 0;
            mTvNoResult.setVisibility(b ? View.GONE : View.VISIBLE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (mListTracks != null) {
            mListTracks.clear();
            mListTracks = null;
        }
    }

    @Override
    public void notifyData() {
        startGetData();
    }

}
