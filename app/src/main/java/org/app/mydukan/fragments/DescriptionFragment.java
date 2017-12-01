package org.app.mydukan.fragments;



import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.app.mydukan.R;

import org.app.mydukan.activities.ProductDescriptionActivity;

import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.NetworkUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;


import static android.content.Context.DOWNLOAD_SERVICE;
import static org.app.mydukan.activities.ProductDescriptionActivity.fullpage;
import static org.app.mydukan.activities.ProductDescriptionActivity.mApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescriptionFragment extends Fragment {

    View mView;
    Context context;
    private Product mProduct;


    private String mProductDesc;
    private SupplierBindData mSupplier;

    private TextView mNameTextView;
    private TextView mPriceTextView;
    private TextView mOthersHeaderView;
    private TextView mOthersTextView;
    private TextView mDescTextView;
    private TextView mStockTextView;
    LinearLayout addTocart_Btn;
    private WebView mDescWebView;
    private ProgressDialog mProgress;
    Product product;
    boolean isCartShow=false;
    private Button btn_DownloadProductPage;
    // Progress Dialog
    private ProgressDialog pDialog;
    private NetworkUtil networkUtil;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    // File url to download
   // private static String file_url = "https://api.androidhive.info/progressdialog/hive.jpg";

    ProductDescriptionActivity productDescriptionActivity;
    private static String file_url = "https://s3-ap-southeast-1.amazonaws.com/mydukan/A+NOVEMBER+UPDATES/1509290822price_list+(1).xls";
    private static final int STORAGE_PERMISSION_CODE = 555;

    public DescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_description, container, false);
        context = mView.getContext();
        networkUtil =new NetworkUtil();
        requestStoragePermission();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            try {
                mProduct = (Product) extras.getSerializable(AppContants.PRODUCT);
                mSupplier = (SupplierBindData) extras.getSerializable(AppContants.SUPPLIER);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        mDescWebView = (WebView) mView.findViewById(R.id.descWebView);
        mDescTextView = (TextView) mView.findViewById(R.id.descTextView);

        btn_DownloadProductPage = (Button) mView.findViewById(R.id.btn_DownloadProductPage);
        btn_DownloadProductPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // starting new Async Task
                if(networkUtil.isConnectingToInternet(getActivity())){
                    new DownloadFileFromURL().execute(file_url);
                }else{
                    Toast.makeText(getActivity(), "Please check network connectivity.", Toast.LENGTH_LONG).show();
                }

            }

        });
//        setupDescription();
        fetchProductAndShow();
//        fullpage.setVisibility(View.VISIBLE);
        return mView;

    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            requestStoragePermission();
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
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
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void showProgress() {

        try {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
        } catch (Exception e) {

        }
    }

    public void dismissProgress() {
        try {
            if ( mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = null;
        } catch (Exception e) {

        }
    }

    private void fetchProductAndShow(){
        showProgress();
        ApiManager.getInstance(context).getProductDetails(mProduct.getProductId(),
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        product = (Product)data;
                        if(product != null) {
                            mProduct.setDescription(product.getDescription());
                            mProduct.setUrl(product.getUrl());
                            mProduct.setAttributes(product.getAttributes());
                            fullpage.setVisibility(View.VISIBLE);
                        }
                        dismissProgress();
                        setupDescription();
                    }

                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        setupDescription();
                    }
                });
    }

    private void setupDescription(){
        String url = mProduct.getUrl();
        String desc = mProduct.getDescription();
        if(!mApp.getUtils().isStringEmpty(url)){
            mProductDesc = url;
            file_url=url;
        } else if(mApp.getUtils().isStringEmpty(desc)){
            if(getActivity()!=null){
                mProductDesc =getActivity().getResources().getString( R.string.Specifications_Not_Available1 );
                btn_DownloadProductPage.setVisibility(View.GONE);
                fullpage.setVisibility(View.GONE);
            }else{
                mProductDesc="Not_Available";
                btn_DownloadProductPage.setVisibility(View.GONE);
                fullpage.setVisibility(View.GONE);

            }
        }

        mDescWebView.getSettings().setJavaScriptEnabled(true);
        mDescWebView.setWebViewClient(new MyWebViewClient());
        mDescWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mDescWebView.getSettings().setBuiltInZoomControls(true);
        mDescWebView.getSettings().setDisplayZoomControls(false);
        mDescWebView.setHorizontalScrollbarOverlay(true);
        mDescWebView.setScrollbarFadingEnabled(true);
        mDescWebView.getSettings().setLoadsImagesAutomatically(true);

        if(!mApp.getUtils().isStringEmpty(mProductDesc)) {
            if (mProductDesc.contains("https://") || mProductDesc.contains("http://")) {
                mDescWebView.loadUrl(mProductDesc);
            } else {
                mDescWebView.loadData(mProductDesc, "text/html; charset=UTF-8", null);
            }
        }

        if(!mApp.getUtils().isStringEmpty(desc)){
            mDescTextView.setText(desc);
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
            mProgress = new ProgressDialog(context);
          //  mProgress.setTitle(getString(R.string.Please_wait));
            mProgress.setMessage(getString(R.string.Page_is_loading));
            mProgress.setCancelable(true);
            mProgress.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mProgress != null) {
                {
                    mProgress.dismiss();
                }
                mProgress = null;
            }
        }
    }


    /**
     * Showing Dialog
     * */
 /*   @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }*/

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

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
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
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MyDukan_"+mProduct.getName()+"_"+randomNum+".csv");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
                    request.allowScanningByMediaScanner();// if you want to be available from media players
                    DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Sorry, Unable to download the file in your device. ", Toast.LENGTH_LONG).show();
                }

                return null;
            }else{

                Toast.makeText(getActivity(), "SD-CARD is not present in your device. ", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";

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
