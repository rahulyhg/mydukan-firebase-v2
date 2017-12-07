package org.app.mydukan.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.common.internal.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.PlatForm_Info;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static org.app.mydukan.activities.ProductDescriptionActivity.fullpage;
import static org.app.mydukan.activities.ProductDescriptionActivity.mApp;

/**
 * Created by Shivu on 31-07-2017.
 */


public class PricePlatformAdapter extends BaseAdapter {
    private final ArrayList mData;
    private Context mContext;
    private LayoutInflater inflater;
    private MyDukan mApp;
    HashMap<String, HashMap<String, String>> mItem;
    public PricePlatformAdapter(Context context, HashMap<String, HashMap<String, String>> map) {
        this. mContext = context;
        inflater = LayoutInflater.from(mContext);
        mData = new ArrayList();
        mItem= new HashMap<>();
        mApp = (MyDukan) context.getApplicationContext();

        mData.addAll(map.keySet());
        mItem=map;
        }

    public class ViewHolder {
        TextView platformName;
        TextView platformPrice;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return (String) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
       final String item = getItem(position);



        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_priceplatformfragment, null);
            holder.platformName = (TextView) view.findViewById(R.id.platformName);
            holder.platformPrice = (TextView) view.findViewById(R.id.platformPrice);

            view.setTag(holder);

        } else {

            holder = (ViewHolder) view.getTag();

        }
        if(item!=null) {

            holder.platformName.setText(mApp.getUtils().toCamelCase(item));
            if(mItem.size()>0){
                String mprice= mItem.get(item).get("price");
                if(mprice!=null){

                    holder.platformPrice.setText("₹ "+mItem.get(item).get("price"));
                }
            }else{
                holder.platformPrice.setText("-");
            }






        /* if(!mApp.getUtils().isStringEmpty(value)){
                if(attKey.equalsIgnoreCase("androidversion")){
                    othersStr += value + " " + getString(R.string.androidversion);
                } else if(attKey.equalsIgnoreCase("cameramegapixel")){
                    othersStr += value + " " + getString(R.string.camera);
                } else if(attKey.equalsIgnoreCase("displaysize")){
                    othersStr += value + " " + getString(R.string.size);
                } else if(attKey.equalsIgnoreCase("ramrom")){
                    othersStr += value + " " + getString(R.string.ram);
                }
            }*/

        /*
        if(item!=null) {
            switch (item.getKey()) {
                case "connectivity":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+": ");
                    ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());

                    break;
                case "displaysize":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(("Display Size")+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue()+" Inches");
                    break;
                case "memory":
                    ImageView imageView= (ImageView) result.findViewById(R.id.img2);
                    imageView.setVisibility(View.GONE);
               */
        /*
         if(item.getValue().contains("&")  && item.getValue().contains("#")){
                            Map<String, String> parsedMap=new HashMap<>();
                            splitString(item.getValue());

                            //mData.notify();
                        }else {
                            ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey()) + " (RAM/ROM) : ");
                            ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());
                        }*//*

                    break;
                case "androidversion":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(("Android Version")+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());
                    break;
                case "cameramegapixel":
                    ((TextView) result.findViewById(android.R.id.text1)).setText("Camera Back/Front :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue()+" MP");
                    break;
                default:
                {
                    ((TextView) result.findViewById(android.R.id.text1)).setText(item.getKey()+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());
                }
                break;
            }
        }

    */



         /*   switch (item.getKey()) {

                case "amazon":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+": ");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());

                    break;
     case "snapdeal":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());
                    break;
                case "paytm":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());
                    break;
                case "lockthedeal":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());
                    break;
                case "shopclues":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());
                    break;

                case "flipkart":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());
                    break;

                case "gadget360":
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());
                    break;

                default:
                {
                    ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+" :");
                    ((TextView) result.findViewById(android.R.id.text2)).setText("₹ "+item.getValue());
                }
                break;

            }*/
        }

        /*holder.platformPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "You Clicked : " + ("₹ "+mItem.get(item).get("price")), Toast.LENGTH_SHORT).show();
            }
        });*/
           /* // TODO replace findViewById by ViewHolder
            ((TextView) result.findViewById(android.R.id.text1)).setText(mApp.getUtils().toCamelCase(item.getKey())+": ");
            ((TextView) result.findViewById(android.R.id.text2)).setText(item.getValue());*/

        return view;
    }

}
