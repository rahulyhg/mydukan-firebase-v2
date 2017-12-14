package org.app.mydukan.fragments.myschemes.calculator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.fragments.myschemes.fragmetns.BaseFragment;
import org.app.mydukan.fragments.myschemes.fragmetns.MySelectedSchemesHelper;
import org.app.mydukan.fragments.myschemes.model.Device;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by rojesharunkumar on 20/10/17.
 */


public class AddSchemeFrag extends BaseFragment implements RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener, View.OnClickListener {


    String[] models = {"- Select Model -"};
    private AutoCompleteTextView mTxtDelarPrice, mEdtQuantity, mEdtTarget, mEdtValue, mCalulatedScheme, mSelectedScheme;
    private RadioGroup mRg, mRgScheme;
    private float mdealPrice = 0.0f;
    private Button btnAdd;
    private MySchemesActivity mMainActivity;

    private DatabaseReference modelRef;
    private String brandId, brandName;
    private ArrayAdapter<String> adapter;
    private HashMap<String, ArrayList<Product>> mProductList;
    ArrayList<Product> mModelsList;
    private String[] category;
    private ArrayAdapter<String> mCategoryAdapter;
    private ArrayAdapter<String> mModelAdapter;
    private Spinner mSpnrCategory;
    private Spinner mSpnrModel;


