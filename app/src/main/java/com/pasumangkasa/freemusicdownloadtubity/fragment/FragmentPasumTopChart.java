package com.pasumangkasa.freemusicdownloadtubity.fragment;


import android.os.Bundle;
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
import com.pasumangkasa.freemusicdownloadtubity.constants.PasumSoundCloudConstants;
import com.pasumangkasa.freemusicdownloadtubity.dataMng.NetUtils;
import com.pasumangkasa.freemusicdownloadtubity.executor.DBExecutorSupplier;
import com.pasumangkasa.freemusicdownloadtubity.model.ConfigureModel;
import com.pasumangkasa.freemusicdownloadtubity.model.TrackModel;
import com.pasumangkasa.freemusicdownloadtubity.utils.ApplicationUtils;
import com.pasumangkasa.freemusicdownloadtubity.view.CircularProgressBar;

import java.util.ArrayList;

import butterknife.BindView;

public class FragmentPasumTopChart extends DBFragment implements PasumConstants {

    public static final String TAG = FragmentPasumTopChart.class.getSimpleName();

    private PasumMainActivity mContext;

    @BindView(R.id.tv_no_result)
    TextView mTvNoResult;

    @BindView(R.id.list_datas)
    RecyclerView mRecyclerViewTrack;

    @BindView(R.id.progressBar1)
    CircularProgressBar mProgressBar;

    private boolean isDestroy;

    private int mTypeUI;
    private TrackAdapter mTrackAdapter;
    private ArrayList<TrackModel> mListTracks;


    @Override
    public View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void findView() {
        mContext = (PasumMainActivity) getActivity();
        ConfigureModel configureModel= mContext.mTotalMng.getConfigureModel();
        mTypeUI=configureModel!=null?configureModel.getTypeTopChart():TYPE_UI_LIST;
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
        if(!isLoadingData() && mContext!=null){
            setLoadingData(true);
            startGetData(true);
        }
    }

    @Override
    public void onNetworkChange(boolean isNetworkOn) {
        super.onNetworkChange(isNetworkOn);
        if(isNetworkOn){
            if(mTrackAdapter==null && mContext!=null && mProgressBar!=null){
                startGetData(true);
            }
        }
    }


    private void startGetData(final boolean isNeedShowProgress) {
        if (!ApplicationUtils.isOnline(mContext)) {
            mProgressBar.setVisibility(View.GONE);
            mTvNoResult.setVisibility(View.VISIBLE);
            mTvNoResult.setText(R.string.info_lose_internet);
            return;
        }
        if (mProgressBar.getVisibility() == View.VISIBLE && !isNeedShowProgress) {
            return;
        }
        mTvNoResult.setText(R.string.title_no_songs);
        mProgressBar.setVisibility(View.VISIBLE);
        mTvNoResult.setVisibility(View.GONE);
        DBExecutorSupplier.getInstance().forBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                ConfigureModel mModel = mContext.mTotalMng.getConfigureModel();
                String genre= mModel!=null? mModel.getTopChartGenre(): PasumSoundCloudConstants.ALL_MUSIC_GENRE;
                String kind = mModel!=null? mModel.getTopChartKind(): PasumSoundCloudConstants.KIND_TOP;
                final ArrayList<TrackModel> mListFinalTrack = NetUtils.getListHotTrackObjectsInGenre(genre,kind,0,MAX_SONG_CACHED);
                mContext.runOnUiThread(() -> {
                    if(!isDestroy){
                        mProgressBar.setVisibility(View.GONE);
                        setUpInfo(mListFinalTrack);
                    }
                });
            }
        });
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
                    mContext.startPlayingList(mTrackObject, mListTracks);
                }

                @Override
                public void onShowMenu(View mView, TrackModel mTrackObject) {
                    mContext.showPopupMenu(mView, mTrackObject);
                }
            });
        }
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
        isDestroy=true;
        if(mListTracks!=null){
            mListTracks.clear();
            mListTracks=null;
        }
    }
}
