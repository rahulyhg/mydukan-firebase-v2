package org.app.mydukan.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.fragments.ProductFragment;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;

import static org.app.mydukan.activities.Search_MyNetworkActivity.FOLLOWERS_ROOT;
import static org.app.mydukan.activities.Search_MyNetworkActivity.FOLLOWING_ROOT;

public class PriceDropModels extends BaseActivity implements ProductFragment.ProductFragmentListener {


    ImageView back;
    TextView heading;
    LinearLayout container;
    SupplierBindData mSupplier;
    ArrayList<Product> products;
    String brandName = "", page = "";
    private MyDukan mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_drop_models);

        //Initializing Widgets
        back = (ImageView) findViewById(R.id.back);
        heading = (TextView) findViewById(R.id.heading);
        container = (LinearLayout) findViewById(R.id.container);
        products = new ArrayList<>();
        mApp = (MyDukan) getApplicationContext();

        //Getting Data from PriceDropActivity
        Bundle bundle = getIntent().getExtras();
        if(bundle.containsKey(AppContants.SUPPLIER)){
            mSupplier = (SupplierBindData) bundle.get(AppContants.SUPPLIER);
        }
        if (bundle.containsKey(AppContants.PRODUCT)) {
            products = (ArrayList<Product>) bundle.getSerializable(AppContants.PRODUCT);
        }
        if(bundle.containsKey(AppContants.BRAND_NAME)){
            brandName = bundle.getString(AppContants.BRAND_NAME);
        }
        if(bundle.containsKey("page")){
            page = bundle.getString("page");
        }

        Log.i("Brand Name: ",brandName);

        heading.setText(brandName.substring(0, 1).toUpperCase() + brandName.substring(1).toLowerCase()+" Price Drops");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ProductFragment fragment = new ProductFragment();
        Bundle product = new Bundle();
        product.putInt(AppContants.POSITION, 1);
        product.putString("page", page);
        fragment.setArguments(product);
        fragment.setData(products,false,mSupplier);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void addProductToCart(Product product) {

    }

    @Override
    public void addProductToClaim(Product product) {
        // Show a dialog to take the key.
        // showTheClaimAlert(product);
        if(page.equalsIgnoreCase("myPriceDrop")) {
            Intent intent = new Intent(PriceDropModels.this, AddIMEIActivity.class);
            intent.putExtra(AppContants.SUPPLIER_ID, mSupplier.getId());
            intent.putExtra(AppContants.PRODUCT, product);
            intent.putExtra(AppContants.CATEGORY_ID, "");
            intent.putExtra(AppContants.BRAND_NAME, brandName);
            intent.putExtra(AppContants.SUPPLIER, mSupplier);
            startActivity(intent);
        }
        else if(page.equalsIgnoreCase("allPriceDrop")){
            setPriceDrop(product.getProductId());
        }
    }

    public void setPriceDrop(final String productId){

        final String uid = mApp.getFirebaseAuth().getCurrentUser().getUid();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference referenceFollowing = FirebaseDatabase.getInstance().getReference().child("/priceDrop/" + uid + "/" + brandName);
        referenceFollowing.child(productId).setValue(true);//adding userid to following list

        /*ApiManager.getInstance(getApplicationContext()).setMyPriceDrop(productId, brandName,  uid, new ApiResult(){
            @Override
            public void onSuccess(Object data) {



            }

            @Override
            public void onFailure(String response) {

            }
        });*/

    }
}
