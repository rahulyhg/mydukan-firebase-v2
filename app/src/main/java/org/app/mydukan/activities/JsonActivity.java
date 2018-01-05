package org.app.mydukan.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ServiceCenterInfo;
import org.app.mydukan.emailSending.SendEmail;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class JsonActivity extends AppCompatActivity {

    private MyDukan mApp;
    ServiceCenterInfo serviceCenterInfo = null;
    ArrayList<ServiceCenterInfo> serviceCenterInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
/*
                CompositionJso compositionJso = new CompositionJso();
                JSONObject obj;
                obj = compositionJso.makeJSONObject(compoTitle, compoDesc, imgPaths, imageViewPaths);

                try {
                    Writer output;  // getApplicationContext().getPackageName() + "/raw/sound_two"
                    File file = new File(String.valueOf(getAssets().open("eventLists_one.json")));
                    output = new BufferedWriter(new FileWriter(file));
                    output.write(obj.toString());
                    output.close();
                    Toast.makeText(getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                finish();
            }
            */
                }
            });

            // Reading json file from assets folder
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(getAssets().open("eventLists_one.json")));
                String temp;
                while ((temp = br.readLine()) != null)
                    sb.append(temp);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if ((br != null)) {
                    try {
                        br.close(); // stop reading
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                JSONObject jsonObjMain = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObjMain.getJSONArray("events");


                serviceCenterInfos = new ArrayList<ServiceCenterInfo>();
                ArrayList<String> mServiceCenterInfo = new ArrayList<String>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    // Creating JSONObject from JSONArray
                    serviceCenterInfo = new ServiceCenterInfo();
                    JSONObject object = jsonArray.getJSONObject(i);
                    serviceCenterInfo.setServicecenter_BRAND(object.getString("Brand"));
                    serviceCenterInfo.setServicecenter_CITY(object.getString("City"));
                    serviceCenterInfo.setServicecenter_NAME(object.getString("CompanyName"));
                    serviceCenterInfo.setServicecenter_ADDRESS(object.getString("CompanyAddress"));
                    serviceCenterInfo.setStateservicecenter_STATE(object.getString("State"));
                    // serviceCenterInfo.setServicecenter_PINCODE(Long.parseLong(object.getString("PinCode")));
                    serviceCenterInfo.setServicecenter_PHONENUMBER(object.getString("PhoneNumber"));
                    serviceCenterInfos.add(serviceCenterInfo);
                    // String message = object.getString("CompanyName");
                    // messages.add(message);
                    //      mServiceCenterInfo.add(serviceCenterInfo.getServicecenter_NAME() + "\n" + serviceCenterInfo.getBrand() + "\n" + serviceCenterInfo.getCity());
                    if (serviceCenterInfos != null) {
                        // updateServiceCenterInfo(this, serviceCenterInfos);
                    } else {
                        Toast.makeText(JsonActivity.this, "list is not added to database", Toast.LENGTH_SHORT).show();
                    }
                }
                //  updateServiceCenterInfo(this, serviceCenterInfos);


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(JsonActivity.this, android.R.layout.simple_list_item_1, mServiceCenterInfo);
                ListView list = (ListView) findViewById(R.id.eventList);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Toast.makeText(JsonActivity.this, "TEST List View", Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
        }
    }

    //=======================================================
    /*
    private void updateServiceCenterInfo(final Context mContext, final ArrayList<ServiceCenterInfo> mServiceCenterInfos) {

        //Initialize AppDukan   appsubcription
        ApiManager.getInstance(mContext).updateServiceCenterList(mApp.getFirebaseAuth().getCurrentUser().getUid(),
                mServiceCenterInfos, new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        Log.i(MyDukan.LOGTAG, "User updated successfully");
                        Toast.makeText(JsonActivity.this, "list is added to database", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(String response) {
                        Log.i(MyDukan.LOGTAG, "Failed to update user profile");

                    }

                    //==================================================================

                });

    }
    */


}