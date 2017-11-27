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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created by Harshit Agarwal on 16-10-2017.
 */

public class FeedRetriever {

    public static final String FOLLOW_ROOT = "following";
    public static final String FEED_ROOT = "feed";

    Map<String, UserFollowing> following;

    FirebaseUser user;
    PriorityQueue<Feed> queue;
    OnFeedRetrievedListener onFeedRetrievedListener;
    List<Feed> newFeeds;
    boolean userListPolled;

    public FeedRetriever( OnFeedRetrievedListener onFeedRetrievedListener) {
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        following = new HashMap<>();
        queue = new PriorityQueue<>(100, new Comparator<Feed>() {
            @Override
            public int compare(Feed feed1, Feed feed2) {
                //todo check if comparator needs to be reversed
                return feed2.getIdFeed().compareTo(feed1.getIdFeed());
            }
        });
        this.onFeedRetrievedListener = onFeedRetrievedListener;
        userListPolled = false;
    }

    public void getFeeds(final int count, boolean forceReload) {
        newFeeds = new ArrayList<>();

        if (forceReload)
            userListPolled = false;

        if (userListPolled)
            getFeedsFromQueue(count);
        else {
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
            UserFollowing userFollowing = following.get(f.getIdFeed());
            userFollowing.index++;

            if (userFollowing.index > userFollowing.feeds.size()) {
                final int finalCount = count;
                retrieveFeeds(userFollowing, new OnFeedRetrievedListener() {
                    @Override
                    public void onFeedRetrieved(List<Feed> feeds) {
                        getFeedsFromQueue(finalCount);
                    }

                    @Override
                    public void onError() {
                        getFeedsFromQueue(finalCount);
                    }
                });
            } else {
                getFeedsFromQueue(count);
            }


        }else{
            onFeedRetrievedListener.onFeedRetrieved(newFeeds);
        }

    }

    private void retrieveFollowList(final OnFeedRetrievedListener onFeedRetrievedListener) {
        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(FOLLOW_ROOT + "/" + user.getUid());
        referenceFollow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.clear();
                List<String> users = new ArrayList<String>();
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        following.put(snapshot.getKey(), new UserFollowing(snapshot.getKey()));
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
        retrieveFeeds(following.get(users.get(0)), new OnFeedRetrievedListener() {
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

    private void retrieveFeeds(final UserFollowing follows, final OnFeedRetrievedListener onFeedRetrievedListener) {
        if (!follows.containsMore || follows.callInProgress) return;

        follows.callInProgress = true;

        //todo the call doesn't work, because of current DB design. to change when the design is changed
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT + "/" + follows.uId).getRef();
        Query query = feedReference.orderByKey();
        if (follows.feeds.size() > 0) {
            query = query.endAt(follows.feeds.get(0).getIdFeed());
        }
        query = query.limitToLast(11);//+1 for removing redundant element
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot == null || dataSnapshot.getChildrenCount() <= 1) {
                    follows.containsMore = false;

                } else {
                    follows.feeds.clear();
                    follows.index = 0;
                    int i = 1;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feed feed = snapshot.getValue(Feed.class);
                        follows.feeds.add(feed);
                        queue.offer(feed);
                        i++;
                        if (i == dataSnapshot.getChildrenCount()) {
                            break;
                        }
                    }

                }
                follows.callInProgress = false;
                if (onFeedRetrievedListener != null) {
                    onFeedRetrievedListener.onFeedRetrieved(null);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                follows.callInProgress = false;
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


    class UserFollowing {
        public String uId;
        public List<Feed> feeds;
        public boolean containsMore;
        public int index;
        public boolean callInProgress;

        public UserFollowing(String uId) {
            this.uId = uId;
            containsMore = true;
            index = 0;
            feeds = new ArrayList<>();
            callInProgress = false;
        }
    }

}
