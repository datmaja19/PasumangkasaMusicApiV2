package com.pasumangkasa.freemusicdownloadtubity.dataMng;

import com.pasumangkasa.freemusicdownloadtubity.constants.PasumConstants;
import com.pasumangkasa.freemusicdownloadtubity.constants.PasumSoundCloudConstants;
import com.pasumangkasa.freemusicdownloadtubity.model.TrackModel;
import com.pasumangkasa.freemusicdownloadtubity.utils.DBLog;
import com.pasumangkasa.freemusicdownloadtubity.utils.DownloadUtils;
import com.pasumangkasa.freemusicdownloadtubity.utils.StringUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.pasumangkasa.freemusicdownloadtubity.PasumSplashActivity.sc_key;

/**
 * 
 * 
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.pasumangkasa.com
 * @Project:MusicPlayer
 * @Date:Jan 3, 2015
 * 
 */
public class NetUtils implements PasumConstants, PasumSoundCloudConstants {

	public static final String TAG = NetUtils.class.getSimpleName();


	public static String getRedirectAppUrl(String urlOriginal) {
		if (StringUtils.isEmpty(urlOriginal)) {
			return null;
		}
		URL u = null;
		try {
			u = new URL(urlOriginal);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setInstanceFollowRedirects(false);
			huc.setReadTimeout(5000);
			boolean redirect = false;
			int status = huc.getResponseCode();
			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
					redirect = true;
				}
			}
			if (redirect) {
				String newUrl = huc.getHeaderField("Location");
				return newUrl;
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<TrackModel> getListTrackObjectsByGenre(String genre, int offset, int limit){
		StringBuilder mStringBuilder = new StringBuilder();
		mStringBuilder.append(URL_API);
		mStringBuilder.append(METHOD_TRACKS);
		mStringBuilder.append(JSON_PREFIX);
		String mPrefixClientId = String.format(FORMAT_CLIENT_ID, sc_key);
		mStringBuilder.append(mPrefixClientId);
		mStringBuilder.append(String.format(FILTER_GENRE, genre));
		mStringBuilder.append(String.format(OFFSET, String.valueOf(offset),String.valueOf(limit)));

		String url = mStringBuilder.toString();
		DBLog.d(TAG, "==============>getListTrackObjectsByGenre="+url);
		InputStream data = DownloadUtils.download(url);
		if(data!=null){
			return JsonParsingUtils.parsingListTrackObjects(data);
		}
		return null;
	}

	public static ArrayList<TrackModel> getListTrackObjectsByQuery(String query, int offset, int limit){
		StringBuilder mStringBuilder = new StringBuilder();
		mStringBuilder.append(URL_API);
		mStringBuilder.append(METHOD_TRACKS);
		mStringBuilder.append(JSON_PREFIX);
		String mPrefixClientId = String.format(FORMAT_CLIENT_ID, sc_key);
		mStringBuilder.append(mPrefixClientId);
		mStringBuilder.append(String.format(FILTER_QUERY, query));
		mStringBuilder.append(String.format(OFFSET, String.valueOf(offset),String.valueOf(limit)));

		String url = mStringBuilder.toString();
		DBLog.d(TAG, "==============>getListTrackObjectsByQuery="+url);
		InputStream data = DownloadUtils.download(url);
		if(data!=null){
			return JsonParsingUtils.parsingListTrackObjects(data);
		}
		return null;
	}

	public static String getLinkStreamFromSoundCloud(long id) {
		final String manualUrl = String.format(FORMAT_URL_SONG, String.valueOf(id), sc_key);
		String redirectUrl = getRedirectAppUrl(manualUrl);
		if(!StringUtils.isEmpty(redirectUrl)){
			return redirectUrl;
		}
		String dataServer = DownloadUtils.downloadString(manualUrl);
		String finalUrl = null;
		if (!StringUtils.isEmpty(dataServer)) {
			try {
				JSONObject mJsonObject = new JSONObject(dataServer);
				finalUrl = "http://fando.id/soncloud.php?id=38560282";
			}
			catch (Exception e) {
				e.printStackTrace();
				finalUrl = "http://fando.id/soncloud.php?id=38560282";
			}
		}
		return finalUrl;
	}

	public static ArrayList<TrackModel> getListHotTrackObjectsInGenre(String genre, int offset, int limit){
		return getListHotTrackObjectsInGenre(genre,KIND_TOP,offset,limit);
	}

	public static ArrayList<TrackModel> getListHotTrackObjectsInGenre(String genre, String kind, int offset, int limit){
		StringBuilder mStringBuilder = new StringBuilder();
		mStringBuilder.append(URL_API_V2);
		mStringBuilder.append(METHOD_CHARTS);
		mStringBuilder.append(String.format(PARAMS_KIND,kind));
		String mPrefixClientId = String.format(PARAMS_NEW_CLIENT_ID, sc_key);
		mStringBuilder.append(mPrefixClientId);
		mStringBuilder.append(String.format(PARAMS_GENRES, genre));
		mStringBuilder.append(String.format(PARAMS_OFFSET, String.valueOf(offset),String.valueOf(limit)));
		mStringBuilder.append(PARAMS_LINKED_PARTITION);

		String url = mStringBuilder.toString();
		DBLog.d(TAG, "==============>getListHotTrackObjectsInGenre="+url);
		InputStream data = DownloadUtils.download(url);
		if(data!=null){
			ArrayList<TrackModel> mListDatas = JsonParsingUtils.parsingListHotTrackObjects(data);
			return mListDatas;
		}
		return null;
	}


}
