package com.example.bbb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class FullscreenActivity extends Activity {
	private WebView mWebView;
	int ani_count = 0;
	boolean isWifiNetConnect = false;
	private ImageButton button;
	private ImageButton button1;
	private ImageButton button2;
	private String DefURI = "file://android-assets/index.html";
	private String goURI;
	private FrameLayout floading;
	
    //网络状态״̬
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive( Context context, Intent intent ) {
	        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
	        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();   
	        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        if (!isWifiNetConnect && (wifiInfo!=null && wifiInfo.isConnected() == true || mobNetInfo != null && mobNetInfo.isConnected() == true)){
	        	mWebView.loadUrl(goURI);
	        	Toast.makeText( context, "网络连接，加载数据("+activeNetInfo.getTypeName()+")", Toast.LENGTH_SHORT ).show();
	        	isWifiNetConnect = true; 
	        }else{
	        	Toast.makeText( context, "网络断开，请开启", Toast.LENGTH_SHORT ).show();
	        	isWifiNetConnect = false;
	        }
	    }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_fullscreen);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		goURI = DefURI;
		new Handler().postDelayed(new Runnable(){
			public void run(){
				iniActivity();
			}
		},2500);
		
	}
	
	public void iniActivity(){
		FrameLayout webParent = (FrameLayout) findViewById(R.id.webParent);
		webParent.setVisibility(View.VISIBLE);
		
		FrameLayout fl = (FrameLayout) findViewById(R.id.Welcome);
		fl.setVisibility(View.GONE);		
		//网络状态̬
		IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);

        floading = (FrameLayout) findViewById(R.id.floading);
        
        button = (ImageButton) findViewById(R.id.imageButton1);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWebView.reload();
			}
		});
        button.setVisibility(View.GONE);
        button1 = (ImageButton) findViewById(R.id.imageButton2);
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWebView.goForward();
			}
		});
        button1.setVisibility(View.GONE);
        button2 = (ImageButton) findViewById(R.id.imageButton3);
        button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWebView.goBack();
			}
		});
        button2.setVisibility(View.GONE);
        
		mWebView = (WebView) findViewById(R.id.webView1);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setAppCacheMaxSize(1024*1024*8);
		String appCacheDir = this.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setAllowFileAccess(true);
		webSettings.setAppCacheEnabled(true);
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); 
		mWebView.requestFocus();
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);
		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		
		mWebView.setWebViewClient(new WebViewClient(){
			String aboutblank = "Page NO FOUND";
			
			public void onReceivedError(WebView view, int errorCode,  
			    String description, String failingUrl) {
				goURI = view.getUrl();
				view.stopLoading(); 
			    view.loadUrl("javascript:document.body.innerHTML=\"" + aboutblank + "\"");
			    String info = "未知错误";
				if(errorCode == -2) info = "网络断开，请开启";
				Toast.makeText(FullscreenActivity.this, info+"("+errorCode+")", Toast.LENGTH_SHORT).show();
			}
			
			public boolean shouldOverrideUrlLoading(WebView view, String url)  
			 {
				 goURI = url;
				 view.loadUrl(goURI);
			     return true;
			 }
		});
		
		mWebView.setWebChromeClient(new WebChromeClient(){
			
	    	public void onProgressChanged(WebView view, int newProgress) {
		
				if(newProgress==100){
					button.setVisibility(View.VISIBLE);
					button1.setVisibility(View.VISIBLE);
					button2.setVisibility(View.VISIBLE);
					floading.setVisibility(View.GONE);
				}else{
					floading.setVisibility(View.VISIBLE);
					button.setVisibility(View.GONE);
					button1.setVisibility(View.GONE);
					button2.setVisibility(View.GONE);
				}
			}
		
		});
		mWebView.loadUrl(goURI);
	}
	
	int back_count = 2;
	@Override
    //后退
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
        	if (goURI != DefURI)
        		mWebView.goBack();
        	else{
        		back_count -=1;
    	        if (back_count >0){
    	            Toast.makeText(this, "再按一次将退出", Toast.LENGTH_SHORT).show();
    	            return false;
                }else{
                	android.os.Process.killProcess(android.os.Process.myPid());
                	return false;
                }
        	}
            return true; 
        }else if((keyCode == KeyEvent.KEYCODE_BACK) && !mWebView.canGoBack()){
            back_count -=1;
	        if (back_count >0){
	            Toast.makeText(this, "再按一次将退出", Toast.LENGTH_SHORT).show();
	            return false;
            }else{
            	android.os.Process.killProcess(android.os.Process.myPid());
            	return false;
            }
        }
        return false;
	}
	
}
