package com.pasumangkasa.freemusicdownloadtubity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pasumangkasa.freemusicdownloadtubity.executor.DBExecutorSupplier;
import com.pasumangkasa.freemusicdownloadtubity.setting.PasumSettingManager;
import com.pasumangkasa.freemusicdownloadtubity.task.PasumCallback;
import com.pasumangkasa.freemusicdownloadtubity.utils.ApplicationUtils;
import com.pasumangkasa.freemusicdownloadtubity.utils.DBLog;
import com.pasumangkasa.freemusicdownloadtubity.utils.IOUtils;
import com.pasumangkasa.freemusicdownloadtubity.view.CircularProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import com.onesignal.OneSignal;

import butterknife.BindView;

public class PasumSplashActivity extends PasumFragmentActivity {

    public static final String TAG = PasumSplashActivity.class.getSimpleName();
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1000;

    String asu;

    public static final int REQUEST_PERMISSION_CODE = 1001;
    public static final String[] REQUEST_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NOTIFICATION_POLICY};

    private Handler mHandler = new Handler();

    private boolean isLoading;

    @BindView(R.id.progressBar1)
    CircularProgressBar mProgressBar;

    TextView TextAds;

    private boolean isCheckGoogle;
    private GoogleApiAvailability googleAPI;

    static String PACKAGE_NAME;
    public static String id_inter;
    public static String id_banner;
    public static String active_ads = "";
    public static String admob_id;
    public static String sc_key;
    public static String link_move;
    public static String availability;
    static String json = "";

    public static String gambar_pop_up;
    public static String gambar_banner;
    public static String judul_pop_up;
    public static String deskripsi_up;
    public static String iklan_up;
    public static String package_pop_up;

    private Handler mHandlerAds = new Handler();
    private InterstitialAd interstitialAd;
    private AdView adView;
    private com.facebook.ads.AdView fbView;
    private com.facebook.ads.InterstitialAd interstitialFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_splash);

        TextAds = (TextView) findViewById(R.id.progressBarText);

        json = getResources().getString(R.string.json);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        new FetchData().execute();
        SystemClock.sleep(4500);
        PasumSettingManager.setOnline(this, true);
        DBLog.setDebug(DEBUG);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        if (PasumSplashActivity.active_ads.equals("fb")) {
            AudienceNetworkAds.initialize(this);
        } else {
            MobileAds.initialize(this);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class FetchData extends AsyncTask<Void, Void, Void> {
        String data = "";

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                URL url = new URL(json);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }
                try {
                    JSONObject json = new JSONObject(data);

                    JSONArray getParam = json.getJSONArray("appsdetail");
                    for (int i = 0; i < getParam.length(); i++) {
                        if (PACKAGE_NAME.equals(getParam.getJSONObject(i).getString("packagename"))) {

                            active_ads = getParam.getJSONObject(i).getString("ads_active");
                            availability = getParam.getJSONObject(i).getString("availability");
                            link_move = getParam.getJSONObject(i).getString("link_move");
                            sc_key = getParam.getJSONObject(i).getString("sc_key");
                            admob_id = getParam.getJSONObject(i).getString("admob_id");

                            if (active_ads.equals("fb")) {
                                id_inter = getParam.getJSONObject(i).getString("fb_inter");
                                id_banner = getParam.getJSONObject(i).getString("fb_banner");
                            } else {
                                id_inter = getParam.getJSONObject(i).getString("ad_inter");
                                id_banner = getParam.getJSONObject(i).getString("ad_banner");
                            }

                            iklan_up = getParam.getJSONObject(i).getString("iklan_up");
                            package_pop_up = getParam.getJSONObject(i).getString("package_pop_up");
                            judul_pop_up = getParam.getJSONObject(i).getString("tampil_judul");
                            deskripsi_up = getParam.getJSONObject(i).getString("tampil_deskripsi");
                            gambar_pop_up = getParam.getJSONObject(i).getString("tampil_icon");
                            gambar_banner = getParam.getJSONObject(i).getString("tampil_banner");

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isCheckGoogle) {
            isCheckGoogle = true;
            checkGooglePlayService();
        }
    }

    private void startLoad() {
        File mFile = mTotalMng.getDirectoryCached();
        if (mFile == null) {
            createFullDialog(-1, R.string.title_info, R.string.title_settings, R.string.title_cancel,
                    getString(R.string.info_error_sdcard), () -> {
                        isCheckGoogle = false;
                        startActivityForResult(new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS), 0);
                    }, () -> {
                        onDestroyData();
                        finish();
                    }).show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        TextAds.setVisibility(View.VISIBLE);

        boolean b = isNeedGrantPermission();
        if (!b) {
            startExecuteTask();
        }

    }

    private void startExecuteTask() {
        if (availability != null) {
            if (availability.equals("y")) {
                DBExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
                    mTotalMng.readConfigure(PasumSplashActivity.this);
                    mTotalMng.readGenreData(PasumSplashActivity.this);
                    mTotalMng.readCached(TYPE_FILTER_SAVED);
                    mTotalMng.readPlaylistCached();
                    mTotalMng.readLibraryTrack(PasumSplashActivity.this);
                    runOnUiThread(() -> goToMainActivity());
                });
            } else {
                MoveApps();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goToMainActivity() {
        showInterstitial(() -> {
            mProgressBar.setVisibility(View.INVISIBLE);
            Intent mIntent = new Intent(PasumSplashActivity.this, PasumMainActivity.class);
            startActivity(mIntent);
            finish();
        });
    }


    private void startInit() {
        if (!isLoading) {
            isLoading = true;
            mProgressBar.setVisibility(View.VISIBLE);
            startLoad();
        }
    }

    private void checkGooglePlayService() {
        googleAPI = GoogleApiAvailability.getInstance();
        try {
            int result = googleAPI.isGooglePlayServicesAvailable(this);
            if (result == ConnectionResult.SUCCESS) {
                startInit();
            } else {
                if (googleAPI.isUserResolvableError(result)) {
                    isCheckGoogle = false;
                    googleAPI.showErrorDialogFragment(this, result, REQUEST_GOOGLE_PLAY_SERVICES);
                } else {
                    showToast(googleAPI.getErrorString(result));
                    startInit();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isNeedGrantPermission() {
        try {
            if (IOUtils.hasMarsallow()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, REQUEST_PERMISSION_CODE);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == REQUEST_PERMISSION_CODE) {
                if (grantResults != null && grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startExecuteTask();
                } else {
                    showToast(R.string.info_permission_denied);
                    onDestroyData();
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.info_permission_denied);
            onDestroyData();
            finish();
        }

    }

    public void showInterstitial(final PasumCallback mCallback) {
        boolean b = SHOW_ADS;
        if (ApplicationUtils.isOnline(this) && b) {
            if (PasumSplashActivity.active_ads.equals("fb")) {
                interstitialFb = new com.facebook.ads.InterstitialAd(this, PasumSplashActivity.id_inter);
                AdSettings.addTestDevice("c661eaa1-29a0-487f-a9ad-10ca1ad1abe1");
                interstitialFb.setAdListener(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        // Interstitial ad displayed callback
//                        Log.e(TAG, "Interstitial ad displayed.");
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        // Interstitial dismissed callback
                        Intent i = new Intent(PasumSplashActivity.this, PasumMainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Ad error callback
                        Intent i = new Intent(PasumSplashActivity.this, PasumMainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        // Interstitial ad is loaded and ready to be displayed
//                        Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                        // Show the ad
                        interstitialFb.show();
                        mProgressBar.progressiveStop();
                        TextAds.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Ad clicked callback
//                        Log.d(TAG, "Interstitial ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Ad impression logged callback
//                        Log.d(TAG, "Interstitial ad impression logged!");
                    }
                });

                // For auto play video ads, it's recommended to load the ad
                // at least 30 seconds before it is shown
                interstitialFb.loadAd();

            } else {
                interstitialAd = new InterstitialAd(getApplicationContext());
                interstitialAd.setAdUnitId(ADMOB_INTERSTITIAL_ID);
                AdRequest adRequest = new AdRequest.Builder().addTestDevice(ADMOB_TEST_DEVICE).build();
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mProgressBar.progressiveStop();
                        TextAds.setVisibility(View.GONE);
                        mHandlerAds.removeCallbacksAndMessages(null);
                        try {
                            if (interstitialAd != null) {
                                interstitialAd.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onAdClosed() {
//                        super.onAdClosed();
//                        if (mCallback != null) {
//                            mCallback.onAction();
//                        }
                        mProgressBar.progressiveStop();
                        TextAds.setVisibility(View.GONE);
                        Intent i = new Intent(PasumSplashActivity.this, PasumMainActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
//                        super.onAdFailedToLoad(i);
//                        if (mCallback != null) {
//                            mCallback.onAction();
//                        }
                        mProgressBar.progressiveStop();
                        TextAds.setVisibility(View.GONE);
                        Intent i = new Intent(PasumSplashActivity.this, PasumMainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                interstitialAd.loadAd(adRequest);
                mHandlerAds.postDelayed(() -> {
//                    interstitialAd = null;
//                    if (mCallback != null) {
//                        mCallback.onAction();
//                    }
                    Intent i = new Intent(PasumSplashActivity.this, PasumMainActivity.class);
                    startActivity(i);
                    finish();
                }, TIME_OUT_LOAD_ADS);
            }
            return;
        }


        if (mCallback != null) {
            mCallback.onAction();
        }

    }




}
