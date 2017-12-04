package org.app.mydukan.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.app.mydukan.R;
import org.app.mydukan.activities.Search_MyNetworkActivity.Contact;
import org.app.mydukan.data.ContactUsers;

import java.util.ArrayList;
import java.util.List;

import static org.app.mydukan.activities.Search_MyNetworkActivity.FOLLOWERS_ROOT;
import static org.app.mydukan.activities.Search_MyNetworkActivity.FOLLOWING_ROOT;
import static org.app.mydukan.adapters.SearchNetworkAdapter.SearchViewType.NETWORK_CONTACT;
import static org.app.mydukan.adapters.SearchNetworkAdapter.SearchViewType.NETWORK_HEADER_STRIP;
import static org.app.mydukan.adapters.SearchNetworkAdapter.SearchViewType.NON_NETWORK_CONTACT;
import static org.app.mydukan.adapters.SearchNetworkAdapter.SearchViewType.NON_NETWORK_HEADER_STRIP;
import static org.app.mydukan.adapters.SearchNetworkAdapter.SearchViewType.NOT_SPECIFIED;


/**
 * Created by Harshit Agarwal on 06-11-2017.
 */
public class SearchNetworkAdapter extends RecyclerView.Adapter {

    Context context;
    LayoutInflater inflater;
    @Nullable
    private DataSnapshot followings;
    private List<ContactUsers> networkUsers;
    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int pos, View v) {
            if (v.getId() == R.id.btn_follow) {
                toggleFollow(networkUsers.get(pos - 1));
            } else if (v.getId() == R.id.invite) {
                if (networkUsers.size() > 0) {
                    pos--;
                }
                pos -= networkUsers.size() + 1;
                sendUserInvite(nonNetworkUsers.get(pos));
            }
        }
    };

    private void sendUserInvite(final Contact contact) {
        if (contact.getNumbers().size() > 1) {
            AlertDialog.Builder invitechooser = new AlertDialog.Builder(context);
            invitechooser.setTitle("Select a number to send invite to");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, contact.getNumbers());

            invitechooser.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            invitechooser.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendMessageInvite(arrayAdapter.getItem(which));
                }
            });
            invitechooser.show();
        } else {
            sendMessageInvite(contact.getNumbers().get(0));
        }
    }

    private void sendMessageInvite(String number) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
        String url = "https://play.google.com/store/apps/details?id=org.app.mydukan";
        intent.putExtra("sms_body", context.getResources().getString(R.string.share_msg_text)+ "\n " + url);
        context.startActivity(intent);

    }

    private List<Contact> nonNetworkUsers;
    private boolean containsNetwork, containsNonNetwork;

    public SearchNetworkAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        networkUsers = new ArrayList<>();
        nonNetworkUsers = new ArrayList<>();
    }

    public void setNonNetworkUsers(List<Contact> nonNetworkUsers) {
        this.nonNetworkUsers = nonNetworkUsers;
        containsNonNetwork = nonNetworkUsers.size() > 0;
        notifyDataSetChanged();
    }

    public void setNetworkUsers(List<ContactUsers> networkUsers) {
        this.networkUsers = networkUsers;
        containsNetwork = networkUsers.size() > 0;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NETWORK_CONTACT) {
            return new NetworkContactViewHolder(inflater.inflate(R.layout.network_row_card, parent, false));
        } else if (viewType == NETWORK_HEADER_STRIP) {
            HeaderViewHolder viewHolder = new HeaderViewHolder(inflater.inflate(R.layout.header_layout, parent, false));
            viewHolder.textView.setText("Network Contacts");
            return viewHolder;
        } else if (viewType == NON_NETWORK_HEADER_STRIP) {
            HeaderViewHolder viewHolder = new HeaderViewHolder(inflater.inflate(R.layout.header_layout, parent, false));
            viewHolder.textView.setText("Other Contacts");
            return viewHolder;
        } else if (viewType == NON_NETWORK_CONTACT) {
            return new NonNetworkViewHolder(inflater.inflate(R.layout.non_network_card, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NetworkContactViewHolder) {
            position--;
            final ContactUsers user = networkUsers.get(position);
            NetworkContactViewHolder viewHolder = (NetworkContactViewHolder) holder;
            viewHolder.setContactName(user.getContactName());
            viewHolder.setTvEmail(user.getEmail());
            viewHolder.setIvAvatar(user.getPhotoUrl());
            viewHolder.setTvType(user.getUserType());
            viewHolder.setDukanName(user.getName());
            if (followings != null && followings.hasChild(user.getuId()))
                viewHolder.followBTN.setText("Unfollow");
            else
                viewHolder.followBTN.setText("Follow");
        }
        if (holder instanceof NonNetworkViewHolder) {
            position -= networkUsers.size();
            if (containsNetwork) {
                position--;
            }
            position--;

            NonNetworkViewHolder viewHolder = (NonNetworkViewHolder) holder;
            viewHolder.name.setText(nonNetworkUsers.get(position).getName());
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (containsNetwork) {
            if (position == 0)
                return NETWORK_HEADER_STRIP;
            position--;
            if (position < networkUsers.size()) {
                return NETWORK_CONTACT;
            }
            position -= networkUsers.size();
        }
        if (containsNonNetwork) {
            if (position == 0)
                return NON_NETWORK_HEADER_STRIP;
            position--;
            if (position < nonNetworkUsers.size()) {
                return NON_NETWORK_CONTACT;
            }
        }
        return NOT_SPECIFIED;

    }

    @Override
    public int getItemCount() {
        int headerCount = 0;
        if (containsNonNetwork)
            headerCount++;
        if (containsNetwork)
            headerCount++;
        return headerCount + networkUsers.size() + nonNetworkUsers.size();
    }

    public void setFollowing(DataSnapshot children) {
        followings = children;
        notifyDataSetChanged();
    }

    private void toggleFollow(final ContactUsers userToFollow) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollowing = FirebaseDatabase.getInstance().getReference().child(FOLLOWING_ROOT + "/" + user.getUid());
        final DatabaseReference referenceFollowers = FirebaseDatabase.getInstance().getReference().child(FOLLOWERS_ROOT + "/" + userToFollow.getuId());

        if (followings == null || !followings.hasChild(userToFollow.getuId())) {
            referenceFollowing.child(userToFollow.getuId()).setValue(true);//adding userid to following list  .
            referenceFollowers.child(user.getUid()).setValue(true);//adding the user id to distributor following list.
        } else {
            referenceFollowing.child(userToFollow.getuId()).removeValue();//removing userid from following list  .
            referenceFollowers.child(user.getUid()).removeValue();//removing the user id from distributor following list.
        }
    }

    public boolean isEmpty() {
        return networkUsers.isEmpty() && nonNetworkUsers.isEmpty();
    }

    interface SearchViewType {
        int NOT_SPECIFIED = -1;
        int NETWORK_HEADER_STRIP = 0;
        int NON_NETWORK_HEADER_STRIP = 1;
        int NETWORK_CONTACT = 2;
        int NON_NETWORK_CONTACT = 3;
    }

    interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }


    class NonNetworkViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button invite;

        NonNetworkViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_profileName);
            invite = (Button) itemView.findViewById(R.id.invite);
            invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getLayoutPosition(), v);
                }
            });
        }
    }

    class NetworkContactViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView contactName, tvEmail, tvFollow, tvType, dukanName;
        private Button followBTN;

        public NetworkContactViewHolder(View itemView) {
            super(itemView);
            dukanName = (TextView) itemView.findViewById(R.id.dukanName);
            ivAvatar = (ImageView) itemView.findViewById(R.id.img_profile);
            contactName = (TextView) itemView.findViewById(R.id.tv_profileName);
            tvEmail = (TextView) itemView.findViewById(R.id.tv_profileEmail);
            tvFollow = (TextView) itemView.findViewById(R.id.profile_Followers);
            tvType = (TextView) itemView.findViewById(R.id.profile_Type);
            followBTN = (Button) itemView.findViewById(R.id.btn_follow);
            followBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getLayoutPosition(), v);
                }
            });
        }


        public void setDukanName(String name) {
            if (contactName == null) return;
            contactName.setText(name);

        }

        public void setIvAvatar(String url) {
            if (ivAvatar == null) return;
            if (url.equals("default_uri")) {
                Glide.with(ivAvatar.getContext())
                        .load(R.drawable.profile_circle)
                        .placeholder(R.drawable.profile_circle)
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50, 50)
                        .into(ivAvatar);
            } else {
                Glide.with(ivAvatar.getContext())
                        // .using(new FirebaseImageLoader())
                        .load(url)
                        .placeholder(R.drawable.profile_circle)
                        .centerCrop()
                        .transform(new CircleTransform(ivAvatar.getContext()))
                        .override(50, 50)
                        .into(ivAvatar);
            }
        }

        public void setContactName(String name) {
            dukanName.setText("(" + name + ")");
        }

        public void setTvType(String text) {
            if (tvType == null) return;
            tvType.setText(text);
        }

        public void setTvEmail(String text) {
            if (tvEmail == null) return;
            tvEmail.setText(text);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

}
