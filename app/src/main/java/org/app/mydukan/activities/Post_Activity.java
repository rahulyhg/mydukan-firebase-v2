package org.app.mydukan.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.app.mydukan.R;
import org.app.mydukan.adapters.CircleTransform;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.User;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.FeedUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.app.mydukan.activities.CommentsActivity.RESULT_DELETED;
import static org.app.mydukan.fragments.MyNetworkFragment.FEED_LOCATION;

/**
 * Created by rajat on 27-11-2017.
 */

public class Post_Activity extends BaseActivity{
    private static final String TAG = "MY_PROFILE";
    private Bitmap bitmap;
    private Uri filePath;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private int PICK_IMAGE_REQUEST = 1;
    private static final String IMAGE_DIRECTORY = "/mydukan";
    String imgString=null;
    String feedText="";
    String feedLink="";
    EditText et_linkText,et_feedtext;
    private MyDukan mApp;
    ImageView uploadImgBtn,uploadLink,profileIMG;
    Button addPost;
    ImageView addedImageView,cancel;
    AdView mAdView;
    int linkClickCount=0;
    public static final String FEED_ROOT = "feed";
    public static final int GET_PHOTO = 13;
    RelativeLayout addImageLayout;

    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_dark);
        mApp = (MyDukan) getApplicationContext();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        et_feedtext=(EditText)findViewById(R.id.editTextprofile_1);
        et_linkText=(EditText)findViewById(R.id.et_Link_1);
        profileIMG=(ImageView)findViewById(R.id.profile_IMG_1);
        addedImageView=(ImageView)findViewById(R.id.img_addedImg_1);
        addImageLayout=(RelativeLayout)findViewById(R.id.img_layout);
        cancel=(ImageView)findViewById(R.id.btn_Cancel);
        InitProfileView();
        requestStoragePermission();
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(null, addedImageView);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage(imgString);
                imgString=null;
                addImageLayout.setVisibility(View.GONE);
            }
        });
        uploadImgBtn=(ImageView)findViewById(R.id.cameraBtn_1);
        uploadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();

            }
        });
        uploadLink=(ImageView)findViewById(R.id.linkBtn_1);
        uploadLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(linkClickCount==0) {
                    et_linkText.setVisibility(View.VISIBLE);
                    et_linkText.requestFocus();
                    linkClickCount++;
                }else {
                    et_linkText.setText("");
                    et_feedtext.requestFocus();
                    et_linkText.setVisibility(View.GONE);
                    linkClickCount=0;
                }
            }
        });

        addPost = (Button) findViewById(R.id.btn_post_1);
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Post","in on click");
                feedText = et_feedtext.getText().toString();
                feedLink = et_linkText.getText().toString();

                if(!feedLink.isEmpty()){
                    boolean isLink= hyperLink(feedLink);
                    if(!isLink){
                        showOkAlert(Post_Activity.this,"Link Error", "Please add the proper Link","ok");

                    }else {


                        if (mApp.getUtils().isStringEmpty(feedText) && (imgString == null)) {
                            Toast.makeText(Post_Activity.this, "Please add something to post.", Toast.LENGTH_LONG).show();
                        }
                        if (mApp.getUtils().isStringEmpty(imgString) && (imgString == null) && !(mApp.getUtils().isStringEmpty(et_feedtext.getText().toString()))) {
                            sendPhotoFirebase(feedText, feedLink);
                        }
                        if (!mApp.getUtils().isStringEmpty(imgString) && !(imgString == null)) {
                            sendPhotoFirebase(imgString, feedText, feedLink);
                        }
                        et_linkText.setVisibility(View.GONE);
                    }
                }else {
                    if (mApp.getUtils().isStringEmpty(feedText) && (imgString == null)) {
                        Toast.makeText(Post_Activity.this, "Please add something to post.", Toast.LENGTH_LONG).show();
                    }
                    if (mApp.getUtils().isStringEmpty(imgString) && (imgString == null) && !(mApp.getUtils().isStringEmpty(et_feedtext.getText().toString()))) {
                        sendPhotoFirebase(feedText, feedLink);
                        //  Toast.makeText(MyProfileActivity.this, "Please add something to post.", Toast.LENGTH_LONG).show();
                    }
                    if (!mApp.getUtils().isStringEmpty(imgString) && !(imgString == null)) {
                        sendPhotoFirebase(imgString, feedText, feedLink);
                    }
                    et_linkText.setVisibility(View.GONE);
                }
            }
        });

    }
    private void showFileChooser() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(Post_Activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, GET_PHOTO);
        }
    }
    private boolean hyperLink(String textHyper) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex ="^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(textHyper);
        while (urlMatcher.find())
        {
            containedUrls.add(textHyper.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
            return true;
        }
        return false;
    }
    private void sendPhotoFirebase(String feedText,String feedLink) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT);
        String key = databaseReference.push().getKey();
        if (user != null) {
            Feed feed = new Feed();
            feed.setIdUser(user.getUid());
            feed.setName(user.getDisplayName());
            feed.setPhotoAvatar(user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString());
            feed.setText(feedText);
            feed.setLink(feedLink);
            feed.setPhotoFeed("");
            feed.setTime(getCurrentTimeStamp());
            feed.setIdFeed(key);
            databaseReference.child(key).setValue(feed, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                }
            });
        }
        et_feedtext.setText("");
        Log.e("Post","posted");
        onBackPressed();


    }
    private void sendPhotoFirebase(String file, final String feedText,final String feedLink) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        imgString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        this.feedText = String.valueOf(et_feedtext.getText());

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading");
        dialog.show();
        Uri uri = Uri.fromFile(new File(file));
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://mydukan-1024.appspot.com");

        StorageReference reference = storageRef.child("image/" + "feedImages/" + Calendar.getInstance().getTime());

        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT);
                String key = databaseReference.push().getKey();
                if (user != null) {
                    Feed feed = new Feed();
                    feed.setIdUser(user.getUid());
                    feed.setName(user.getDisplayName());
                    feed.setPhotoAvatar(user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString());
                    feed.setText(feedText);
                    feed.setLink(feedLink);
                    feed.setPhotoFeed(taskSnapshot.getDownloadUrl().toString());
                    feed.setTime(getCurrentTimeStamp());
                    feed.setIdFeed(key);
                    databaseReference.child(key).setValue(feed);
                }
                et_feedtext.setText("");
                //tv_LinkText.setText("");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                addImageLayout.setVisibility(View.GONE);
                Toast.makeText(Post_Activity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests")
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                dialog.setMessage("Uploaded " + ((int) progress) + "%...");
                et_feedtext.setText("");
                //tv_LinkText.setText("");
                addImageLayout.setVisibility(View.GONE);
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Log.e("Post","posted");
                dialog.dismiss();
                Toast.makeText(Post_Activity.this,"Feed uploaded successfully",Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

    }
    public static String getCurrentTimeStamp() {
        DateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date current_Date = new Date();
        String strDate = sdfDate.format(current_Date);
        return strDate;
    }
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Getting the imagesFrom the Camera Intent

        if (requestCode == GET_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imgString = (saveImage(bitmap));
            addedImageView.setImageBitmap(bitmap);
            addImageLayout.setVisibility(View.VISIBLE);
        }
        //Gettting the imagesFrom the Galary

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgString = saveImage(bitmap);
                addedImageView.setImageBitmap(bitmap);
                addImageLayout.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
    public void deleteImage(String path) {

        try {
            File target = new File(path);
            Log.d(" target_path", "" + path);
            if (target.exists() && target.isFile() && target.canWrite()) {
                target.delete();
                Log.d("d_file", "" + target.getName());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
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
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void InitProfileView() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String url=(firebaseUser.getPhotoUrl() == null ? "default_uri" : firebaseUser.getPhotoUrl().toString());
        if( url!=null)
        {
            Glide.with(this)
                    .load( url)
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50,50)
                    .into(profileIMG);
        }else{
            Glide.with(this)
                    .load( R.mipmap.ic_launcher )
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .override(50,50)
                    .into(profileIMG);
        }
    }
}
