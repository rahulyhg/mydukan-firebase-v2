package org.app.mydukan.activities;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.data.Feed;
import org.app.mydukan.utils.AppContants;

import java.io.PrintWriter;
import java.io.StringWriter;

public class WebViewActivity extends BaseActivity {

    Feed wFeed =new Feed();
    private WebView wv1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        try{
            wv1=(WebView)findViewById(R.id.webview);
            Bundle mybundle = getIntent().getExtras();
            if (mybundle != null) {
                if (mybundle.containsKey(AppContants.VIEW_PROFILE)) {
                    wFeed = (Feed) mybundle.getSerializable(AppContants.VIEW_PROFILE);

                    if (wFeed != null) {
                        wv1.setVisibility(View.VISIBLE);
                        wv1.setWebViewClient(new MyBrowser());

                        WebSettings settings = wv1.getSettings();
                        settings.setMinimumFontSize(20);
                        settings.setBuiltInZoomControls(true);
                        settings.setUseWideViewPort(true);
                        settings.setJavaScriptEnabled(true);
                        settings.setSupportMultipleWindows(true);
                        settings.setJavaScriptCanOpenWindowsAutomatically(true);
                        settings.setLoadsImagesAutomatically(true);
                        settings.setLightTouchEnabled(true);
                        settings.setDomStorageEnabled(true);
                        settings.setLoadWithOverviewMode(true);
                        String url = wFeed.getLink();
                        ///  String url ="file:///android_asset/responssivepage.html";
                        wv1.getSettings().setLoadsImagesAutomatically(true);
                        wv1.getSettings().setJavaScriptEnabled(true);
                        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                        wv1.loadUrl(url);

                    } else {
                        Toast.makeText(WebViewActivity.this, "Error: Unable to Open the Link", Toast.LENGTH_SHORT).show();
                    }
                }
                if (mybundle.containsKey(AppContants.VIEW_PLATFORM)) {
                    String url_Platform = (String) mybundle.get(AppContants.VIEW_PLATFORM);

                    if (url_Platform != null) {
                        wv1.setVisibility(View.VISIBLE);
                        wv1.setWebViewClient(new MyBrowser());

                        WebSettings settings = wv1.getSettings();
                        settings.setMinimumFontSize(20);
                        settings.setBuiltInZoomControls(true);
                        settings.setUseWideViewPort(true);
                        settings.setJavaScriptEnabled(true);
                        settings.setSupportMultipleWindows(true);
                        settings.setJavaScriptCanOpenWindowsAutomatically(true);
                        settings.setLoadsImagesAutomatically(true);
                        settings.setLightTouchEnabled(true);
                        settings.setDomStorageEnabled(true);
                        settings.setLoadWithOverviewMode(true);

                        ///  String url ="file:///android_asset/responssivepage.html";
                        wv1.getSettings().setLoadsImagesAutomatically(true);
                        wv1.getSettings().setJavaScriptEnabled(true);
                        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                        wv1.loadUrl(url_Platform);

                    }
                    else {
                        Toast.makeText(WebViewActivity.this, "Error: Unable to Open the Link", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }catch (Exception e){
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() +" - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            Crashlytics.log(0,this.getClass().getSimpleName() +" - onCreate : ",errors.toString());
        }

    }
    @Override
    public void onBackPressed()
    {
        finish();
        // code here to show dialog
        // super.onBackPressed();
        //Intent intent = new Intent(this, MyNetworksActivity.class);
        //startActivity(intent);
        // optional depending on your needs
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
