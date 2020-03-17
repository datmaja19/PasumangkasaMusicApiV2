package com.pasumangkasa.freemusicdownloadtubity.playservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.pasumangkasa.freemusicdownloadtubity.dataMng.MusicDataMng;
import com.pasumangkasa.freemusicdownloadtubity.dataMng.TotalDataManager;
import com.pasumangkasa.freemusicdownloadtubity.model.TrackModel;
import com.pasumangkasa.freemusicdownloadtubity.setting.PasumSettingManager;
import com.pasumangkasa.freemusicdownloadtubity.utils.StringUtils;

import java.util.ArrayList;




public class PasumMusicIntentReceiver extends BroadcastReceiver implements PasumMusicConstant {

	public static final String TAG = PasumMusicIntentReceiver.class.getSimpleName();
	private ArrayList<TrackModel> mListTrack;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}
		String action = intent.getAction();
		if (StringUtils.isEmpty(action)) {
			return;
		}
		mListTrack = MusicDataMng.getInstance().getListPlayingTrackObjects();
		String packageName = context.getPackageName();
		if (action.equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
			startService(context, ACTION_PAUSE);
		}
		else if (action.equals(packageName + ACTION_NEXT)) {
			startService(context, ACTION_NEXT);
		}
		else if (action.equals(packageName + ACTION_TOGGLE_PLAYBACK)) {
			startService(context, ACTION_TOGGLE_PLAYBACK);
		}
		else if (action.equals(packageName + ACTION_PREVIOUS)) {
			startService(context, ACTION_PREVIOUS);
		}
		else if (action.equals(packageName + ACTION_STOP)) {
			startService(context, ACTION_STOP);
			if(!PasumSettingManager.getOnline(context)){
				try{
					MusicDataMng.getInstance().onDestroy();
					TotalDataManager.getInstance().onDestroy();
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		else if (action.equals(packageName + ACTION_SHUFFLE)) {
			startService(context, ACTION_SHUFFLE, !PasumSettingManager.getShuffle(context));
		}
		else if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
			if(mListTrack==null || mListTrack.size()==0){
				MusicDataMng.getInstance().onDestroy();
				startService(context, ACTION_STOP);
				return;
			}
			KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
			if (keyEvent.getAction() != KeyEvent.ACTION_DOWN){
				return;
			}
			switch (keyEvent.getKeyCode()) {
				case KeyEvent.KEYCODE_HEADSETHOOK:
				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
					if(PasumSettingManager.getOnline(context)){
						startService(context, ACTION_TOGGLE_PLAYBACK);
					}
					else{
						startService(context, ACTION_STOP);
					}
					break;
				case KeyEvent.KEYCODE_MEDIA_PLAY:
					startService(context, ACTION_PLAY);
					break;
				case KeyEvent.KEYCODE_MEDIA_PAUSE:
					if(PasumSettingManager.getOnline(context)){
						startService(context, ACTION_PAUSE);
					}
					else{
						startService(context, ACTION_STOP);
					}
					break;
				case KeyEvent.KEYCODE_MEDIA_STOP:
					startService(context, ACTION_STOP);
					break;
				case KeyEvent.KEYCODE_MEDIA_NEXT:
					startService(context, ACTION_NEXT);
					break;
				case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					startService(context,ACTION_PREVIOUS);
					break;
			}
		}
	}
	private void startService(Context context, String action){
		Intent mIntent1= new Intent(context, PasumService.class);
		mIntent1.setAction(context.getPackageName() +action);
		context.startService(mIntent1);
	}
	private void startService(Context context, String action, boolean value){
		Intent mIntent1= new Intent(context, PasumService.class);
		mIntent1.setAction(context.getPackageName() +action);
		mIntent1.putExtra(KEY_VALUE, value);
		context.startService(mIntent1);
	}
}
