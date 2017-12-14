package org.app.mydukan.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.fragments.ProductFragment;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;

public class PriceDropModels extends BaseActivity implements ProductFragment.ProductFragmentListener {

    ImageView back;
    TextView heading;
    LinearLayout container;
    SupplierBindData mSupplier;
    ArrayList<Product> products;
    String brandName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_drop_models);

        //Initializing Widgets
        back = (ImageView) findViewById(R.id.back);
        heading = (TextView) findViewById(R.id.heading);
        container = (LinearLayout) findViewById(R.id.container);
        products = new ArrayList<>();

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
        fragment.setArguments(product);
        fragment.setData(products,false,mSupplier);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
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
        Intent intent = new Intent(PriceDropModels.this, AddIMEIActivity.class);
        intent.putExtra(AppContants.SUPPLIER_ID, mSupplier.getId());
        intent.putExtra(AppContants.PRODUCT, product);
        intent.putExtra(AppContants.CATEGORY_ID,"");
        intent.putExtra(AppContants.SUPPLIER, mSupplier);
        startActivity(intent);
    }
}
