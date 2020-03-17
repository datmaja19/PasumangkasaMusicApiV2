package com.pasumangkasa.freemusicdownloadtubity.task;

import android.os.AsyncTask;


public class PasumTask extends AsyncTask<Void, Void, Void> {
	
	private PasumTaskListener mDownloadListener;
	
	public PasumTask(PasumTaskListener mDownloadListener) {
		this.mDownloadListener = mDownloadListener;
	}
	
	@Override
	protected void onPreExecute() {
		if(mDownloadListener!=null){
			mDownloadListener.onPreExcute();
		}
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if(mDownloadListener!=null){
			mDownloadListener.onDoInBackground();
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		if(mDownloadListener!=null){
			mDownloadListener.onPostExcute();
		}
	}

}
