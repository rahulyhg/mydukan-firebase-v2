package org.app.mydukan.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.app.mydukan.activities.Post_Activity;
import org.app.mydukan.adapters.AdapterListFeed;
import org.app.mydukan.data.Feed;
import org.app.mydukan.services.VolleyNetworkRequest;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.FeedRetriever2;
import org.app.mydukan.utils.FeedUtils;

import java.util.ArrayList;
import java.util.List;

import static org.app.mydukan.activities.CommentsActivity.RESULT_DELETED;


public class MyNetworkFragment extends Fragment implements AdapterListFeed.OnClickItemFeed, View.OnClickListener {

    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOW_ROOT = "following";
    public static final int GET_PHOTO = 11;
    private static final int COMMENTS_REQUEST = 100;
    public static final String FEED_LOCATION = "feed_pos";
    View mView;
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    FloatingActionButton addPost;
    Context context;
    AdView mAdView;
    SwipeRefreshLayout mSwipeRefereshLayout;
    FeedRetriever2 feedRetriever;
    View emptyText;
    private RecyclerView recyclerView;
    private String userType = "Retailer";
    private List<Feed> mList = new ArrayList<>();
    private List<Feed> mFeedList;
    private ImageView profileIMG;
    private TextView profileName, profileEmail, newPOST;
    private boolean flagLike;
    private boolean flagFollow;
    private FloatingActionButton appNewPost;
    //Variables
    private AdapterListFeed adapterListFeed;
    private int itemThreshold = 4;
    private boolean hasMoreFeeds = true;
    VolleyNetworkRequest jsonRequest;
    TextView writePost;

    public MyNetworkFragment() {
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
        mView = inflater.inflate(R.layout.fragment_one, container, false);
        context = mView.getContext();
        jsonRequest = new VolleyNetworkRequest(context);
        initViews();
        //initialize ads for the app  - ca-app-pub-1640690939729824/2174590993
        MobileAds.initialize(context, "ca-app-pub-1640690939729824/2174590993");
        mAdView = (AdView) mView.findViewById(R.id.adView_myNetwork_one);
        writePost = (TextView) mView.findViewById(R.id.writePost);
        writePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Post_Activity.class);
                intent.putExtra(AppContants.CHAT_USER_PROFILE, "" );
                startActivity(intent);
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapterListFeed = new AdapterListFeed(mList, this);
        recyclerView.setAdapter(adapterListFeed);

        feedRetriever = new FeedRetriever2(new FeedRetriever2.OnFeedRetrievedListener() {
            @Override
            public void onFeedRetrieved(List<Feed> feeds) {
                if (feeds.size() == 0) {
                    hasMoreFeeds = false;
                }
                mList.addAll(feeds);
                adapterListFeed.notifyItemRangeInserted(mList.size() - feeds.size(), feeds.size());
                mSwipeRefereshLayout.setRefreshing(false);
                emptyText.setVisibility(mList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError() {

            }
        });

        mSwipeRefereshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                adapterListFeed.notifyDataSetChanged();
                feedRetriever.getFeeds(10, true);

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItemPos, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {   //scroll down

                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItemPos = layoutManager.findLastVisibleItemPosition();
                    if (!mSwipeRefereshLayout.isRefreshing() && hasMoreFeeds) {
                        if (totalItemCount - lastVisibleItemPos < itemThreshold) {
                            mSwipeRefereshLayout.setRefreshing(true);
                            feedRetriever.getFeeds(10, false);
                        }
                    }
                }
            }
        });

        feedRetriever.getFeeds(10, false);
        mSwipeRefereshLayout.setRefreshing(true);

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
                 /*   String profileID = feed.getIdUser();
                    Intent intent = new Intent(getContext(), FeedPrifileActivity.class);
                    intent.putExtra(AppContants.VIEW_PROFILE, (Serializable) feed);
                    startActivity(intent);*/
                }
                break;
            case R.id.tv_contentLink:  // HyperLink click
                FeedUtils.handleHyperLink(feed,getActivity());
                break;
            case R.id.comment:
                FeedUtils.startCommentsActivity(feed,position,COMMENTS_REQUEST,getActivity());
                break;
            case R.id.delete:

                FeedUtils.delete(feed, new FeedUtils.OnCompletion() {
                    @Override
                    public void onSuccess() {
                        int pos = FeedUtils.removeFromList(mList, feed, position);
                        if (pos != -1) {
                            adapterListFeed.notifyItemRemoved(pos);
                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(getContext(), "Unknown error occurred while deleting", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.share:
                FeedUtils.share(feed,getActivity());
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COMMENTS_REQUEST && resultCode == RESULT_DELETED) {
            int position = data.getIntExtra(FEED_LOCATION, -1);
            if (position == -1 || position>=mList.size()) {
                return;
            }
            int pos = FeedUtils.removeFromList(mList, (Feed) data.getSerializableExtra(AppContants.FEED), position);
            if (pos != -1)
                adapterListFeed.notifyItemRemoved(pos);
        }
    }


    private void initViews() {
        mSwipeRefereshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeToRefresh);
        recyclerView = (RecyclerView) mView.findViewById(R.id.rv_list_feed);
        emptyText = mView.findViewById(R.id.emptyText);

    }


    @Override
    public void onClick(View view) {

    }
}
