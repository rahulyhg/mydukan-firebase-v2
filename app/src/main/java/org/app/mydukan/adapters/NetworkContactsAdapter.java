package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.data.ContactUsers;
import org.app.mydukan.fragments.TwoFragment;
import org.app.mydukan.services.VolleyNetworkRequest;

import java.util.HashMap;
import java.util.List;

import static org.app.mydukan.activities.Search_MyNetworkActivity.FOLLOWERS_ROOT;
import static org.app.mydukan.activities.Search_MyNetworkActivity.FOLLOWING_ROOT;

/**
 * Created by MyDukan on 04-01-2018.
 */

public class NetworkContactsAdapter extends RecyclerView.Adapter<NetworkContactsAdapter.ViewHolder> {

    private List<ContactUsers> networkUsers;
    private Context context;
    private TwoFragment fragment;
    VolleyNetworkRequest jsonRequest;
    DataSnapshot following;

    public NetworkContactsAdapter(List<ContactUsers> networkUsers, Context context, TwoFragment fragment, DataSnapshot following) {
        this.networkUsers = networkUsers;
        this.context = context;
        this.fragment = fragment;
        jsonRequest = new VolleyNetworkRequest(context);
        this.following = following;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;
        contactView = inflater.inflate(R.layout.follow_new_contacts, parent, false);
        // Return a new holder instance
        return new NetworkContactsAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final ContactUsers contactUsers = networkUsers.get(position);

        holder.contact_name.setText(contactUsers.getContactName());
        holder.contact_type.setText("(" + contactUsers.getUserType() + ")");
        holder.setIvAvatar(contactUsers.getPhotoUrl());

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFollow(contactUsers);
                fragment.syncContacts();
            }
        });
    }

    @Override
    public int getItemCount() {
        /*for(int i=0; i<networkUsers.size(); i++){
            if(networkUsers.get(i).get)
        }*/
        return networkUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView contact_pic;
        TextView contact_name;
        TextView contact_type;
        TextView follow;

        public ViewHolder(View itemView) {
            super(itemView);
            contact_pic = (ImageView)itemView.findViewById(R.id.contact_pic);
            contact_name = (TextView) itemView.findViewById(R.id.contact_name);
            contact_type = (TextView) itemView.findViewById(R.id.contact_type);
            follow = (TextView) itemView.findViewById(R.id.follow_contact);
        }

        public void setIvAvatar(String url) {
            if (contact_pic == null) return;
            if (url.equals("default_uri")) {
                Glide.with(contact_pic.getContext())
                        .load(R.drawable.profile_circle)
                        .placeholder(R.drawable.profile_circle)
                        .centerCrop()
                        .transform(new CircleTransform(contact_pic.getContext()))
                        .override(50, 50)
                        .into(contact_pic);
            } else {
                Glide.with(contact_pic.getContext())
                        // .using(new FirebaseImageLoader())
                        .load(url)
                        .placeholder(R.drawable.profile_circle)
                        .centerCrop()
                        .transform(new CircleTransform(contact_pic.getContext()))
                        .override(50, 50)
                        .into(contact_pic);
            }
        }

    }

    private void toggleFollow(final ContactUsers userToFollow) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollowing = FirebaseDatabase.getInstance().getReference().child(FOLLOWING_ROOT + "/" + user.getUid());
        final DatabaseReference referenceFollowers = FirebaseDatabase.getInstance().getReference().child(FOLLOWERS_ROOT + "/" + userToFollow.getuId());

        if (following == null || !following.hasChild(userToFollow.getuId())) {
            referenceFollowing.child(userToFollow.getuId()).setValue(true);//adding userid to following list  .
            referenceFollowers.child(user.getUid()).setValue(true);//adding the user id to distributor following list.
            fragment.showProgress(true);
            getUserToken(user.getUid(), user.getDisplayName());
        } else {
            referenceFollowing.child(userToFollow.getuId()).removeValue();//removing userid from following list  .
            referenceFollowers.child(user.getUid()).removeValue();//removing the user id from distributor following list.
        }
    }

    public void getUserToken(final String auth, final String name){
        final DatabaseReference referenceFcm = FirebaseDatabase.getInstance().getReference().child("fcmregistration");
//        final String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceFcm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> map = new HashMap<>();
                map = (HashMap<String, Object>) dataSnapshot.getValue();
                if(map.containsKey(auth)) {
                    System.out.println("User Token Comment: " + map.get(auth));
                    jsonRequest.JsonObjectRequest((String)map.get(auth), name, "follow", "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
