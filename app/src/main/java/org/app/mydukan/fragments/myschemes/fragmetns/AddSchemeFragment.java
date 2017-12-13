package org.app.mydukan.fragments.myschemes.fragmetns;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.app.mydukan.R;

import org.app.mydukan.data.Product;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.DatePickerFragment;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by rojesharunkumar on 15/11/17.
 */

public class AddSchemeFragment extends BaseFragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener,
        AdapterView.OnItemSelectedListener {

    private MySchemesActivity mActivity;
    private String title;
    private AutoCompleteTextView mCategory, mTxtSchemeTitle, mTxtSchemeDescription;
    private TextView mTxtStartDate, mTxtEndDate;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private Button btnCaptureScheme, btnAddScheme;
    private ImageView imgSchemeDoc;
    private boolean isStart = true;
    private Spinner mSpnrCategory;
    private Spinner mSpnrModel;
    private HashMap<String, ArrayList<Product>> mProductList;
    ArrayList<Product> mModelsList;
    private String[] models;
    private ArrayAdapter<String> mCategoryAdapter;
    private ArrayAdapter<String> mModelAdapter;
    private String[] category;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MySchemesActivity) context;
        mActivity.showProgress();
    }

    public static AddSchemeFragment newInstance(String category) {

        Bundle args = new Bundle();
        args.putString("_category", category);
        AddSchemeFragment fragment = new AddSchemeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getArguments() != null) {
            title = getArguments().getString("_category");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_scheme, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCategory = (AutoCompleteTextView) view.findViewById(R.id.lbl_category);
        mTxtSchemeTitle = (AutoCompleteTextView) view.findViewById(R.id.edt_scheme_title);
        mTxtSchemeDescription = (AutoCompleteTextView) view.findViewById(R.id.edt_scheme_desc);
        mTxtStartDate = (TextView) view.findViewById(R.id.txt_start_date);
        mTxtEndDate = (TextView) view.findViewById(R.id.txt_end_date);
        btnAddScheme = (Button) view.findViewById(R.id.btn_addscheme);
        btnCaptureScheme = (Button) view.findViewById(R.id.btn_capture_doc);
        imgSchemeDoc = (ImageView) view.findViewById(R.id.img_doc);
        mSpnrCategory = (Spinner) view.findViewById(R.id.sprn_category);
        mSpnrModel = (Spinner) view.findViewById(R.id.sprn_model);
        String[] category = {"- Select Cateogry -"};
        setAdapter(category);


        mTxtStartDate.setOnClickListener(this);
        mTxtEndDate.setOnClickListener(this);
        btnAddScheme.setOnClickListener(this);
        btnCaptureScheme.setOnClickListener(this);

        if (!TextUtils.isEmpty(title)) {
            mCategory.setText(title.trim());
        }

        getProductList();

    }


    private void getProductList() {

        try {
            ApiManager.getInstance(getActivity()).getSupplierProductList(MySelectedSchemesHelper.getInstance().getCurrentSupplier(),
                    MySelectedSchemesHelper.getInstance().getCategoryId(title),
                    null, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            mProductList = (HashMap<String, ArrayList<Product>>) data;
                            if (mProductList != null && mProductList.size() > 0) {
                                category = new String[mProductList.size() + 1];
                                category[0] = "- Select Category -";
                                int i = 1;
                                for (String key : mProductList.keySet()) {
                                    category[i++] = key;
                                }
                            }
                            setAdapter(category);
                            mActivity.dismissProgress();

                        }

                        @Override
                        public void onFailure(String response) {

                        }
                    });
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_start_date:
                showDatePickerDialog();
                isStart = true;
                break;
            case R.id.txt_end_date:
                isStart = false;
                showDatePickerDialog();
                break;
            case R.id.btn_addscheme:
                break;
            case R.id.btn_capture_doc:
                dispatchTakePictureIntent();
                break;
        }

    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
                setPic();
            }
        }
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = imgSchemeDoc.getWidth();
        int targetH = imgSchemeDoc.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        imgSchemeDoc.setImageBitmap(bitmap);
        imgSchemeDoc.setVisibility(View.VISIBLE);
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void showDatePickerDialog() {

        DialogFragment newFragment = new DatePickerFragment();
        ((DatePickerFragment) newFragment).setCallBack((DatePickerDialog.OnDateSetListener) this);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if (isStart) {
            mTxtStartDate.setText(dayOfMonth + "/" + month + "/" + year);
        } else {
            mTxtEndDate.setText(dayOfMonth + "/" + month + "/" + year);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sprn_category:
                if(category != null)
                getModelsList(category[position]);
                break;
            case R.id.sprn_model:

                break;
        }

    }

    private void getModelsList(String category) {
        if(mProductList != null && mProductList.size() >0 && !category.equalsIgnoreCase("- Select category -")) {
            mModelsList = mProductList.get(category);
            if(mModelsList != null && mModelsList.size() >0)
                mSpnrModel.setVisibility(View.VISIBLE);
            models = new String[mModelsList.size()];
            for (int i = 0; i < mModelsList.size(); i++) {
                models[i] = mModelsList.get(i).getName();
            }
            if (models != null && models.length > 0) {
                setModelAdapter(models);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setAdapter(String[] category) {
        mCategoryAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, category);
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrCategory.setOnItemSelectedListener(this);
        mSpnrCategory.setAdapter(mCategoryAdapter);
    }

    public void setModelAdapter(String[] models) {
        mModelAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, models);
        mModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrModel.setOnItemSelectedListener(this);
        mSpnrModel.setAdapter(mModelAdapter);
    }
}
