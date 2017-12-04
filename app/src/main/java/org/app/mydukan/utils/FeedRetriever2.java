package org.app.mydukan.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.data.Feed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Harshit Agarwal on 16-10-2017.
 * <p>
 * A temporary class to retrieve feeds from follows
 */

public class FeedRetriever2 {

    public static final String FOLLOW_ROOT = "following";
    public static final String FEED_ROOT = "feed";


    FirebaseUser user;
    PriorityQueue<Feed> queue;
    OnFeedRetrievedListener onFeedRetrievedListener;
    List<Feed> newFeeds;
    boolean userListPolled;
    boolean hasPreDeterminedList;
    List<String> userListPredetermined;


    public FeedRetriever2(OnFeedRetrievedListener onFeedRetrievedListener) {
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        queue = new PriorityQueue<>(100, new Comparator<Feed>() {
            @Override
            public int compare(Feed feed1, Feed feed2) {
                //todo check if comparator needs to be reversed
                return feed2.getIdFeed().compareTo(feed1.getIdFeed());
            }
        });
        this.onFeedRetrievedListener = onFeedRetrievedListener;
        userListPolled = false;
        hasPreDeterminedList = false;
    }

    public FeedRetriever2(String[] followList, OnFeedRetrievedListener onFeedRetrievedListener) {
        this(onFeedRetrievedListener);
        hasPreDeterminedList = true;
        userListPredetermined = Arrays.asList(followList);

    }

    public void getFeeds(final int count, boolean forceReload) {
        newFeeds = new ArrayList<>();

        if (forceReload)
            userListPolled = false;

        if (userListPolled)
            getFeedsFromQueue(count);
        else {
            if (!hasPreDeterminedList)
                retrieveFollowList(new OnFeedRetrievedListener() {

                    @Override
                    public void onFeedRetrieved(List<Feed> feeds) {
                        getFeedsFromQueue(count);
                    }

                    @Override
                    public void onError() {
                        //not able to retrieve follow list
                    }
                });
            else
                retrieveFeeds(new ArrayList<String>(userListPredetermined), new OnFeedRetrievedListener() {
                    @Override
                    public void onFeedRetrieved(List<Feed> feeds) {
                        getFeedsFromQueue(count);
                    }

                    @Override
                    public void onError() {

                    }
                });
        }

    }

    private void getFeedsFromQueue(int count) {
        if (count > 0) {
            Feed f = queue.poll();
            if (f == null) {
                onFeedRetrievedListener.onFeedRetrieved(newFeeds);
                return;
            }
            newFeeds.add(f);
            count--;
            getFeedsFromQueue(count);

        } else {
            onFeedRetrievedListener.onFeedRetrieved(newFeeds);
        }

    }

    private void retrieveFollowList(final OnFeedRetrievedListener onFeedRetrievedListener) {
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT + "/" + user.getUid());
        referenceFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<String>();
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        users.add(snapshot.getKey());
                    }
                }
                retrieveFeeds(users, onFeedRetrievedListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onFeedRetrievedListener.onError();
            }
        });
    }

    private void retrieveFeeds(final List<String> users, final OnFeedRetrievedListener onFeedRetrievedListener) {
        if (users.size() == 0) {
            userListPolled = true;
            onFeedRetrievedListener.onFeedRetrieved(null);
            return;
        }
        retrieveFeeds(users.get(0), new OnFeedRetrievedListener() {
            @Override
            public void onFeedRetrieved(List<Feed> feeds) {
                users.remove(0);
                retrieveFeeds(users, onFeedRetrievedListener);
            }

            @Override
            public void onError() {
                users.remove(0);
                retrieveFeeds(users, onFeedRetrievedListener);
            }
        });

    }

    private void retrieveFeeds(String uId, final OnFeedRetrievedListener onFeedRetrievedListener) {
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT).getRef();
       /* Query query = feedReference.orderByKey();
        if (follows.feeds.size() > 0) {
            query = query.endAt(follows.feeds.get(0).getIdFeed());
        }
        query = query.limitToLast(11);//+1 for removing redundant element*/
        Query query = feedReference.orderByChild("idUser").equalTo(uId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feed feed = snapshot.getValue(Feed.class);
                        queue.offer(feed);
                    }

                }
                if (onFeedRetrievedListener != null) {
                    onFeedRetrievedListener.onFeedRetrieved(null);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onFeedRetrievedListener != null) {
                    onFeedRetrievedListener.onError();
                }
            }
        });
    }

    public interface OnFeedRetrievedListener {
        public void onFeedRetrieved(List<Feed> feeds);

        void onError();
    }


}
