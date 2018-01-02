package org.app.mydukan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.adapters.AdapterListFeed;
import org.app.mydukan.adapters.CircleTransform;
import org.app.mydukan.adapters.MyFeedAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.services.VolleyNetworkRequest;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.FeedUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.app.mydukan.activities.CommentsActivity.RESULT_DELETED;
import static org.app.mydukan.fragments.MyNetworkFragment.FEED_LOCATION;

/**
 * Created by rajat on 05-11-2017.
 */

public class MyProfileActivity extends BaseActivity implements AdapterListFeed.OnClickItemFeed,MyFeedAdapter.OnClickItemFeed{

    private static final String TAG = "MY_PROFILE";
    String profileUID="";
    User profileDetails = new User();
    String mid;
    ChattUser chattUser;
    private List<Feed> mList;
    private ProgressBar mProgressBar;
    TextView tv_profile, tv_profileEmail,tvFollowers,tvFollowing,tv_profilePhone, tv_FeedText,emptyText,bt_post,more;
    ImageView tv_profileImg;
    Button btn_Follow;
    private RecyclerView recyclerView;
    private static final String IMAGE_DIRECTORY = "/mydukan";
    private MyDukan mApp;
    private static final int COMMENTS_REQUEST = 99;
    AdapterListFeed mAdapter;
    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOWING_ROOT = "following";
    public static final String FOLLOWERS_ROOT = "followers";
    private boolean flagFollow=false;
    LinearLayout btn_Following,btn_Followers,details_layout,post_layout;
    AdView mAdView;
    boolean mMyProfile=false;
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    SwipeRefreshLayout mSwipeRefereshLayout;
    NestedScrollView nestedScrollView;
    CardView postcard;
    VolleyNetworkRequest jsonRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        mApp = (MyDukan) getApplicationContext();
        jsonRequest = new VolleyNetworkRequest(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initViews();
        mSwipeRefereshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                mAdapter.notifyDataSetChanged();
                Funct_onSwipeReload();

            }
        });
        more=(TextView) findViewById(R.id.bt_more);
        postcard=(CardView)findViewById(R.id.layout_ac_3);
        tv_profile = (TextView) findViewById(R.id.profile_Name_1);
        tv_profileEmail = (TextView) findViewById(R.id.profile_Email_1);
        tv_profilePhone=(TextView)findViewById(R.id.profile_Phone_1);
        tvFollowing=(TextView)findViewById(R.id.profile_Following_1);
        tv_FeedText = (TextView) findViewById(R.id.editTextprofile_1);
        details_layout=(LinearLayout)findViewById(R.id.layout_ac_2);
        post_layout=(LinearLayout)findViewById(R.id.layout_ac_4);
        bt_post=(TextView)findViewById(R.id.editTextprofile_1);
        tv_profileImg = (ImageView) findViewById(R.id.profile_IMG_1);
        btn_Follow = (Button) findViewById(R.id.bt_follow);
        tvFollowers = (TextView) findViewById(R.id.profile_Followers_1);
        btn_Followers = (LinearLayout) findViewById(R.id.view_followers_1);
        btn_Following = (LinearLayout) findViewById(R.id.view_following_1);
        Funct_onSwipeReload();
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, YourAccountActivity.class);
                intent.putExtra(AppContants.CHAT_USER_PROFILE, (Serializable) chattUser);
                startActivity(intent);
            }
        });
        btn_Follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mid != null) {
                    addFollow(mid);
                }
            }
        });


        btn_Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileUID.isEmpty()||(profileUID)==null){
                    return;
                }
                Intent activityintent = new Intent( MyProfileActivity.this, FeedProfileFollowActivity.class);
                activityintent.putExtra(AppContants.PROFILE_ID_FOLLOWING,profileUID);
                activityintent.putExtra(AppContants.MYPROFILE_FOLLOW, AppContants.MYPROFILE_FOLLOW);
                startActivity(activityintent);
            }
        });
        btn_Followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileUID.isEmpty()||(profileUID)==null){
                    return;
                }
                Intent activityintent = new Intent(MyProfileActivity.this, FeedProfileFollowActivity.class);
                activityintent.putExtra(AppContants.PROFILE_ID_FOLLOWERS,profileUID);
                activityintent.putExtra(AppContants.MYPROFILE_FOLLOW, AppContants.MYPROFILE_FOLLOW);
                startActivity(activityintent);
            }
        });
        bt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, Post_Activity.class);
                intent.putExtra(AppContants.CHAT_USER_PROFILE, (Serializable) chattUser);
                startActivity(intent);
            }
        });
        //initialize ads for the app  - ca-app-pub-1640690939729824/2174590993
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-1640690939729824/2174590993");
        mAdView = (AdView) findViewById(R.id.adView_myProfile);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //TestFunc_Add50();

    }

    private void Funct_onSwipeReload() {
        Bundle mybundle = getIntent().getExtras();
        if (mybundle != null) {
            if (mybundle.containsKey(AppContants.CHAT_USER_PROFILE)) {
                chattUser = (ChattUser) mybundle.getSerializable(AppContants.CHAT_USER_PROFILE);
                String id=mApp.getFirebaseAuth().getCurrentUser().getUid();
                if(chattUser.getuId().endsWith(id)){
                    setMyProfile(true);
                }else {
                    setMyProfile(false);
                }
                initPhoto(chattUser);
                if (chattUser != null) {
                    profileUID = chattUser.getuId();
                    mid=chattUser.getuId();
                    changeFollowing(profileUID);
                    getListFollowing(profileUID);
                    getProfileData(profileUID);

                } else {
                    Toast.makeText(MyProfileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setMyProfile(boolean b) {
        mMyProfile=b;
        if(b){
            btn_Follow.setVisibility(View.GONE);
            postcard.setVisibility(View.VISIBLE);
        }else {
            btn_Follow.setVisibility(View.VISIBLE);
            postcard.setVisibility(View.GONE);
        }
    }

    private void getProfileData(String profileUID) {
        ApiManager.getInstance(this).getUserProfile(profileUID, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                profileDetails = (User) data;
                if (profileDetails == null) {
                    return;
                }
                profileDetails.getId();
                if(profileDetails!=null){
                    initProfileView(profileDetails, chattUser);
                    retriveFeedsData(profileDetails, chattUser.getuId());
                }else{
                    Toast.makeText(MyProfileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(String response) {
                Toast.makeText(MyProfileActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void retriveFeedsData(final User profileDetails, final String UserId) {
        if (UserId != null && !UserId.isEmpty()) {
            DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT).getRef();
            feedReference.orderByChild("idUser").equalTo(UserId).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //1_open
                    Log.d(TAG,"abcd");
                    showProgress(true);
                    mList.clear();
                    if (dataSnapshot != null) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Feed feed = snapshot.getValue(Feed.class);
                            mList.add(feed);
                            mSwipeRefereshLayout.setRefreshing(false);
                        }
                        initRecyclerView(mList);
                        Log.d(TAG,"abcd2");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //closefor1
                    showProgress(false);
                }
            });
        }


    }

    // Change the following button Text(Follow - UnFollow).
    public void changeFollowing(final String followKey) {// followkey is user_id
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOWERS_ROOT +"/"+followKey);
        referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long totalFollowers = 0;
                if(dataSnapshot!=null){
                    totalFollowers = dataSnapshot.getChildrenCount();
                }
                tvFollowers.setText(totalFollowers+"");
                if (dataSnapshot!=null && dataSnapshot.hasChild(auth.getUid())) {
                    btn_Follow.setText("Unfollow");
                } else {
                    btn_Follow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // get the following profile count.
    public void getListFollowing(final String followingKey) {

        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOWING_ROOT +"/"+followingKey);
        referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //showProgress(false);
                long totalFollowing = 0;
                if(dataSnapshot!=null){
                    totalFollowing=dataSnapshot.getChildrenCount();
                }
               // if(intial_follow) {

//                    intial_follow=false;
                //}
                tvFollowing.setText(totalFollowing + "");
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });
    }
    private void addFollow(final String id) {
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        flagFollow = true;
        final DatabaseReference referenceFollowing = FirebaseDatabase.getInstance().getReference().child(FOLLOWING_ROOT +"/"+auth.getUid());
        final DatabaseReference referenceFollowers = FirebaseDatabase.getInstance().getReference().child(FOLLOWERS_ROOT +"/"+id);

        referenceFollowing.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (flagFollow) {
                    if (dataSnapshot!=null&& dataSnapshot.hasChild(id)) {
                        referenceFollowing.child(id).removeValue();//removing userid to following list  .
                        referenceFollowers.child(auth.getUid()).removeValue();//removing the user id to distributor following list.
//                        btn_Follow.setText("Follow");
//                        flagFollow = false;
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(id, "unFollow Clicked"));

                    } else {
                        referenceFollowing.child(id).setValue(true);//adding userid to following list  .
                        referenceFollowers.child(auth.getUid()).setValue(true);//adding the user id to distributor following list.
//                        btn_Follow.setText("UnFollow");
//                        flagFollow = false;
                        Answers.getInstance().logCustom(new CustomEvent("Mynetwork FeedPage")
                                .putCustomAttribute(id, "Follow Clicked"));

                    }
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initProfileView(User profileDetails, ChattUser mChattUser) {
        details_layout.setVisibility(View.VISIBLE);
        String cNAME= (profileDetails.getCompanyinfo()!=null && profileDetails.getCompanyinfo().getName()!=null )?
                profileDetails.getCompanyinfo().getName() : profileDetails.getUserinfo().getName();
        tv_profile.setText(cNAME);
        String profileRole=profileDetails.getOtherinfo().getRole();
        if(profileRole.isEmpty()&&profileRole!=null){
            //tv_ProfileType.setVisibility(View.VISIBLE);
            //tv_ProfileType.setText(profileDetails.getOtherinfo().getRole());
        }else{
            //tv_ProfileType.setVisibility(View.GONE);
        }

        tv_profileEmail.setText(profileDetails.getUserinfo().getEmailid());
        tv_profilePhone.setText(profileDetails.getUserinfo().getNumber());
        Answers.getInstance().logCustom(new CustomEvent("My Profile ")
                .putCustomAttribute("profileDetails.getUserinfo().getEmailid()", "Profile Viewed"));

    }
    private void initPhoto(ChattUser mChattUser) {
        if (mChattUser.getPhotoUrl() != null) {
            Glide.with(MyProfileActivity.this)
                    .load(mChattUser.getPhotoUrl())
                    .centerCrop()
                    .transform(new CircleTransform(MyProfileActivity.this))
                    .override(120,120)
                    .into(tv_profileImg);
        } else {
            Glide.with(MyProfileActivity.this)
                    .load(R.drawable.ic_action_profile)
                    .centerCrop()
                    .transform(new CircleTransform(MyProfileActivity.this))
                    .override(120,120)
                    .into(tv_profileImg);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //for comments
        if (requestCode == COMMENTS_REQUEST && resultCode == RESULT_DELETED) {
            int position = data.getIntExtra(FEED_LOCATION, -1);
            if (position == -1 || position >= mList.size()) {
                return;
            }
            int pos = FeedUtils.removeFromList(mList, (Feed) data.getSerializableExtra(AppContants.FEED), position);
            if (pos != -1)
                mAdapter.notifyItemRemoved(pos);
        }
    }


    @Override
    public void onClickItemFeed(final int position, View view) {
        final Feed feed = mList.get(position);
        switch (view.getId()) {
            case R.id.like:  // Like_BTN
                FeedUtils.addLike(feed,jsonRequest);
                break;
            case R.id.btn_follow:  // follow_BTN
                // addFollow(feed);
                break;
            case R.id.layout_vProfile: // followBTN
                if (feed.getIdUser() != null && !(feed.getIdUser().isEmpty())) {
                }
                break;
            case R.id.tv_contentLink:  // HyperLink click
                FeedUtils.handleHyperLink(feed, this);
                break;
            case R.id.comment:
                FeedUtils.startCommentsActivity(feed, position, COMMENTS_REQUEST, this);
                break;
            case R.id.delete:
//                showProgress(true);
                FeedUtils.delete(feed, new FeedUtils.OnCompletion() {
                    @Override
                    public void onSuccess() {
                        //Toast.makeText(MyProfileActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        //showProgress(false);

                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(MyProfileActivity.this, "Unknown error occurred while deleting", Toast.LENGTH_SHORT).show();
                        //showProgress(false);
                    }

                });

                break;
            case R.id.share:
                FeedUtils.share(feed, this);
                break;
        }
    }

    private void initViews() {
        mProgressBar = (ProgressBar) findViewById(R.id.pb_1);
        mList = new ArrayList<>();
        nestedScrollView=(NestedScrollView)findViewById(R.id.nestedScrollView_1);
        emptyText=(TextView)findViewById(R.id.emptyText_1);
        mSwipeRefereshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh_1);
        recyclerView = (RecyclerView) findViewById(R.id.rv_list_feed_1);
        mAdapter= new AdapterListFeed(mList,this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int lastVisibleItemPos, totalItemCount;

                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        //code to fetch more data for endless scrolling
                    }
                }
            }
        });
    }

    private void showProgress(boolean b) {
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private boolean showingProgress() {
        return mProgressBar.getVisibility() == View.VISIBLE;
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    public static String getCurrentTimeStamp() {
        DateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date current_Date = new Date();
        String strDate = sdfDate.format(current_Date);
        return strDate;
    }


    private void initRecyclerView(List<Feed> list) {
        // recyclerView.setAdapter(new AdapterListFeed(list, this));
        Collections.sort(list, new Comparator<Feed>() {
            // DateFormat f = new SimpleDateFormat("dd/MM/yyyy '@'hh:mm a");
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // SimpleDateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            @Override
            public int compare(Feed o1, Feed o2) {
                try {
                    return f.parse(o1.getTime()).compareTo(f.parse(o2.getTime()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        // reverse the list
        Collections.reverse(list);
        mList = list;
        mAdapter.notifyDataSetChanged();
        post_layout.setVisibility(View.VISIBLE);
        showProgress(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
