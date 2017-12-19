package org.app.mydukan.activities.Schemes;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.activities.BaseActivity;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.data.SchemeInfo;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.data.SupplierInfo;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.NetworkUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;

/**
 * Created by arpithadudi on 9/11/16.
 */
public class SchemeDetailsActivity extends BaseActivity {

    //UI reference
    private TextView mSchemeNameView;
    private TextView mSupplierNameView;
    private TextView mStartDateView;
    private TextView mEndDateView;
    private TextView mDescTextView;
    private Switch mEnrolledBtn;
    private Button mDetailsBtn;
    private WebView mDescWebView;
    private ProgressDialog mProgress;

    //Variables
    private MyDukan mApp;
    private Scheme mScheme;
    private SchemeRecord mSchemeRecord;
    private String mSupplierId;
    private String mSupplierName;
    private RelativeLayout webLayout;
    private Button fullpage, normalpage, downloadpage;
    private LinearLayout linear_scheme_layout;
    private NetworkUtil networkUtil;
    // Progress Dialog
    private ProgressDialog cDialog;
    private static final int STORAGE_PERMISSION_CODE = 555;
    private static String file_url = "https://s3-ap-southeast-1.amazonaws.com/mydukan/A+NOVEMBER+UPDATES/1509290822price_list+(1).xls";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemedetails);

        mApp = (MyDukan) getApplicationContext();
        networkUtil =new NetworkUtil();
        //Set up the actionbar
        setupActionBar();

        //get the initial data
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
                mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
            }

            if (bundle.containsKey(AppContants.SCHEME)) {
                mScheme = (Scheme) bundle.getSerializable(AppContants.SCHEME);
            }

            if (bundle.containsKey(AppContants.SUPPLIER_NAME)) {
                mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
            }
        }

        mSchemeNameView = (TextView) findViewById(R.id.schemeName);
        mSupplierNameView = (TextView) findViewById(R.id.supplierName);
        mStartDateView = (TextView) findViewById(R.id.startDate);
        mEndDateView = (TextView) findViewById(R.id.endDate);
        mDescTextView = (TextView) findViewById(R.id.descTextView);
        mDescWebView = (WebView) findViewById(R.id.descWebView);
        mEnrolledBtn = (Switch) findViewById(R.id.enrolledBtn);
        mDetailsBtn = (Button) findViewById(R.id.detailsBtn);
        webLayout =(RelativeLayout) findViewById(R.id.weblayout);
        linear_scheme_layout = (LinearLayout) findViewById(R.id.linear_scheme_layout);

        fullpage= (Button) findViewById(R.id.btn_FullPage);
        normalpage= (Button) findViewById(R.id.btn_NormalPage);
        downloadpage= (Button) findViewById(R.id.btn_DownloadPage);

        downloadpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // starting new Async Task
                if(networkUtil.isConnectingToInternet(SchemeDetailsActivity.this)){
                    new  DownloadFileFromURL().execute(file_url);
                }else{
                    Toast.makeText(SchemeDetailsActivity.this, "Please check network connectivity.", Toast.LENGTH_LONG).show();
                }

            }
        });
        mEnrolledBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (mSchemeRecord != null && mSchemeRecord.getEnrolled() != null && mSchemeRecord.getEnrolled() != isChecked) {
                    mSchemeRecord.setEnrolled(isChecked);
                    addSchemeRecord(mSchemeRecord);
                }
            }
        });


        fullpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linear_scheme_layout.setVisibility(View.GONE);
                normalpage.setVisibility(View.VISIBLE);
                fullpage.setVisibility(View.GONE);
            }
        });

        normalpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linear_scheme_layout.setVisibility(View.VISIBLE);
                fullpage.setVisibility(View.VISIBLE);
                normalpage.setVisibility(View.GONE);

            }
        });

        mDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SchemeDetailsActivity.this, AddSchemeRecordActivity.class);
                intent.putExtra(AppContants.SCHEMERECORD, mSchemeRecord);
                startActivity(intent);
            }
        });


        mDescWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                /*Intent intent = new Intent(SchemeDetailsActivity.this, WebViewActivity.class);
                intent.putExtra(AppContants.VIEW_WEBVIEW, "xyz");
                startActivity(intent);*/

             /*
                WebView webview = new WebView(SchemeDetailsActivity.this);
                webview.getSettings().setLoadWithOverviewMode(true);
                webview.getSettings().setUseWideViewPort(false);
                webview.getSettings().setSupportZoom(false);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.setBackgroundColor(Color.TRANSPARENT);
                webview.loadUrl("https://s3-ap-southeast-1.amazonaws.com/mydukan/BANNERS/My+Dukan+Banner+4.jpeg");

                RelativeLayout.LayoutParams paramsWebView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                Dialog dialog = new Dialog(SchemeDetailsActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                dialog.addContentView(webview, paramsWebView);
                dialog.show();
                */


            }
        });

        setSchemeData();
    }
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(SchemeDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(SchemeDetailsActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            requestStoragePermission();
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(SchemeDetailsActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast

                //  Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(SchemeDetailsActivity.this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.schemesdetails_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setSchemeData() {
        showProgress();
        //Scheme details
        mSchemeNameView.setText(mScheme.getName().toUpperCase());
        mStartDateView.setText("Validity " + String.valueOf(mApp.getUtils().dateFormatter(mScheme.getStartdate(), "dd-MM-yy")));
        mEndDateView.setText(String.valueOf(mApp.getUtils().dateFormatter(mScheme.getEnddate(), "dd-MM-yy")));
        setupDescriptionView();

        //Supplier details
        mSupplierNameView.setText(mSupplierName.toUpperCase());

        getSchemeRecordInfo();
    }

    private void setupDescriptionView() {
        mDescWebView.getSettings().setJavaScriptEnabled(true);
        mDescWebView.setWebViewClient(new MyWebViewClient());
        mDescWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mDescWebView.getSettings().setBuiltInZoomControls(true);
        mDescWebView.getSettings().setDisplayZoomControls(false);
        // mDescWebView.setHorizontalScrollbarOverlay(true);
        mDescWebView.setScrollbarFadingEnabled(true);
        mDescWebView.getSettings().setLoadsImagesAutomatically(true);

        mDescWebView.setWebViewClient(new WebViewClient());
        mDescWebView.setClickable(true);
        mDescWebView.setWebChromeClient(new WebChromeClient());

        mDescWebView.getSettings().setLoadWithOverviewMode(true);
        mDescWebView.getSettings().setUseWideViewPort(true);

   /*     wv1.setVisibility(View.VISIBLE);
        wv1.setWebViewClient(new PrivacyPolicyActivity.MyBrowser());
        String url ="https://s3-ap-southeast-1.amazonaws.com/mydukan/images/Mydukan/Privacy+policy.html";

    mDescWebView.getSettings().setLoadsImagesAutomatically(true);
        mDescWebView.getSettings().setJavaScriptEnabled(true);
        mDescWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mDescWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mDescWebView.getSettings().setBuiltInZoomControls(true);
        mDescWebView.getSettings().setDisplayZoomControls(false);
        mDescWebView.setHorizontalScrollbarOverlay(true);
        mDescWebView.setScrollbarFadingEnabled(true);
        mDescWebView.getSettings().setLoadsImagesAutomatically(true);
        wv1.loadUrl(url);
        */

        String schemeDesc = "";
        String url = mScheme.getUrl();
        String desc = mScheme.getDescription();
        if (!mApp.getUtils().isStringEmpty(url)) {
            schemeDesc = url;
            file_url=url;
        } else if (mApp.getUtils().isStringEmpty(desc)) {
            schemeDesc = getString(R.string.Specifications_Not_Available);
        }

        if (schemeDesc.contains("https://") || schemeDesc.contains("http://")) {
            // String webURL_Responcieve= changedHeaderHtml(schemeDesc);
            mDescWebView.loadUrl(schemeDesc);
        } else {
            mDescWebView.loadData(schemeDesc, "text/html; charset=UTF-8", null);
        }

        if (!mApp.getUtils().isStringEmpty(desc)) {
            fullpage.setVisibility(View.GONE);
            downloadpage.setVisibility(View.GONE);
            mDescTextView.setText(desc);
        }
    }

    public static String changedHeaderHtml(String htmlText) {

        String head = "<head><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" /></head>";

        String closedTag = "</body></html>";
        String changeFontHtml = head + htmlText + closedTag;
        return changeFontHtml;
    }

    private void getSchemeRecordInfo() {
        try {
            ApiManager.getInstance(SchemeDetailsActivity.this).getSchemeRecord(mScheme.getSchemeId(),
                    mSupplierId, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            dismissProgress();
                            if (data != null) {
                                mSchemeRecord = (SchemeRecord) data;
                            } else {
                                mSchemeRecord = new SchemeRecord();
                                SupplierInfo info = new SupplierInfo();
                                info.setId(mSupplierId);
                                info.setName(mSupplierName);
                                mSchemeRecord.setSupplierinfo(info);

                                SchemeInfo schemeInfo = new SchemeInfo();
                                schemeInfo.setId(mScheme.getSchemeId());
                                schemeInfo.setName(mScheme.getName());
                                mSchemeRecord.setSchemeinfo(schemeInfo);
                            }

                            if (mSchemeRecord.getEnrolled()) {
                                mEnrolledBtn.setChecked(true);
                                mDetailsBtn.setEnabled(true);
                            } else {
                                mEnrolledBtn.setChecked(false);
                                mDetailsBtn.setEnabled(false);
                            }
                        }

                        @Override
                        public void onFailure(String response) {

                        }
                    });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - getSchemeRecordInfo : ",e.toString());
            Crashlytics.log(0,"Exception - SchemeDetailsActivity - getSchemeRecordInfo : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - getSchemeRecordInfo : ",ex.toString());
            Crashlytics.log(0,"1 - SchemeDetailsActivity - getSchemeRecordInfo : ",ex.toString());
        }
    }

    private void addSchemeRecord(SchemeRecord record) {
        try {
            showProgress();

            ApiManager.getInstance(SchemeDetailsActivity.this).addSchemeRecord(record, new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    String result = (String) data;
                    dismissProgress();
                    if (!result.equalsIgnoreCase(getString(R.string.status_success))) {
                        showErrorToast(SchemeDetailsActivity.this, result);
                        mSchemeRecord.setEnrolled(!mSchemeRecord.getEnrolled());
                        mEnrolledBtn.setChecked(!mSchemeRecord.getEnrolled());
                    } else {
                        if (mSchemeRecord.getEnrolled()) {
                            mDetailsBtn.setEnabled(true);
                        } else {
                            mDetailsBtn.setEnabled(false);
                        }
                    }
                }

                @Override
                public void onFailure(String response) {

                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - addSchemeRecord : ",e.toString());
            Crashlytics.log(0,"Exception - SchemeDetailsActivity - addSchemeRecord : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - addSchemeRecord : ",ex.toString());
            Crashlytics.log(0,"1 - SchemeDetailsActivity - addSchemeRecord : ",ex.toString());
        }

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgress = new ProgressDialog(SchemeDetailsActivity.this);
            mProgress.setTitle("Please wait");
            mProgress.setMessage("Page is loading..");
            mProgress.setCancelable(false);
            mProgress.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Showing Dialog
             * */
            cDialog = new ProgressDialog(SchemeDetailsActivity.this);
            cDialog.setMessage("Downloading file. Please wait...");
            cDialog.setIndeterminate(false);
            cDialog.setMax(100);
            cDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            cDialog.setCancelable(true);
            cDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            if (new CheckForSDCard().isSDCardPresent()) {
                try{
                    Uri uri = Uri.parse(f_url[0]);
                    Log.e("Check", uri.toString());
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    Random rand = new Random();
                    int randomNum = rand.nextInt(50) + 1;
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MyDukan_"+ mScheme.getName()+"_"+randomNum+".csv");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                    request.allowScanningByMediaScanner();// if you want to be available from media players
                    DownloadManager manager = (DownloadManager)  SchemeDetailsActivity.this.getSystemService(DOWNLOAD_SERVICE);
                    manager.enqueue(request);

                }catch (Exception e){
                    e.printStackTrace();
                    Handler mHandler = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message message) {
                            // This is where you do your work in the UI thread.
                            // Your worker tells you in the message what to do.
                            Toast.makeText(SchemeDetailsActivity.this, "Sorry, Unable to download the file in your device. ", Toast.LENGTH_LONG).show();
                        }

                    };

                }
                return null;
            }else{

                Toast.makeText(SchemeDetailsActivity.this, "SD-CARD is not present in your device. ", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            cDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            cDialog.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
           // String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";

        }

    }

    public class CheckForSDCard {
        //Check If SD Card is present or not method
        public boolean isSDCardPresent() {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return true;
            }
            return false;
        }
    }

}
