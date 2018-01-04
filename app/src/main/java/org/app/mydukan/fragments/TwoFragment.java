package org.app.mydukan.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import org.app.mydukan.activities.MyNetworksActivity;
import org.app.mydukan.activities.Search_MyNetworkActivity;
import org.app.mydukan.adapters.AdapterListFeed;
import org.app.mydukan.adapters.NetworkContactsAdapter;
import org.app.mydukan.data.ContactUsers;
import org.app.mydukan.data.Feed;
import org.app.mydukan.services.SyncContacts;
import org.app.mydukan.services.VolleyNetworkRequest;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.FeedRetriever2;
import org.app.mydukan.utils.FeedUtils;
import org.app.mydukan.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.internal.cache.DiskLruCache;

import static org.app.mydukan.activities.CommentsActivity.RESULT_DELETED;
import static org.app.mydukan.fragments.MyNetworkFragment.FEED_LOCATION;

public class TwoFragment extends Fragment implements AdapterListFeed.OnClickItemFeed, View.OnClickListener {

    public static final String FEED_ROOT = "feed";
    public static final String LIKE_ROOT = "like";
    public static final String FOLLOW_ROOT = "following";
    public static final int GET_PHOTO = 11;
    private static final int COMMENTS_REQUEST = 99;
    View mView;
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    FloatingActionButton addPost;
    Context context;
    AdView mAdView;
    VolleyNetworkRequest jsonRequest;
    //NC8By7oxjjeYVQxSfjiY3nYWoGq1
    String[] myDukan_Ids = {"NC8By7oxjjeYVQxSfjiY3nYWoGq1","oGNKfltdMsVAfjDN63QjjITGnhw1","hxv73SDWuUNG0IXEMXWO6Lez26u2"};
    AdapterListFeed mAdapter;
    FeedRetriever2 feedRetriever;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    private String userType = "Retailer";
    private List<Feed> mList;
    private ImageView profileIMG;
    private TextView profileName, profileEmail, newPOST;
    private boolean flagLike;
    private boolean flagFollow;
    private int itemThreshold = 4;
    private boolean hasMoreFeeds = true;
    Map<String, String> contactMap;
    List<Search_MyNetworkActivity.Contact> allContactList;
    List<ContactUsers> networkContacts;
    Realm realm;
    String currentQuery="";
    NetworkContactsAdapter networkContactsAdapter;
    RecyclerView followContact;
    public static final String FOLLOWING_ROOT = "following";

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(config);
        new TwoFragment.ContactLoader().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_two, container, false);
        context = mView.getContext();
        jsonRequest = new VolleyNetworkRequest(context);
        initViews();
        //initialize ads for the app  - ca-app-pub-1640690939729824/2174590993
        MobileAds.initialize(context, "ca-app-pub-1640690939729824/2174590993");
        mAdView = (AdView) mView.findViewById(R.id.adView_myNetwork_two);
        followContact = (RecyclerView) mView.findViewById(R.id.contacts_rv);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        feedRetriever = new FeedRetriever2(myDukan_Ids, new FeedRetriever2.OnFeedRetrievedListener() {
            @Override
            public void onFeedRetrieved(List<Feed> feeds) {

                if (feeds.size() == 0) {
                    hasMoreFeeds = false;
                }
                mList.addAll(feeds);
                mAdapter.notifyItemRangeInserted(mList.size() - feeds.size(), feeds.size());
                showProgress(false);
//                mSwipeRefereshLayout.setRefreshing(false);
//                emptyText.setVisibility(mList.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError() {

            }
        });
        feedRetriever.getFeeds(10, false);

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
                FeedUtils.handleHyperLink(feed, getActivity());
                break;
            case R.id.comment:
                FeedUtils.startCommentsActivity(feed, position, COMMENTS_REQUEST, getActivity());
                break;
            case R.id.delete:
                FeedUtils.delete(feed, new FeedUtils.OnCompletion() {
                    @Override
                    public void onSuccess() {
                        int pos = FeedUtils.removeFromList(mList, feed, position);
                        if (pos != -1) {
                            mAdapter.notifyItemRemoved(pos);
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
                FeedUtils.share(feed, getActivity());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    /**
     * Bind views XML with JavaAPI
     */
    private void initViews() {
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progres_bar);
        //    FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab);
        recyclerView = (RecyclerView) mView.findViewById(R.id.rv_list_feed);
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
                    if (!showingProgress() && hasMoreFeeds) {
                        if (totalItemCount - lastVisibleItemPos < itemThreshold) {
                            //mSwipeRefereshLayout.setRefreshing(true);
                            showProgress(true);
                            feedRetriever.getFeeds(10, false);
                        }
                    }
                }
            }
        });
        mList = new ArrayList<>();
        mAdapter = new AdapterListFeed(mList, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void showProgress(boolean b) {
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private boolean showingProgress() {
        return mProgressBar.getVisibility() == View.VISIBLE;
    }

    @Override
    public void onClick(View view) {

    }

    private void loadData() {
        if (contactMap == null)
            return;
        allContactList = createContactList(new ArrayList<>(contactMap.entrySet()));
        Collections.sort(allContactList, new Comparator<Search_MyNetworkActivity.Contact>() {
            @Override
            public int compare(Search_MyNetworkActivity.Contact o1, Search_MyNetworkActivity.Contact o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        filterLists(currentQuery);
    }

    private List<Search_MyNetworkActivity.Contact> createContactList(ArrayList<Map.Entry<String, String>> entries) {
        List<Search_MyNetworkActivity.Contact> contacts = new ArrayList<>();
        Collections.sort(entries, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        for (Map.Entry<String, String> entry : entries) {
            if (contacts.size() > 0 && contacts.get(contacts.size() - 1).getName().equals(entry.getValue())) {
                contacts.get(contacts.size() - 1).add(entry.getKey());
            } else {
                contacts.add(new Search_MyNetworkActivity.Contact(entry.getValue(), entry.getKey()));
            }
        }
        List<Search_MyNetworkActivity.Contact> contacts2 = new ArrayList<>();
        for (Search_MyNetworkActivity.Contact contact : contacts) {
            boolean inNetwork = false;
            for (String number : contact.getNumbers()) {
                if (!realm.where(ContactUsers.class).contains("phoneNumber", Utils.formatNumber(number)).findAll().isEmpty()) {
                    inNetwork = true;
                }
            }
            if (!inNetwork) {
                contacts2.add(contact);
            }
        }
        return contacts2;

    }

    private void filterLists(final String s) {
        networkContacts = realm.copyFromRealm(realm.where(ContactUsers.class)
                .beginsWith("name", s, Case.INSENSITIVE)
                .or()
                .contains("phoneNumber", Utils.formatNumber(s))
                .or()
                .beginsWith("contactName", s, Case.INSENSITIVE)
                .findAllSorted("name"));

        System.out.println("Inside Contacts");
        getFollowings();
    }

    public void syncContacts(){
        loadData();
    }

    private void getFollowings() {
//        swipeRefreshLayout.setRefreshing(true);
        final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollowing = FirebaseDatabase.getInstance().getReference().child(FOLLOWING_ROOT+"/"+auth.getUid());
        referenceFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                mAdapter.setFollowing(dataSnapshot);
//                reSyncContacts();
                System.out.println("DataSnapshot: "+dataSnapshot);
                List<ContactUsers> contactUsers = new ArrayList<>();
                if(dataSnapshot != null) {
                    for(int i=0; i<networkContacts.size(); i++){
                        if(!dataSnapshot.hasChild(networkContacts.get(i).getuId())){
                            contactUsers.add(networkContacts.get(i));
                        }
                    }
                    if(contactUsers.size() > 0) {
                        networkContactsAdapter = new NetworkContactsAdapter(contactUsers, context, TwoFragment.this, dataSnapshot);
                        followContact.setAdapter(networkContactsAdapter);
                        followContact.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        networkContactsAdapter.notifyDataSetChanged();
                    }
                    else{
                        followContact.setVisibility(View.GONE);
                    }
                }
                else{
                    followContact.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress(false);
            }
        });
    }

    class ContactLoader extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            return SyncContacts.getNumberMap(getContext());
        }

        @Override
        protected void onPostExecute(Map<String, String> s) {
            contactMap = s;
            loadData();
            showProgress(false);
        }
    }

}




