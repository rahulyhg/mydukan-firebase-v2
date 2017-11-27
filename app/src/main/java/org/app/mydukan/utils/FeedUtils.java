package org.app.mydukan.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.activities.CommentsActivity;
import org.app.mydukan.activities.WebViewActivity;
import org.app.mydukan.data.Feed;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.app.mydukan.activities.CommentsActivity.COMMENT_ROOT;
import static org.app.mydukan.fragments.MyNetworkFragment.FEED_LOCATION;
import static org.app.mydukan.fragments.TwoFragment.FEED_ROOT;
import static org.app.mydukan.fragments.TwoFragment.LIKE_ROOT;

/**
 * Created by Harshit Agarwal on 22-10-2017.
 */

public class FeedUtils {

    public static void getFeed(final String feedId, String userId, @Nullable final OnDataRetrieved onDataRetrieved) {
        DatabaseReference feedReference = FirebaseDatabase.getInstance().getReference().child(FEED_ROOT + "/" + feedId).getRef();
        feedReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (onDataRetrieved != null) {
                    if (dataSnapshot != null) {
                        Feed feed = dataSnapshot.getValue(Feed.class);
                        if (feed != null) {
                            onDataRetrieved.onSuccess(feed);
                        } else
                            onDataRetrieved.onFailure();
                    } else
                        onDataRetrieved.onFailure();
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onDataRetrieved != null)
                    onDataRetrieved.onFailure();
            }
        });
    }


    public static void addLike(final Feed feed) {
        final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(LIKE_ROOT+"/"+feed.getIdFeed());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            return;
        }
        referenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    referenceLike.child(user.getUid()).setValue(true);
                    return;
                }
                if (dataSnapshot.hasChild(user.getUid())) {
                    referenceLike.child(user.getUid()).removeValue();
                } else {
                    referenceLike.child(user.getUid()).setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void delete(Feed feed, @Nullable final OnCompletion onCompletion) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!user.getUid().equalsIgnoreCase(feed.getIdUser()))  //to check - if user is eligible to delete
            return;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> map = new HashMap<>();
        map.put(COMMENT_ROOT + "/" + feed.getIdFeed(), null);
        map.put(LIKE_ROOT + "/" + feed.getIdFeed(), null);
        map.put(FEED_ROOT + "/" + feed.getIdFeed(), null);


        databaseReference.updateChildren(map, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    if (onCompletion != null) {
                        onCompletion.onFailure();
                    }
                } else {
                    if (onCompletion != null) {
                        onCompletion.onSuccess();
                    }
                }
            }
        });


        /*deleteComments(feed,new OnCompletion());
        deleteLikes(feed);
        deleteFeed(feed,onCompletion);*/
    }


    public static void share(Feed feed, Context context) {
        String userId = feed.getIdUser();
        String feedId = feed.getIdFeed();

        String dynamicLink = "https://wy4d6.app.goo.gl/?link=https://mydukaan.com/feed_link/"
                + userId + "/" + feedId + "&apn=org.app.mydukan&amv=105";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, dynamicLink);
        sendIntent.setType("text/plain");

        try {
            context.startActivity(Intent.createChooser(sendIntent, "Share link"));
        } catch (ActivityNotFoundException e) {
            //probability=0
        }

    }

    public static void handleHyperLink(Feed feed, Context context) {
        String textHyper = feed.getText();
        if (textHyper.isEmpty() || textHyper == null) {
            return;
        } else {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra(AppContants.VIEW_PROFILE, (Serializable) feed);
            context.startActivity(intent);
        }
    }

    public static void startCommentsActivity(Feed feed, int position, int requestCode, Activity activity) {
        Intent intent = new Intent(activity, CommentsActivity.class);
        intent.putExtra(AppContants.FEED, feed);
        intent.putExtra(FEED_LOCATION, position);
        activity.startActivityForResult(intent, requestCode);
    }

    public static int removeFromList(List<Feed> mList, Feed feed, int position) {
        if(mList.get(position).getIdFeed().equalsIgnoreCase(feed.getIdFeed())){
            mList.remove(position);
            return position;
        }
        int i=0;
        position=-1;
        for(Feed rFeed:mList){
            if(rFeed.getIdFeed().equalsIgnoreCase(feed.getIdFeed())){
                position=i;
                break;
            }
            i++;
        }

        if(position!=-1){
            mList.remove(position);
        }
        return position;
    }

    public interface OnCompletion {
        public void onSuccess();

        public void onFailure();
    }

    public interface OnDataRetrieved {
        public void onSuccess(Object object);

        public void onFailure();
    }
}
