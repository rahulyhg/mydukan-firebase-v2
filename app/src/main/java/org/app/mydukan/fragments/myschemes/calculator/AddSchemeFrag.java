package org.app.mydukan.fragments.myschemes.calculator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.fragments.myschemes.fragmetns.BaseFragment;
import org.app.mydukan.fragments.myschemes.model.Device;


/**
 * Created by rojesharunkumar on 20/10/17.
 */


public class AddSchemeFrag extends BaseFragment implements RadioGroup.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener, View.OnClickListener {


    private AppCompatSpinner spnrModles;
    String[] models = {"- Select Model -"};
    private AutoCompleteTextView mTxtDelarPrice, mEdtQuantity, mEdtTarget, mEdtValue, mCalulatedScheme, mSelectedScheme;
    private RadioGroup mRg, mRgScheme;
    private float mdealPrice = 0.0f;
    private Button btnAdd;
    private MySchemesActivity mMainActivity;
    private ValueEventListener modelListener;

    private DatabaseReference modelRef;
    private String brandId, brandName;
    private ArrayAdapter<String> adapter;


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
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        spnrModles = (AppCompatSpinner) view.findViewById(R.id.spnr_selectmodel);
        setModelsAdapter();
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

        mMainActivity.dismissProgress();

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
        spnrModles.setOnItemSelectedListener(this);
        spnrModles.setAdapter(adapter);

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.spnr_selectmodel: {

                mdealPrice = getDealPrice((String) spnrModles.getSelectedItem());
                if (mRg.getChildAt(0).isSelected()) {
                    mTxtDelarPrice.setText(mdealPrice + "");
                } else {
                    mTxtDelarPrice.setText((mdealPrice + (mdealPrice * 88) / 10000) + "");
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
return  0;
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
        if (spnrModles.getSelectedItem().toString().equalsIgnoreCase(models[0])) {
            Toast.makeText(getActivity(), "Select Model", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mEdtTarget.getText().toString())) {
            mEdtTarget.setError("Enter target");
        } else if (TextUtils.isEmpty(mEdtQuantity.getText().toString())) {
            mEdtQuantity.setError("Enter quantity");
        } else if (TextUtils.isEmpty(mSelectedScheme.getText().toString())) {
            mSelectedScheme.setError("Enter per handset value / %");
        } else {
            Device device = new Device();
            device.setModel(spnrModles.getSelectedItem().toString().trim());
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

        }/*
        mMainActivity.clearCurrentFragment();
        mMainActivity.notifyListDataChanged();*/

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        modelRef.removeEventListener(modelListener);
    }
}

