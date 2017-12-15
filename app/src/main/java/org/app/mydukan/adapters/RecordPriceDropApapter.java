package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.activities.RecordPriceDropActivity;
import org.app.mydukan.data.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MyDukan on 13-12-2017.
 */

public class RecordPriceDropApapter extends RecyclerView.Adapter<RecordPriceDropApapter.ViewHolder>{

    private HashMap<String, Integer> map;
    private Context mContext;
    private ArrayList<Record> recordArrayList = new ArrayList<>();
    private List<String> brandList = new ArrayList<>();

    public RecordPriceDropApapter(HashMap<String, Integer> map, Context mContext, ArrayList<Record> recordArrayList, List<String> brandList) {
        this.map = map;
        this.mContext = mContext;
        this.recordArrayList = recordArrayList;
        this.brandList = brandList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;
        contactView = inflater.inflate(R.layout.activity_pricedrop_brands, parent, false);
        // Return a new holder instance
        return new RecordPriceDropApapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        
        switch (brandList.get(position)) {
            case "-KTTV8BvY0PpRUOEyKOo":
                //SAMSUNG = -KTTV8BvY0PpRUOEyKOo
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_samsung));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KZkiZ785mct3R2ROHlf":
                //NEW LAUNCH= -KZkiZ785mct3R2ROHlf
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_newlaunch));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KX41ilBK4hjaSDIV419":
                //PRICE DROP = -KX41ilBK4hjaSDIV419
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_pricedrop));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-Ksr9awbFUvRMz-Uu1i3":
                //     UPCOMING LAUNCH     = -Ksr9awbFUvRMz-Uu1i3
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_upcominglaunch));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KuxmE7rc7w4WpFYKWE5":
                // IPHONE = -KuxmE7rc7w4WpFYKWE5 ,-KuxmE7rc7w4WpFYKWE5
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_iphone));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KTmmfHzpn5Nn-ZZ0N1A":
                // GIONEE=  -KTmmfHzpn5Nn-ZZ0N1A
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_gionee));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KTwazqP9cmccZNbt9hb":
                // LAVA = -KTwazqP9cmccZNbt9hb
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lava));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KrovlBYSgcM3NA8GxAg":
                // COMIO = -KrovlBYSgcM3NA8GxAg
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_comio));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KThq1qTj7upEbf5OAGV":
                // ASUS = -KThq1qTj7upEbf5OAGV
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_asus));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KTwlJGRrjcnqumXCzGV":
                // LENOVO =-KTwlJGRrjcnqumXCzGV
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lenovo));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU2hE65nFcTF0eYa0u_":
                // VIVO= -KU2hE65nFcTF0eYa0u_
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_vivo));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KUIMuoS_jziNt_deTkv":
                //     ITEL    = -KUIMuoS_jziNt_deTkv
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_itel));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KTx_nTRtIoZtqG_ErSD":
                // ZIOX =-KTx_nTRtIoZtqG_ErSD
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_ziox));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KU0J03fDBO4Zrsb3tTz":
                // MICROMAX SMART PHONES=  -KU0J03fDBO4Zrsb3tTz
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_micromax));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU1r_NUU5DdFPJLS1C2":
                //XIAOMI= -KU1r_NUU5DdFPJLS1C2
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_xiaomi));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KU23bD-T28Bx3AF87K0":
                // MOTOROLA = -KU23bD-T28Bx3AF87K0
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_motorola));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KTxmDYOCC_MxyRtu83V":
                //OPPO= -KTxmDYOCC_MxyRtu83V
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_oppo));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KUSXXe-dC52lGIcD16J":
                //ZEN
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_zen));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
            
            case "-KTlvtThlXhnokm3hux3":
                //INTEX= -KTlvtThlXhnokm3hux3
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_intex));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KTzALKLYilcvlcnM1Z0":
                // CELKON
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_celkon));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KUFxA23cmu4baJ3BL0P":
                //NOKIA
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_nokia));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KkvKrWFqJR7tpaAb7Sy":
                //TECNO
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_tecno));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-KTmMpmpYcsv3XVjr3Nl":
                //HTC
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_htc));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;

            case "-Kjg5QfunwoNhVUAXz-z":
                //Google Pixel
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_pixel));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KpY9KtzUtnRFFoVLjXO":
                //SPICE= -KpY9KtzUtnRFFoVLjXO
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_spice));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KqmnwSZdtFqRtjVqeDh":
                //COOLPAD
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_coolpad));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-Kum8bSbB3Qq5OSVbUM8":
                //SMARTRON
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_smartron));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU1tkMWpVGdbiozbdMk":
                //MICROMAX FEATURE PHONES
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_micromax_featurephones));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU2u7z3XVfFrHQiiXAg":
                //PANASONIC
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_panasonic));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU1m3xWbMbRpybhTFyb":
                //INFOCUS
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_infocus));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KTzBkfTUVuHcjKL-_nG":
                //HUAWEI
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_huawei));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU1oqGw6Vvs9E5R4AAk":
                //LG
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lg));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KTzBNFwVsjGDHaYfR9A":
                //LEPHONE =-KTzBNFwVsjGDHaYfR9A
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lephone));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KreSsMPOYr7fsSqyB8C":
                //MAFE=-KreSsMPOYr7fsSqyB8C
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mafe));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KepYPQdF-uJOEscF0lm":
                //m-tech
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mtech));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-Ksc37Nj0Ye0ZrnbgK8D":
                //JIVI
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_jivi));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU7f5qx3Oe-XxEjfbZk":
                //SONY
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_sony));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KUL97n-vNpLoNW2AK_q":
                //-KUL97n-vNpLoNW2AK_q =KARBONN
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_karbonn));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KqLd12Ir-CII-o28yST":
                //LEMON
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lemon));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KUQDt2SABFB5SMXXRk6":
                //JIO LYF
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_lyf));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-Kl2tmit89SZyWuyOaNo":
                //SAMSUNG ACCESSORIES
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_samsung_accessories));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KscjmvCSVlEY6WSloaS":
                //ORAIMO Accessories
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_oraimo));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU6o-IaO1J_H0MDNNyf":
                //IBALL
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_iball));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KU1thpXX242Q7NSEzcL":
                //YU
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_yu));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KpJlnV76WbYmrcLHVxh":
                //Mozomaxx
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mozomaxx));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KVzHyaGhW1O7FdEjYuo":
                //ACER
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_acer));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KfQXgzUi2mh4qfHWlI7":
                //EDGE MOBILE
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_mobileedge));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KULvmv5RjPypNDXYXvR":
                //VIDEOCON
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_videocon));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KYXXJ0Q3io-JtGMe8bb":
                //ZOPO
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_zopo));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KkBlkABWX7NtvkQgtcL":
                //DETEL
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_detel));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KkUFMpMIXXPBl-yFPGh":
                //EXMART
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_exmart));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            case "-KusMlizwhhlJ3Clxjtb":
                //VOTO
                holder.brandLogo.setBackground(ContextCompat.getDrawable(mContext, R.drawable.logo_voto));
                holder.priceDropCount.setText(String.valueOf(map.get(brandList.get(position))));
                break;
                
            default:
                
                break;
        }
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView brandLogo;
        public TextView priceDropCount;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.brandLogo = (ImageView) itemView.findViewById(R.id.brand_logo);
            this.priceDropCount = (TextView) itemView.findViewById(R.id.no_of_records);
        }
    }
}