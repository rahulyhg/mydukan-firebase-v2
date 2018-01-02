package org.app.mydukan.activities;

import android.app.SearchManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.adapters.SearchNetworkAdapter;
import org.app.mydukan.data.ContactUsers;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.services.SyncContacts;
import org.app.mydukan.services.VolleyNetworkRequest;
import org.app.mydukan.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Search_MyNetworkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchNetworkAdapter mAdapter;

    public static final String LIKE_ROOT = "like";
    public static final String FOLLOWING_ROOT = "following";
    public static final String FOLLOWERS_ROOT = "followers";

    List<Contact> nonNetwork, allContactList;
    List<ContactUsers> networkContacts;
    Map<String, String> contactMap;
    Realm realm;
    View progressBar;
    TextView emptyText;
    VolleyNetworkRequest jsonRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_mynetwork);
        try {
            if(getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            jsonRequest = new VolleyNetworkRequest(this);

            emptyText = (TextView) findViewById(R.id.emptyText);
            progressBar = findViewById(R.id.progressBar);
//        contactMap = SyncContacts.getNumberMap(this);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new SearchNetworkAdapter(this, jsonRequest);
            recyclerView.setAdapter(mAdapter);


            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();

            realm = Realm.getInstance(config);
            //loadData();

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    reSyncContacts();
                }
            });
            getFollowings();

            showProgress(true);
            new ContactLoader().execute();
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

    private void reSyncContacts() {
        SyncContacts.startService(Search_MyNetworkActivity.this, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                loadData();
                swipeRefreshLayout.setRefreshing(false);
//                Toast.makeText(Search_MyNetworkActivity.this, "Re-synced contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFollowings() {
        try {
            swipeRefreshLayout.setRefreshing(true);
            final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference referenceFollowing = FirebaseDatabase.getInstance().getReference().child(FOLLOWING_ROOT + "/" + auth.getUid());
            referenceFollowing.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mAdapter.setFollowing(dataSnapshot);
                    reSyncContacts();
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showProgress(false);
                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - getFollowings : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - getFollowings : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - getFollowings : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - getFollowings : ",ex.toString());
        }
    }

    private void loadData() {
        if (contactMap == null)
            return;
        allContactList = createContactList(new ArrayList<>(contactMap.entrySet()));
        Collections.sort(allContactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact o1, Contact o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        filterLists(currentQuery);
    }
    String currentQuery="";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterLists(final String s) {
        networkContacts = realm.copyFromRealm(realm.where(ContactUsers.class)
                .beginsWith("name", s, Case.INSENSITIVE)
                .or()
                .contains("phoneNumber", Utils.formatNumber(s))
                .or()
                .beginsWith("contactName", s, Case.INSENSITIVE)
                .findAllSorted("name"));

        nonNetwork = Lists.newArrayList(Collections2.filter(allContactList, new Predicate<Contact>() {
            @Override
            public boolean apply(@Nullable Contact input) {
                return input == null || input.getName().toLowerCase().startsWith(s.toLowerCase())
                        || input.getNumbers().toString().contains(Utils.formatNumber(s));
            }
        }));

        mAdapter.setNetworkUsers(networkContacts);
        mAdapter.setNonNetworkUsers(nonNetwork);
        emptyText.setText("Contact not in phone-book.\nTap search button to search in the Network.");
        checkEmpty();
    }

    private void checkEmpty() {
        emptyText.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private List<Contact> createContactList(ArrayList<Map.Entry<String, String>> entries) {
        List<Contact> contacts = new ArrayList<>();
        Collections.sort(entries, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        for (Map.Entry<String, String> entry : entries) {
            if (contacts.size() > 0 && contacts.get(contacts.size() - 1).getName().equals(entry.getValue())) {
                contacts.get(contacts.size() - 1).add(entry.getKey());
            } else {
                contacts.add(new Contact(entry.getValue(), entry.getKey()));
            }
        }
        List<Contact> contacts2 = new ArrayList<>();
        for (Contact contact : contacts) {
            boolean inNetwork = false;
            for (String number : contact.getNumbers()) {
                if (!realm.where(ContactUsers.class).contains("phoneNumber", Utils.formatNumber(number)).findAll().isEmpty()) {
                    inNetwork = true;
                }
            }
            if (!inNetwork) {
                contacts2.add(contact);
            }
        }
        return contacts2;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_my_network, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setQueryHint("Filter or tap search on keyboard");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = Utils.formatNumber(query);
                if (!query.matches("^[+]91[0-9]*$") || query.length() != 13) {
                    Toast.makeText(Search_MyNetworkActivity.this, "Provide a valid number", Toast.LENGTH_SHORT).show();
                }
                else{
                    searchUser(query);
                }
                currentQuery=query;
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery=newText;
                filterLists(newText);
                return true;
            }
        });
        return true;
    }

    private void showProgress(boolean b) {
        progressBar.setVisibility(b ? View.VISIBLE : View.GONE);

    }

    private void searchUser(String mobNum ) {
        showProgress(true);
        SyncContacts.searchContacts(mobNum, new SyncContacts.UserRetrieved() {
            @Override
            public void onUserRetrieved(ContactUsers users) {
                networkContacts=new ArrayList<>();
                networkContacts.add(users);
                mAdapter.setNetworkUsers(networkContacts);
                nonNetwork=new ArrayList<>();
                showProgress(false);
                checkEmpty();
            }

            @Override
            public void onUserNotExists() {
                showProgress(false);
                emptyText.setText("User Profile doesn't exist in Network.");
            }

            @Override
            public void onError(Exception e) {
                showProgress(false);
                emptyText.setText("Unknown error occurred.\nRetry search later");
            }
        },contactMap);
    }

    public static class Contact {
        String name;
        List<String> numbers;

        public Contact(String name, String number) {
            this();
            this.name = name;
            numbers.add(number);
        }

        public Contact() {
            numbers = new ArrayList<>();
        }

        public String getName() {

            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getNumbers() {
            return numbers;
        }

        public void setNumbers(List<String> numbers) {
            this.numbers = numbers;
        }

        public void add(String number) {
            numbers.add(number);
        }
    }

    class ContactLoader extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            return SyncContacts.getNumberMap(Search_MyNetworkActivity.this);
        }

        @Override
        protected void onPostExecute(Map<String, String> s) {
            contactMap = s;
            loadData();
            showProgress(false);
        }
    }
}
