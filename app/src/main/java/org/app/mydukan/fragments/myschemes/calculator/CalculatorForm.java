package org.app.mydukan.fragments.myschemes.calculator;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wooplr.spotlight.utils.Utils;

import org.app.mydukan.R;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.fragments.myschemes.MySchemesActivity;
import org.app.mydukan.fragments.myschemes.adapter.AddedModelsAdapter;
import org.app.mydukan.fragments.myschemes.adapter.GridSpacingItemDecoration;
import org.app.mydukan.fragments.myschemes.fragmetns.BaseFragment;
import org.app.mydukan.fragments.myschemes.fragmetns.MySelectedSchemesHelper;
import org.app.mydukan.utils.DatePickerFragment;
import org.app.mydukan.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.network.NetworkUtils;


public class CalculatorForm extends BaseFragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener,DatePickerDialog.OnDateSetListener {

    private MySchemesActivity mActivity;
    AppCompatSpinner spnrBrands, spnrSchemes, sprnMonth;

    String[] brand = {"- Select Brand -"};
    // Brands
    private List<List<Scheme>> brandArrayList;
    // Model
    private List<Scheme> schemesArrayList;

    String[] schemes = {"- Select Scheme -"};

    private TextView mTxtOfferDater, mTxtCalculator;

    private Button btnAdd, btnGetSummary;
    private RelativeLayout mDateLayout;
    private RecyclerView recyclerView;
    private AddedModelsAdapter adapter;


    public CalculatorForm() {
        // Required empty public constructor
    }

    public static CalculatorForm newInstance() {
        CalculatorForm fragment = new CalculatorForm();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MySchemesActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.showProgress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calculator_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCalculatorUI();
    }

    private void setCalculatorUI() {
        View view = getView();
        spnrBrands = (AppCompatSpinner) view.findViewById(R.id.sprn_selectbrand);
        setBrandSpinner();
        mTxtCalculator = (TextView) view.findViewById(R.id.lbl_calculator);
        mTxtOfferDater = (TextView) view.findViewById(R.id.txt_date);
        mDateLayout = (RelativeLayout) view.findViewById(R.id.date_layout);
        mTxtOfferDater.setOnClickListener(this);

        spnrSchemes = (AppCompatSpinner) view.findViewById(R.id.sprn_selectschemes);
        sprnMonth = (AppCompatSpinner) view.findViewById(R.id.sprn_selectmonth);
        sprnMonth.setOnItemSelectedListener(this);


        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        btnGetSummary = (Button) view.findViewById(R.id.btn_submit);
        btnGetSummary.setOnClickListener(this);

    }


    private void setBrandSpinner() {
        if (getActivity() != null) {
            brandArrayList = MySelectedSchemesHelper.getInstance().getAllSelectedSchemesList();
            brand = new String[brandArrayList.size()+1];
            brand[0] = new String("- Select Brand -");
            for(int i =1;i<brandArrayList.size()+1;i++){
                brand[i] = brandArrayList.get(i-1).get(0).getCategory();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(),
                    android.R.layout.simple_spinner_item, brand);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnrBrands.setOnItemSelectedListener(this);
            spnrBrands.setAdapter(adapter);
        }

    }

    private void showDatePickerOnUi() {
        mDateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity.dismissProgress();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.sprn_selectbrand:
                if (i != 0) {
                    showDatePickerOnUi();
                    // TODO Schemes
                    loadSchemesSpinner(i - 1);
                } else {
                    reset();
                }
                break;
            case R.id.sprn_selectschemes: {
                mTxtCalculator.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.VISIBLE);

            }
            break;
            case R.id.sprn_selectmonth:
                if (i != 0) {
                    setSchemesOnUi();
                }
                break;

        }

    }

    private void loadSchemesSpinner(int i) {
        schemesArrayList =brandArrayList.get(i);
        schemes = new String[schemesArrayList.size()+1];
        schemes[0] = new String("- Select Scheme -");
        int j =1;
        for (Scheme schemeItem : schemesArrayList) {
            schemes[j] = schemeItem.getName();
            j++;
        }
        setSchemeSpinner();
    }

    private void setSchemeSpinner() {
        ArrayAdapter<String> schemeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, schemes);
        schemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSchemes.setOnItemSelectedListener(this);
        spnrSchemes.setAdapter(schemeAdapter);
    }

    private String getBrandId(String selectedItemName) {
      /*  for (Brands brand : brandArrayList) {
            if (brand.getBrandTitle().equalsIgnoreCase(selectedItemName)) {
                return brand.getBrandId();
            }
        }*/
        return null;
    }

    private void reset() {
        spnrSchemes.setVisibility(View.GONE);
        mDateLayout.setVisibility(View.GONE);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void setMonthYear(int month, int year) {
        sprnMonth.setSelection(month + 1);
        mTxtOfferDater.setText(year + "");
        setSchemesOnUi();

    }

    private void setSchemesOnUi() {
        spnrSchemes.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setMonthYear(month,year);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                gotoAddSchemeItemFrag();
                break;
            case R.id.txt_date:
                showDatePickerDialog();
                break;
            case R.id.btn_submit:
                gotoPdfScreen();
                break;
        }
    }
    public void showDatePickerDialog() {

        DialogFragment newFragment = new DatePickerFragment();
        ((DatePickerFragment) newFragment).setCallBack((DatePickerDialog.OnDateSetListener) this);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    private void gotoPdfScreen() {
   /*     PdfFragment pdfFragment = PdfFragment.newInstance();
        mActivity.addFragment(pdfFragment, true);
*/
    }

    private void gotoAddSchemeItemFrag() {
        String selectedBrandTitle = (String) spnrBrands.getSelectedItem();
        AddSchemeFrag frag = AddSchemeFrag.
                newInstance(getBrandId(selectedBrandTitle), selectedBrandTitle);
        mActivity.addFragment(frag, true);
    }


    public void notifyData() {
        /*if (adapter != null) {
            adapter.updateList(getModelList());
            adapter.notifyDataSetChanged();
            if (DeviceHelper.getInstance().getDeviceList() != null
                    && DeviceHelper.getInstance().getDeviceList().size() > 0) {
                // Show Create Summary Button
                btnGetSummary.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mActivity != null) {
            mActivity.dismissProgress();
        }
    }
}

