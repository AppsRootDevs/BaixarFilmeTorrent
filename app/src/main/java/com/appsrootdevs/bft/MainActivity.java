package com.appsrootdevs.bft;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import com.appsrootdevs.bft.*;
import java.io.*;
import java.net.*;
import java.util.*;
import com.google.android.gms.ads.*;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{

	final private String url_load = "https://www.baixarfilmetorrent.com/";
	final private String url_fail = "file:///android_asset/pag/fail.html";
	final private String url_offline = "file:///android_asset/pag/offline.html";
	final private String url_welcome = "file:///android_asset/pag/welcome.html";

	private SwipeRefreshLayout swipeRefreshLayout;
    private WebView mWebView;
    private CoordinatorLayout coordinatorLayout;
	private boolean mValue;
	private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
		openDialog();

		// Initialize the Mobile Ads SDK.
		MobileAds.initialize(this, getString(R.string.app_ad_unit_id));
        adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.build();
        adView.loadAd(adRequest);

		mWebView = (WebView) findViewById(R.id.mainWebView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
		registerForContextMenu(coordinatorLayout);
		mWebView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon)
				{
					super.onPageStarted(view, url, favicon);
					swipeRefreshLayout.setRefreshing(true);
				}
				public void onPageFinished(WebView view, String url)
				{
					swipeRefreshLayout.setRefreshing(false);
				}
			});

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        mWebView.setWebViewClient(new Callback());
        loadWebsite();
	}
	private void loadWebsite()
	{
        ConnectivityManager cm = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
		{
			mWebView.loadUrl(url_load);
        }
		else
		{
            Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.please_check_your_internet_connection), Snackbar.LENGTH_LONG);
			snackbar.setDuration(6000);
			snackbar.show();
			mWebView.loadUrl(url_offline);
            swipeRefreshLayout.setRefreshing(false);
		}
    }


	public class Callback extends WebViewClient
	{
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			mWebView.loadUrl(url_fail);
			Toast.makeText(getBaseContext(), getString(R.string.fail_on_loading_of_page), Toast.LENGTH_SHORT).show();
        }
        public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
            if (url.startsWith("magnet"))
			{
				Intent magnet = new Intent(Intent.ACTION_VIEW);
				magnet.setData(Uri.parse(url));
				try
				{
					startActivity(magnet);
				}
				catch (ActivityNotFoundException e)
				{
					Toast.makeText(getBaseContext(), getString(R.string.no_bittorent_client_installed_on_the_system), Toast.LENGTH_SHORT).show();
				}
				return true;
            }
			else
			{
                view.loadUrl(url);
                return true;
            }
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
            swipeRefreshLayout.setRefreshing(true);
        }


        public void onPageFinished(WebView view, String url)
		{
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onRefresh()
	{
		loadWebsite();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event)
	{
        if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
            switch (keyCode)
			{
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack())
					{
                        mWebView.goBack();
                    }
					else
					{
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
	private void openDialog()
	{
        mValue = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("mValue", true);

        if (mValue)
		{
            AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
			WebView mWebViewDialog = new WebView(this);
			mWebViewDialog.loadUrl(url_welcome);
			mWebViewDialog.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url)
					{
						view.loadUrl(url);

						return true;
					}
				});

            mAlertDialog.setView(mWebViewDialog);
			mAlertDialog.setCancelable(false);
            mAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.dismiss();
					}
				});
            mAlertDialog.show();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("mValue", false).commit();
        }
    }
	/** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
