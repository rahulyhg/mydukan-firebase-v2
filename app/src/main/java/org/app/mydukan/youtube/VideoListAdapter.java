package org.app.mydukan.youtube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.content.YouTubeContent;
import org.app.mydukan.data.Videos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***********************************************************************************

 ***********************************************************************************/

public class VideoListAdapter extends BaseAdapter implements YouTubeThumbnailView.OnInitializedListener {

    public MyDukan mApp;
    private Context mContext;
    private Map<View, YouTubeThumbnailLoader> mLoaders;
    public List<String> ITEMS = new ArrayList<String>();


    /**
     * A map of YouTube videos, by ID.
     */

    ArrayList<Videos> videosList = new ArrayList<>();


    public VideoListAdapter(final Context context, ArrayList<Videos> mITEM_MAP) {
        mContext = context;
        mLoaders = new HashMap<>();
        videosList = mITEM_MAP;
        mApp = (MyDukan) mContext.getApplicationContext();
    }

    @Override
    public int getCount() {
        return  videosList.size();
    }

    @Override
    public Object getItem(int position) {
        return videosList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        VideoHolder holder;

        //The item at the current position
        final Videos item = videosList.get(position);

        if (convertView == null) {
            //Create the row
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_layout, parent, false);

            //Create the video holder
            holder = new VideoHolder();

            //Set the title
            holder.title = (TextView) convertView.findViewById(R.id.textView_title);
            holder.title.setText(mApp.getUtils().toCamelCase(item.getVideoINFO()));

            //Initialise the thumbnail
            holder.thumb = (YouTubeThumbnailView) convertView.findViewById(R.id.imageView_thumbnail);
            holder.thumb.setTag(item.getVideoID());
            holder.thumb.initialize(mContext.getString(R.string.DEVELOPER_KEY), this);

            convertView.setTag(holder);
        } else {
            //Create it again
            holder = (VideoHolder) convertView.getTag();
            final YouTubeThumbnailLoader loader = mLoaders.get(holder.thumb);

            if (item != null) {
                //Set the title
                holder.title.setText(item.getVideoINFO());

                //Setting the video id can take a while to actually change the image
                //in the meantime the old image is shown.
                //Removing the image will cause the background color to show instead, not ideal
                //but preferable to flickering images.
                holder.thumb.setImageBitmap(null);

                if (loader == null) {
                    //Loader is currently initialising
                    holder.thumb.setTag(item.getVideoID());
                } else {
                    //The loader is already initialised
                    //Note that it's possible to get a DeadObjectException here
                    try {
                        loader.setVideo(item.getVideoID());
                    } catch (IllegalStateException exception) {
                        //If the Loader has been released then remove it from the map and re-init
                        mLoaders.remove(holder.thumb);
                        holder.thumb.initialize(mContext.getString(R.string.DEVELOPER_KEY), this);

                    }
                }

            }
        }
        return convertView;
    }


    @Override
    public void onInitializationSuccess(YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
        mLoaders.put(view, loader);
        loader.setVideo((String) view.getTag());
    }

    @Override
    public void onInitializationFailure(YouTubeThumbnailView thumbnailView, YouTubeInitializationResult errorReason) {
        final String errorMessage = errorReason.toString();
        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
    }


    static class VideoHolder {
        YouTubeThumbnailView thumb;
        TextView title;
    }
}
