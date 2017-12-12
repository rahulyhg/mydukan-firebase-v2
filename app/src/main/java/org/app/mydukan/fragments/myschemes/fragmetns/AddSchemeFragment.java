package org.app.mydukan.fragments.myschemes.fragmetns;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import org.app.mydukan.R;

import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;


import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by rojesharunkumar on 15/11/17.
 */

public class AddSchemeFragment extends Fragment {

    private MySchemesActivity mActivity;
    private String title;
    private AutoCompleteTextView mCategory,mTxtSchemeTitle,mTxtSchemeDescription;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MySchemesActivity) context;
    }

    public static AddSchemeFragment newInstance(String category) {

        Bundle args = new Bundle();
        args.putString("_category",category);
        AddSchemeFragment fragment = new AddSchemeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if(getArguments() != null){
            title = getArguments().getString("_category");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_scheme,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCategory = (AutoCompleteTextView) view.findViewById(R.id.lbl_category);
        mTxtSchemeTitle = (AutoCompleteTextView) view.findViewById(R.id.edt_scheme_title);
        mTxtSchemeDescription = (AutoCompleteTextView) view.findViewById(R.id.edt_scheme_desc);

        if(!TextUtils.isEmpty(title)){
            mCategory.setText(title.trim());
        }

        getProductList();


    }


    private void getProductList() {

        try {
            ApiManager.getInstance(getApplicationContext()).getSupplierProductList(MySelectedSchemesHelper.getInstance().getCurrentSupplier(),
                    MySelectedSchemesHelper.getInstance().getCategoryId(title),
                    null, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onFailure(String response) {

                        }
                    });
        } catch (Exception e) {

        }
    }

}
