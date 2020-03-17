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
import com.pasumangkasa.freemusicdownloadtubity.dataMng.NetUtils;
import com.pasumangkasa.freemusicdownloadtubity.executor.DBExecutorSupplier;
import com.pasumangkasa.freemusicdownloadtubity.model.ConfigureModel;
import com.pasumangkasa.freemusicdownloadtubity.model.TrackModel;
import com.pasumangkasa.freemusicdownloadtubity.utils.ApplicationUtils;
import com.pasumangkasa.freemusicdownloadtubity.utils.StringUtils;
import com.pasumangkasa.freemusicdownloadtubity.view.CircularProgressBar;

import java.util.ArrayList;

import butterknife.BindView;

public class FragmentPasumSearchTrack extends DBFragment implements PasumConstants {

    public static final String TAG = FragmentPasumSearchTrack.class.getSimpleName();

    private PasumMainActivity mContext;

    @BindView(R.id.list_datas)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_no_result)
    TextView mTvNoResult;

    @BindView(R.id.progressBar1)
    CircularProgressBar mProgressBar;

    private TrackAdapter mTrackAdapter;

    private ArrayList<TrackModel> mListTrackObjects;

    private String mKeyword;
    private boolean isDestroy;
    private int mTypeUI;

    @Override
    public View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void findView() {
        mContext = (PasumMainActivity) getActivity();
        ConfigureModel configureModel= mContext.mTotalMng.getConfigureModel();
        mTypeUI=configureModel!=null?configureModel.getTypeSearch():TYPE_UI_LIST;
        if(mTypeUI==TYPE_UI_LIST){
            mContext.setUpRecyclerViewAsListView(mRecyclerView,null);
        }
        else{
            mContext.setUpRecyclerViewAsGridView(mRecyclerView,2);
        }

        startSearch(mKeyword);


    }


    private void setUpInfo(final ArrayList<TrackModel> mListTrackObjects) {
        if (isDestroy) {
            return;
        }
        mRecyclerView.setAdapter(null);
        if (this.mListTrackObjects != null) {
            this.mListTrackObjects.clear();
            this.mListTrackObjects = null;
        }
        if (mListTrackObjects != null && mListTrackObjects.size() > 0) {
            this.mListTrackObjects = mListTrackObjects;
            mRecyclerView.setVisibility(View.VISIBLE);
            mTvNoResult.setVisibility(View.GONE);
            mTrackAdapter = new TrackAdapter(mContext, this.mListTrackObjects,mTypeUI);
            mRecyclerView.setAdapter(mTrackAdapter);
            mTrackAdapter.setOnTrackListener(new TrackAdapter.OnTrackListener() {
                @Override
                public void onListenTrack(TrackModel mTrackObject) {
                    mContext.startPlayingList(mTrackObject, (ArrayList<TrackModel>) mListTrackObjects.clone());
                }

                @Override
                public void onShowMenu(View mView, TrackModel mTrackObject) {
                    mContext.showPopupMenu(mView, mTrackObject);
                }
            });
        }
        else {
            mTvNoResult.setText(R.string.title_no_songs);
            mTvNoResult.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        if (mListTrackObjects != null) {
            mListTrackObjects.clear();
            mListTrackObjects = null;
        }

    }

    public void startSearch(String keyword) {
        if (StringUtils.isEmpty(keyword)) {
            mContext.showToast(R.string.info_empty);
            return;
        }
        if (!ApplicationUtils.isOnline(mContext)) {
            mContext.showToast(R.string.info_lose_internet);
            return;
        }
        this.mKeyword = keyword;
        this.mTvNoResult.setVisibility(View.GONE);
        showLoading(true);
        mRecyclerView.setVisibility(View.GONE);

        DBExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            ArrayList<TrackModel> mListTrack = NetUtils.getListTrackObjectsByQuery(StringUtils.urlEncodeString(mKeyword), 0, MAX_SEARCH_SONG);
            mContext.runOnUiThread(() -> {
                showLoading(false);
                mContext.dimissProgressDialog();
                if (mListTrack == null) {
                    mContext.showToast(R.string.info_server_error);
                    return;
                }
                setUpInfo(mListTrack);
            });
        });
    }

    public void showLoading(boolean b) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void notifyData() {
        if (mTrackAdapter != null) {
            mTrackAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onExtractData() {
        super.onExtractData();
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            mKeyword = mBundle.getString(KEY_BONUS);
        }
    }

    @Override
    public boolean isCheckBack() {
        if ((mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE)) {
            return true;
        }
        return false;
    }
}
