package org.app.mydukan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ProductsAdapter extends
        RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    public final String VIEW = "VIEW";
    public final String ADD_CART = "ADD_CART";
    private List<Product> mProductData;
    private Context mContext;
    private Product currentProduct;
    private MyDukan mApp;
    private ProductAdapterListener mListener;
    private Boolean isCartEnabled;
    private String mSupplierID="";

    public ProductsAdapter(Context context, ProductAdapterListener productListener,String supplierID) {
        mContext = context;
        mProductData = new ArrayList<Product>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = productListener;
        mSupplierID=supplierID;

    }
    public ProductsAdapter(Context context, ProductAdapterListener productListener) {
        mContext = context;
        mProductData = new ArrayList<Product>();
        mApp = (MyDukan) mContext.getApplicationContext();
        mListener = productListener;

    }
    private ArrayList<Product> getPriceDropProducts(){
        ArrayList<Product> result = new ArrayList<Product>();
        for (Product product : mProductData) {
            if (product.getPriceDrop() != null) {
                result.add(0, product);
            }
            notifyDataSetChanged();
        }
        return result;
    }
    public void setCartFlag(boolean flag){
        isCartEnabled = flag;
    }

    public void clearProduct() {
        mProductData.clear();
    }

    public void addProduct(ArrayList<Product> product) {
        mProductData.addAll(0, product);
        notifyDataSetChanged();
    }

    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;
        contactView = inflater.inflate(R.layout.product_card, parent, false);
        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //clear the holder.
        holder.mProductName.setText("");
        holder.mProductPrice.setText("");
        holder.mProductMOPPrice.setText("");
        holder.mProductMRPPrice.setText("");
        currentProduct = mProductData.get(position);

        holder.mInfobtn.setVisibility(View.GONE);
        holder.mProductLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnProductOpenClick(position,VIEW);
            }
        });
        holder.mInfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnProductOpenClick(position,VIEW);
            }
        });

        holder.mAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnProductOpenClick(position,ADD_CART);
            }
        });

        holder.mClaimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnProductClaimClick(position);
            }
        });

        holder.mProductName.setText(mApp.getUtils().toSameCase(currentProduct.getName().trim().toString()));

        if(mApp.getUtils().getPriceFormat(currentProduct.getMrp())!=null ){
            holder.mProductMRPPrice.setVisibility(View.VISIBLE);
            holder.mProductMRPPrice.setText(currentProduct.getMrp());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String valueMRP=currentProduct.getMrp();
                switch(valueMRP){
                    case "1":
                        holder.mProductMRPPrice.setText("Jan");
                        break;
                    case "2":
                        holder.mProductMRPPrice.setText("Feb");
                        break;
                    case "3":
                        holder.mProductMRPPrice.setText("Mar");
                        break;
                    case "4":
                        holder.mProductMRPPrice.setText("Apr");
                        break;
                    case "5":
                        holder.mProductMRPPrice.setText("May");
                        break;
                    case "6":
                        holder.mProductMRPPrice.setText("Jun");
                        break;
                    case "7":
                        holder.mProductMRPPrice.setText("Jul");
                        break;
                    case "8":
                        holder.mProductMRPPrice.setText("Aug");
                        break;
                    case "9":
                        holder.mProductMRPPrice.setText("Sep");
                        break;
                    case "10":
                        holder.mProductMRPPrice.setText("Oct");
                        break;
                    case "11":
                        holder.mProductMRPPrice.setText("Nov");
                        break;
                    case "12":
                        holder.mProductMRPPrice.setText("Dec");
                        break;
                    default:
                        holder.mProductMRPPrice.setText("");
                        break;
                }


            }
        }
        if(mSupplierID!=null){
            if(mSupplierID.equals("RcJ1L4mWaZeIe2wRO3ejHOmcSxf2")){
                holder.mProductMOPPrice.setVisibility(View.GONE);
                holder.mProductPrice.setVisibility(View.GONE);
            }else{
                holder.mProductPrice.setText(mApp.getUtils().getPriceFormat(currentProduct.getPrice()));
                if(mApp.getUtils().getPriceFormat(currentProduct.getMop())!=null){
                    holder.mProductMOPPrice.setVisibility(View.VISIBLE);
                    holder.mProductMOPPrice.setText("MOP: "+mApp.getUtils().getPriceFormat(currentProduct.getMop()));
                }else{
                    holder.mProductMOPPrice.setVisibility(View.GONE);
                }
            }
        }else{
            holder.mProductPrice.setText(mApp.getUtils().getPriceFormat(currentProduct.getPrice()));
            if(mApp.getUtils().getPriceFormat(currentProduct.getMop())!=null){
                holder.mProductMOPPrice.setVisibility(View.VISIBLE);
                holder.mProductMOPPrice.setText("MOP: "+mApp.getUtils().getPriceFormat(currentProduct.getMop()));
            }else{
                holder.mProductMOPPrice.setVisibility(View.GONE);
            }
        }

        if(currentProduct.getIsnew()==true){
            holder.mNewArrival.setVisibility(View.VISIBLE);
        }else{
            holder.mNewArrival.setVisibility(View.GONE);
        }

        //======showing the product price platform.=====

       if (currentProduct.getmPlatforms()!=null && currentProduct.getmPlatforms().size()>0){
            holder.mpricePlatform.setVisibility(View.VISIBLE);
            String comparePrice="";
            Set<String> platformName=currentProduct.getmPlatforms().keySet();
            for (String mplatform:currentProduct.getmPlatforms().keySet()) {
                comparePrice=comparePrice+mplatform+": "+"₹ "+currentProduct.getmPlatforms().get(mplatform)+" | ";
            }
            holder.mpricePlatform.setText( mApp.getUtils().toCamelCase(comparePrice));
            holder.mpricePlatform.setSelected(true);
        }else{
            holder.mpricePlatform.setVisibility(View.GONE);
        }

        //=================================
        if (currentProduct.getPriceDrop() != null && currentProduct.getPriceDrop().getStartdate() != 0l) {
            holder.mStatusLayout.setVisibility(View.VISIBLE);
            holder.mStatusLabel.setVisibility(View.VISIBLE);
            holder.mClaimBtn.setVisibility(View.VISIBLE);
            holder.mStatusLabelDate.setVisibility(View.VISIBLE);
            holder.mStatusPrice.setVisibility(View.INVISIBLE);

            holder.mStatusLabel.setText(R.string.label_price_drop);
            holder.mStatusLabelDate.setText("Effective From " + String.valueOf(mApp.getUtils().dateFormatter(
                    currentProduct.getPriceDrop().getStartdate(), "dd-MM-yy")));

            if(!mApp.getUtils().isStringEmpty(currentProduct.getPriceDrop().getDropamount())){
                holder.mStatusPrice.setVisibility(View.VISIBLE);
                holder.mStatusPrice.setText(mApp.getUtils().getPriceFormat(currentProduct.getPriceDrop().getDropamount()));
            }
            holder.mStatusPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnProductClaimClick(position);
                }
            });
            holder.mStatusLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnProductClaimClick(position);
                }
            });
            holder.mStatusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.OnProductClaimClick(position);
                }
            });

        } else if (currentProduct.getOffer() != null && currentProduct.getOffer().getStartdate() != 0l && currentProduct.getOffer().getEnddate() != 0l) {
            if (currentProduct.getOffer().getStartdate() != 0l && currentProduct.getOffer().getEnddate() != 0l) {
                holder.mStatusLayout.setVisibility(View.VISIBLE);
                holder.mStatusLabel.setVisibility(View.VISIBLE);
                holder.mStatusLabelDate.setVisibility(View.VISIBLE);
                holder.mStatusPrice.setVisibility(View.INVISIBLE);
                holder.mClaimBtn.setVisibility(View.GONE);

                holder.mStatusLabel.setText(R.string.label_offer);
                holder.mStatusLabelDate.setText("Effective From "
                        + String.valueOf(mApp.getUtils().dateFormatter(currentProduct.getOffer().getStartdate(), "dd-MM-yy")) +" to "
                        + String.valueOf(mApp.getUtils().dateFormatter(currentProduct.getOffer().getEnddate(), "dd-MM-yy")));

                if(!mApp.getUtils().isStringEmpty(currentProduct.getOffer().getOfferamount())){
                    holder.mStatusPrice.setVisibility(View.VISIBLE);
                    holder.mStatusPrice.setText(mApp.getUtils().getPriceFormat(currentProduct.getOffer().getOfferamount()));
                }
            }
        }else if(currentProduct.getIsnew()){
            holder.mStatusLayout.setVisibility(View.VISIBLE);
            holder.mStatusLabel.setVisibility(View.VISIBLE);
            holder.mStatusLabelDate.setVisibility(View.GONE);
            holder.mClaimBtn.setVisibility(View.GONE);
            holder.mStatusPrice.setVisibility(View.INVISIBLE);

            holder.mStatusLabel.setText(R.string.label_new_product);
            holder.mStatusLabel.setBackgroundColor(Color.rgb(236,39,35));
        } else {
            holder.mStatusLayout.setVisibility(View.INVISIBLE);
            holder.mStatusLabel.setVisibility(View.INVISIBLE);
            holder.mStatusLabelDate.setVisibility(View.GONE);
            holder.mStatusPrice.setVisibility(View.GONE);
            holder.mClaimBtn.setVisibility(View.GONE);
        }

        if(isCartEnabled){
            holder.mAddToCart.setVisibility(View.VISIBLE);
            holder.mStockCount.setVisibility(View.VISIBLE);

            if(currentProduct.getStockremaining() <= 0){
                holder.mAddToCart.setVisibility(View.INVISIBLE);
                holder.mStockCount.setText("No Stock");
                holder.mStockCount.setVisibility(View.GONE);
            } else if(currentProduct.getStockremaining() <= 5){
                holder.mStockCount.setText("Limited Stock");
            } else {
                holder.mStockCount.setVisibility(View.GONE);
            }
        } else {
            holder.mAddToCart.setVisibility(View.GONE);
            holder.mStockCount.setVisibility(View.GONE);
            holder.mCartoptionLayout.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mProductData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mProductLayout;
        private LinearLayout  mStatusLayout;
        private LinearLayout  mNewArrivalLayout;
        private TextView mProductName;
        private TextView mProductPrice;
        private TextView mProductMOPPrice;
        private TextView mProductMRPPrice;
        private TextView mStatusPrice;
        private TextView mStatusLabel;
        private TextView mStatusLabelDate;
        private TextView mStockCount;
        private TextView mNewArrival;
        private TextView mpricePlatform;
        private LinearLayout mAddToCart;
        private LinearLayout mCartoptionLayout;//optionLayout
        private ImageButton mInfobtn;
        private Button mClaimBtn;

        public ViewHolder(final View itemView) {
            super(itemView);
            mProductLayout = (RelativeLayout) itemView.findViewById(R.id.product_table);
            mProductName = (TextView) itemView.findViewById(R.id.modelName);
            mStatusPrice = (TextView) itemView.findViewById(R.id.discountPrice);
            mStatusLayout = (LinearLayout) itemView.findViewById(R.id.discountLayout);
            //mNewArrivalLayout= (LinearLayout) itemView.findViewById(R.id.layout_NewArrival);
            mStatusLabel = (TextView) itemView.findViewById(R.id.product_label);
            mStatusLabelDate = (TextView) itemView.findViewById(R.id.product_label_date);
            mStockCount = (TextView) itemView.findViewById(R.id.stockCount);
            mAddToCart = (LinearLayout) itemView.findViewById(R.id.addtocartlayout);
            mCartoptionLayout = (LinearLayout) itemView.findViewById(R.id.optionLayout);
            mInfobtn = (ImageButton) itemView.findViewById(R.id.infobtn);
            mNewArrival= (TextView) itemView.findViewById(R.id.newArrival);
            mClaimBtn = (Button) itemView.findViewById(R.id.claimBtn);
            mProductPrice = (TextView) itemView.findViewById(R.id.price);
            mProductMOPPrice = (TextView) itemView.findViewById(R.id.mop_price);
            mProductMRPPrice = (TextView) itemView.findViewById(R.id.mrp_price);
            mpricePlatform = (TextView) itemView.findViewById(R.id.pricePlatform);

        }
    }

    public interface ProductAdapterListener {
        void OnProductOpenClick(int position, String type);
        void OnProductClaimClick(int position);
    }
}