    public static AddSchemeFrag newInstance(String id, String name) {

        Bundle args = new Bundle();
        args.putString("brand_id", id);
        args.putString("brand_name", name);

        AddSchemeFrag fragment = new AddSchemeFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            brandId = getArguments().getString("brand_id");
            brandName = getArguments().getString("brand_name");

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MySchemesActivity) context;
        mMainActivity.showProgress();
    }

    private void getProductList() {

        try {
            ApiManager.getInstance(getActivity()).getSupplierProductList(MySelectedSchemesHelper.getInstance().getCurrentSupplier(),
                    brandId,
                    null, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            mProductList = (HashMap<String, ArrayList<Product>>) data;
                            if (mProductList != null && mProductList.size() > 0) {
                                category = new String[mProductList.size() + 1];
                                category[0] = "- Select Category -";
                                int i = 1;
                                for (String key : mProductList.keySet()) {
                                    category[i++] = key;
                                }
                                setAdapter(category);
                                models[0] = new String("- Select Model -");
                                setModelAdapter(models);
                            }
                            mMainActivity.dismissProgress();
                        }

                        @Override
                        public void onFailure(String response) {
                            mMainActivity.dismissProgress();
                        }
                    });
        } catch (Exception e) {

        }
    }


    public void setAdapter(String[] category) {
        mCategoryAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, category);
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrCategory.setOnItemSelectedListener(this);
        mSpnrCategory.setAdapter(mCategoryAdapter);
    }


    public void setModelAdapter(String[] models) {
        mModelAdapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, models);
        mModelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrModel.setOnItemSelectedListener(this);
        mSpnrModel.setAdapter(mModelAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calculator_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMainActivity.showProgress();
        setCalculatorformUI(view);
    }

    private void setCalculatorformUI(View view) {
        mSpnrCategory = (Spinner) view.findViewById(R.id.sprn_category);
        mSpnrModel = (Spinner) view.findViewById(R.id.sprn_model);
        String[] category = {"- Select Cateogry -"};
        setAdapter(category);
        mTxtDelarPrice = (AutoCompleteTextView) view.findViewById(R.id.edt_dp);
        mRg = (RadioGroup) view.findViewById(R.id.rg);
        mRg.setOnCheckedChangeListener(this);
        mRgScheme = (RadioGroup) view.findViewById(R.id.rg_scheme);
        mRgScheme.setOnCheckedChangeListener(this);
        mEdtValue = (AutoCompleteTextView) view.findViewById(R.id.edt_value);
        mEdtQuantity = (AutoCompleteTextView) view.findViewById(R.id.edt_quantity);
        mEdtTarget = (AutoCompleteTextView) view.findViewById(R.id.edt_target);
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);


        // Fetch all Models
        getProductList();

        mEdtQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    calculateValue();

                    return true;
                }
                return false;
            }
        });
        mEdtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateValue();
            }
        });
        mSelectedScheme = (AutoCompleteTextView) view.findViewById(R.id.edt_selected_scheme);
        mCalulatedScheme = (AutoCompleteTextView) view.findViewById(R.id.edt_calulated_scheme);

        mSelectedScheme.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    calulateScheme();
                }
                return false;
            }
        });
        mSelectedScheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calulateScheme();
            }
        });

    }


    private void calculateValue() {

        if (!TextUtils.isEmpty(mEdtQuantity.getText().toString().trim())
                && !TextUtils.isEmpty(mEdtTarget.getText().toString())) {
            Float res = Float.parseFloat(mEdtQuantity.getText().toString())
                    * Float.parseFloat(mTxtDelarPrice.getText().toString());
            mEdtValue.setText(res + "");
        } else {
            mEdtValue.setText(0 + "");
        }

    }

    private void setModelsAdapter() {
        if (adapter != null) {
            adapter = null;
        }
        adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_item, models);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrModel.setOnItemSelectedListener(this);
        mSpnrModel.setAdapter(adapter);

    }

    float dp = 0.0f;

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {

        switch (i) {
            case R.id.rb_withvat:
                mTxtDelarPrice.setText(mdealPrice + (mdealPrice * 88) / 10000 + "");
                break;
            case R.id.lrb_withoutvat:
                mTxtDelarPrice.setText(mdealPrice + "");
                break;
            case R.id.rb_perhandset:
                mSelectedScheme.setHint(getResources().getString(R.string.prompt_perhandset));
                mSelectedScheme.setText("");
                break;
            case R.id.rb_percent:
                mSelectedScheme.setHint(getResources().getString(R.string.percent));
                mSelectedScheme.setText("");
                break;
        }
    }

    private void calulateScheme() {
        Float incentiveAmount = 0.0f;

        if (mRgScheme.getCheckedRadioButtonId() == R.id.rb_perhandset) {
            if (!TextUtils.isEmpty(mEdtQuantity.getText().toString().trim())
                    && !TextUtils.isEmpty(mSelectedScheme.getText().toString())) {
                incentiveAmount = Float.parseFloat(mEdtQuantity.getText().toString().trim())
                        * Float.parseFloat(mSelectedScheme.getText().toString());
            } else {
                incentiveAmount = 0.0f;
            }
        } else {
            if (!TextUtils.isEmpty(mEdtValue.getText().toString().trim())
                    && !TextUtils.isEmpty(mSelectedScheme.getText().toString())) {

                incentiveAmount = Float.parseFloat(mEdtValue.getText().toString()) * Float.parseFloat(mSelectedScheme.getText().toString()) / 100;
            } else {
                incentiveAmount = 0.0f;
            }
        }

        mCalulatedScheme.setText(incentiveAmount + "");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        switch (adapterView.getId()) {
            case R.id.sprn_category:
                mSpnrModel.setVisibility(View.VISIBLE);
                if (pos != 0) {
                    mModelsList = mProductList.get(category[pos]);
                    models = new String[mModelsList.size() + 1];
                    models[0] = new String("- Select Model -");
                    for (int i = 1; i < mModelsList.size() + 1; i++) {
                        models[i] = mModelsList.get(i - 1).getName();
                    }
                    setModelAdapter(models);
                }
                break;
            case R.id.sprn_model: {
                if (pos != 0) {
                    mdealPrice = mModelsList.get(pos - 1).getPriceInt();
                    if (mRg.getChildAt(0).isSelected()) {
                        mTxtDelarPrice.setText(mdealPrice + "");
                    } else {
                        mTxtDelarPrice.setText((mdealPrice + (mdealPrice * 88) / 10000) + "");
                    }
                }
            }
            break;
            default:
                break;

        }
    }

    private float getDealPrice(String selectedItem) {
/*
        Model selectedModel = null;
        for (Model model : modelsArrayList) {
            if (selectedItem.equalsIgnoreCase(model.getModelName())) {
                selectedModel = model;
                break;
            }
        }
        if (selectedModel == null)
            return 0;
        return Float.parseFloat(selectedModel.getModelPrice());
*/
        return 0;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addModel();
                break;
        }


    }

    private void addModel() {
        if (mSpnrCategory.getSelectedItem().toString().equalsIgnoreCase(category[0])) {
            Toast.makeText(getActivity(), "Select Category", Toast.LENGTH_SHORT).show();
        } else if (mSpnrModel.getSelectedItem().toString().equalsIgnoreCase(models[0])) {
            Toast.makeText(getActivity(), "Select Model", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mEdtTarget.getText().toString())) {
            mEdtTarget.setError("Enter target");
        } else if (TextUtils.isEmpty(mEdtQuantity.getText().toString())) {
            mEdtQuantity.setError("Enter quantity");
        } else if (TextUtils.isEmpty(mSelectedScheme.getText().toString())) {
            mSelectedScheme.setError("Enter per handset value / %");
        } else {
            Device device = new Device();
            device.setCategory(mSpnrCategory.getSelectedItem().toString().trim());
            device.setModel(mSpnrModel.getSelectedItem().toString().trim());
            device.setTarget(mEdtTarget.getText().toString().trim());
            device.setQuantity(mEdtQuantity.getText().toString().trim());
            device.setDp(mTxtDelarPrice.getText().toString().trim());
            device.setValue(mEdtValue.getText().toString().trim());
            if (mRg.getChildAt(0).isSelected()) {
                device.setType(true);
            } else {
                device.setType(false);
            }
            device.setPercent(mSelectedScheme.getText().toString().trim());
            device.setIncentiveAmount(mCalulatedScheme.getText().toString().trim());

            DeviceHelper.getInstance().addDevice(device);

        }
        mMainActivity.popCurrentFragment();
        mMainActivity.notifyListDataChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

