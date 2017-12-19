package org.app.mydukan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.app.mydukan.R;
import org.app.mydukan.data.Feed;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.viewholder.FeedViewHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shivayogi Hiremath on 04/07/2017.
 */

public class AdapterListFeed extends RecyclerView.Adapter<FeedViewHolder> {

    final FirebaseUser auth;
    public List<Feed> mList = new ArrayList<>();
    DatabaseReference databaseReference;
    private OnClickItemFeed onClickItemFeed;


    public AdapterListFeed(final List<Feed> mList, OnClickItemFeed onClickItemFeed) {
        this.mList = mList;
        auth = FirebaseAuth.getInstance().getCurrentUser();
        this.onClickItemFeed = onClickItemFeed;
    }

    /*  public static void clearDat(){
        if(!mList.isEmpty()){
            mList.clear();
        }
    }*/

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_feed2, parent, false);
        return new FeedViewHolder(view,onClickItemFeed);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {

        try {
            Feed feed = mList.get(position);
            holder.currFeed = feed;

            holder.setTvName(feed.getName());
            holder.setTvContent(feed.getText());
            holder.setTvTime(feed.getTime());
            holder.setIvAvatar(feed.getPhotoAvatar());
            holder.setIvContent(feed.getPhotoFeed());
            if (feed.getLink() != null) {
                holder.setTvLink(feed.getLink());
            }
            holder.getLikes(feed);
            holder.setDeletable(feed.getIdUser().equalsIgnoreCase(auth.getUid()));
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onBindViewHolder : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onBindViewHolder : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onBindViewHolder : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onBindViewHolder : ",ex.toString());
        }
//        holder.changeFollowing(feed.getIdUser());

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void addFeedData(List<Feed> list) {
        mList.addAll(list);
    }

   /* public static void clearFeedList() {
        mList.clear();
    }
*/

    @Override
    public void onViewRecycled(FeedViewHolder holder) {
        super.onViewRecycled(holder);
        holder.tvLike.setText("loading...");
        holder.ivLike.setImageResource(R.drawable.ic_action_like);
    }


    /**
     * Click item list
     */
    public interface OnClickItemFeed {
        void onClickItemFeed(int position, View view);
    }

}
