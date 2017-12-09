package org.app.mydukan.fragments.myschemes.calculator;/*
package org.app.mydukan.fragments.myschemes.calculator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import calc.mydukan.com.calculatortool.Helper.DeviceHelper;
import calc.mydukan.com.calculatortool.MainActivity;
import calc.mydukan.com.calculatortool.R;
import calc.mydukan.com.calculatortool.Utils.FireBaseUtils;
import calc.mydukan.com.calculatortool.Utils.NetworkUtils;
import calc.mydukan.com.calculatortool.Utils.Utils;
import calc.mydukan.com.calculatortool.adapters.AddedModelsAdapter;
import calc.mydukan.com.calculatortool.adapters.GridSpacingItemDecoration;
import calc.mydukan.com.calculatortool.fragments.PdfFragment;
import calc.mydukan.com.calculatortool.models.Brands;
import calc.mydukan.com.calculatortool.models.Device;
import calc.mydukan.com.calculatortool.models.MySelectedModel;
import calc.mydukan.com.calculatortool.models.Schemes;


public class CalculatorForm extends Fragment
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private MainActivity mActivity;
    AppCompatSpinner spnrBrands, spnrSchemes, sprnMonth;
    private DatabaseReference databaseReference;
    private ValueEventListener brandsListener;

    String[] brand = {"- Select Brand -"};
    List<Brands> brandArrayList;
    List<Schemes> schemesArrayList;

    String[] schemes = {"- Select Scheme -"};

    private TextView mTxtOfferDater, mTxtCalculator;

    private Button btnAdd, btnGetSummary;
    private RelativeLayout mDateLayout;
    // DevicesAdapter adapter;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.showProgressBar();
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.setRetainInstance(true);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculator_form, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCalculatorUI();
        setCalculatorData();
        setFirebase();
    }

    private void setFirebase() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://otsystem-64e61.firebaseio.com/MySchemes/" + FireBaseUtils.getUid());
//addTempData();
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            getMobileBrands();
        } else {
            mActivity.hideProgressBar();
            Toast.makeText(getActivity(), "Connect to internet", Toast.LENGTH_SHORT).show();
        }
        brandArrayList = new ArrayList<>();

    }

    private void getMobileBrands() {
        brandsListener= databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mActivity != null && dataSnapshot.getValue() != null) {
                    int i = 0;
                    MySelectedModel mySelectedModel = dataSnapshot.getValue(MySelectedModel.class);
                    brandArrayList = mySelectedModel.getMySelectedSchemes();
                    brand = new String[brandArrayList.size() + 1];
                    brand[0] = "- Select Brand -";
                    i++;

                    for (Brands brandItem : brandArrayList) {
                        brand[i] = brandItem.getBrandTitle();
                        i++;
                    }
                    setBrandSpinner();
                    mActivity.hideProgressBar();
                }else{
                    Toast.makeText(mActivity, "Please add schemes", Toast.LENGTH_SHORT).show();
                    mActivity.clearStack();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (mActivity != null) {
                    mActivity.hideProgressBar();
                }
            }
        });
    }

    private void addTempSchemes(Brands brandItem) {
        DatabaseReference dbSchemeRef = FirebaseDatabase.getInstance().
                getReference("schemes").child(brandItem.getBrandId());
        String id = dbSchemeRef.push().getKey();
        Schemes scheme = new Schemes(id, brandItem.getBrandTitle() + " Scheme 1");
        dbSchemeRef.child(id).setValue(scheme);

        id = dbSchemeRef.push().getKey();
        scheme = new Schemes(id, brandItem.getBrandTitle() + " Scheme 2");
        dbSchemeRef.child(id).setValue(scheme);


        id = dbSchemeRef.push().getKey();
        scheme = new Schemes(id, brandItem.getBrandTitle() + " Scheme 3");
        dbSchemeRef.child(id).setValue(scheme);


        id = dbSchemeRef.push().getKey();
        scheme = new Schemes(id, brandItem.getBrandTitle() + " Scheme 4");
        dbSchemeRef.child(id).setValue(scheme);


        id = dbSchemeRef.push().getKey();
        scheme = new Schemes(id, brandItem.getBrandTitle() + " Scheme 5");
        dbSchemeRef.child(id).setValue(scheme);


        id = dbSchemeRef.push().getKey();
        scheme = new Schemes(id, brandItem.getBrandTitle() + " Scheme 6");
        dbSchemeRef.child(id).setValue(scheme);


    }

    private void setTempData() {
        String id = databaseReference.push().getKey();
        Brands brand = new Brands(id, "Sony");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "Samsung");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "Motoralo");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "Vivo");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "MI");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "Nokia");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "MicroMax");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "LG");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "Oppo");
        databaseReference.child(id).setValue(brand);

        id = databaseReference.push().getKey();
        brand = new Brands(id, "Lava");
        databaseReference.child(id).setValue(brand);


    }

    private void setCalculatorData() {
        adapter = new AddedModelsAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = getView().findViewById(R.id.lst_schemes);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(
                new GridSpacingItemDecoration(2, Utils.dpToPx(getActivity(), 10), true));
        adapter.updateList(getModelList());
        recyclerView.setAdapter(adapter);


    }

    private List<String> getModelList() {
        int size = DeviceHelper.getInstance().getListSize();
        List<Device> arylst = DeviceHelper.getInstance().getDeviceList();
        List<String> modelsArry = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            modelsArry.add(arylst.get(i).getModel());
        }

        return modelsArry;
    }

    private void setCalculatorUI() {
        View view = getView();
        spnrBrands = view.findViewById(R.id.sprn_selectbrand);
        setBrandSpinner();
        mTxtCalculator = view.findViewById(R.id.lbl_calculator);
        mTxtOfferDater = view.findViewById(R.id.txt_date);
        mDateLayout = view.findViewById(R.id.date_layout);
        mTxtOfferDater.setOnClickListener(this);

        spnrSchemes = view.findViewById(R.id.sprn_selectschemes);
        sprnMonth = view.findViewById(R.id.sprn_selectmonth);
        sprnMonth.setOnItemSelectedListener(this);


        btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);

        btnGetSummary = view.findViewById(R.id.btn_submit);
        btnGetSummary.setOnClickListener(this);

    }

    private void setBrandSpinner() {
        if (getActivity() != null && brand != null) {
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
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mActivity = null;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.sprn_selectbrand:
                if (i != 0) {
                    showDatePickerOnUi();
                    // TODO Schemes
                    loadSchemes(i-1);
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

    private void loadSchemes(int pos) {
        Brands selectedBrand = brandArrayList.get(pos);
        schemesArrayList = new ArrayList<>();
        schemesArrayList = selectedBrand.getMySelectedSchemesList();
        schemes = new String[schemesArrayList.size() + 1];
        int i = 1;
        schemes[0] = "- Select Schemes -";
        for (Schemes schemeItem : schemesArrayList) {
            schemes[i] = schemeItem.getSchemeName();
            i++;
            setSchemeSpinner();
        }

*/
/*

        DatabaseReference schemesRef = FirebaseDatabase.getInstance().getReference("schemes").child(brandId);

        schemesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mActivity != null & isAdded()) {
                    schemes = new String[Integer.parseInt(dataSnapshot.getChildrenCount() + "") + 1];
                    int i = 1;
                    schemes[0] = "- Select Schemes -";
                    for (DataSnapshot schemeSS : dataSnapshot.getChildren()) {
                        Schemes schemesItem = schemeSS.getValue(Schemes.class);
                        schemesArrayList.add(schemesItem);
                        schemes[i] = schemesItem.getSchemeName();
                        i++;
                        setSchemeSpinner();
                    }

                    mActivity.hideProgressBar();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (mActivity != null) {
                    mActivity.hideProgressBar();
                }
            }
        });

*//*

    }

    private void setSchemeSpinner() {
        ArrayAdapter<String> schemeAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, schemes);
        schemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrSchemes.setOnItemSelectedListener(this);
        spnrSchemes.setAdapter(schemeAdapter);
    }

    private String getBrandId(String selectedItemName) {
        for (Brands brand : brandArrayList) {
            if (brand.getBrandTitle().equalsIgnoreCase(selectedItemName)) {
                return brand.getBrandId();
            }
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                gotoAddSchemeItemFrag();
                break;
            case R.id.txt_date:
                mActivity.showDatePickerDialog();
                break;
            case R.id.btn_submit:
                gotoPdfScreen();
                break;
        }
    }

    private void gotoPdfScreen() {
        PdfFragment pdfFragment = PdfFragment.newInstance();
        mActivity.addFragment(pdfFragment, true);

    }

    private void gotoAddSchemeItemFrag() {
        String selectedBrandTitle = (String) spnrBrands.getSelectedItem();
        AddSchemeFrag frag = AddSchemeFrag.
                newInstance(getBrandId(selectedBrandTitle), selectedBrandTitle);
        mActivity.addFragment(frag, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("key", 10);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            int a = savedInstanceState.getInt("key");
        }
    }

    public void notifyData() {
        if (adapter != null) {
            adapter.updateList(getModelList());
            adapter.notifyDataSetChanged();
            if (DeviceHelper.getInstance().getDeviceList() != null
                    && DeviceHelper.getInstance().getDeviceList().size() > 0) {
                // Show Create Summary Button
                btnGetSummary.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mActivity != null) {
            databaseReference.removeEventListener(brandsListener);
            mActivity.hideProgressBar();
        }
    }
}
*/
