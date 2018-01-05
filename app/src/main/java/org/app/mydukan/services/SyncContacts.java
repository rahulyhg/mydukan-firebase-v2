package org.app.mydukan.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.app.mydukan.data.Contact;
import org.app.mydukan.data.ContactUsers;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;


public class SyncContacts extends IntentService {


    private static final String TAG = "SYNC_CONTACTS";
    private static final String CONTACT_COLLECTION = "contacts";
    private static final String RECEIVER = "mreceiver";
    Map<String, String> contactMap;
    List<ContactUsers> users;
    int count = 0;
    int finished = 0;
    @Nullable
    private static ResultReceiver mReceiver;
    private static final int CONTACT_BATCH = 100;
    private static final String MY_PREFS_NAME = "ContactsSharedPref";

    public SyncContacts() {
        super("SyncContacts");
    }

    private static boolean isRunning;

    public static void searchContacts(String phoneNumber, @Nullable final UserRetrieved userRetrieved, final Map<String, String> contactMap) {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference doc = db.collection(CONTACT_COLLECTION).document(phoneNumber);
            doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                            List<Map.Entry<String, Object>> list = new ArrayList<>(document.getData().entrySet());
                            getUser(list.get(0).getKey(), userRetrieved, contactMap);
                        } else {
                            Log.d(TAG, "No such document");
                            if (userRetrieved != null)
                                userRetrieved.onUserNotExists();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        if (userRetrieved != null)
                            userRetrieved.onError(task.getException());
                    }
                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + "SyncContacts" + " - searchContacts : ",e.toString());
            Crashlytics.log(0,"Exception - " + "SyncContacts" + " - searchContacts : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail("SyncContacts" + " - searchContacts : ",ex.toString());
            Crashlytics.log(0,"SyncContacts" + " - searchContacts : ",ex.toString());
        }
    }

    private static void getUser(final String userId, @Nullable final UserRetrieved userRetrieved, final Map<String, String> contactMap) {
        try {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(AppContants.CHAT_USER + "/" + userId);
            //addListenerForSingleValueEvent
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get user value
                    if (userRetrieved != null) {
                        if (dataSnapshot != null && dataSnapshot.exists()) {
                            ContactUsers user = dataSnapshot.getValue(ContactUsers.class);
                            user.setContactName(contactMap.get(user.getPhoneNumber()) == null ? "" : contactMap.get(user.getPhoneNumber()));
                            userRetrieved.onUserRetrieved(user);
                        } else {
                            userRetrieved.onUserNotExists();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (userRetrieved != null)
                        userRetrieved.onError(databaseError.toException());
                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + "SyncContacts" + " - getUser : ",e.toString());
            Crashlytics.log(0,"Exception - " + "SyncContacts" + " - getUser : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail("SyncContacts" + " - getUser : ",ex.toString());
            Crashlytics.log(0,"SyncContacts" + " - getUser : ",ex.toString());
        }
    }

    public static Map<String, String> getNumberMap(Context context) {
        Map<String, String> phoneMap = new HashMap<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            while (phones.moveToNext()) {

                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = Utils.formatNumber(phoneNumber);
                phoneMap.put(phoneNumber, name);
//                Log.d(TAG, name + " " + phoneNumber);
            }
            phones.close();
        }
        return phoneMap;
    }

    public static void startService(Context context, @Nullable ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, SyncContacts.class);
        intent.putExtra(RECEIVER, resultReceiver);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (!isRunning)
            return super.onStartCommand(intent, flags, startId);
        else {
            if (intent != null) {
                mReceiver = intent.getParcelableExtra(RECEIVER);
            }
            return START_NOT_STICKY;
        }

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        isRunning = true;
        mReceiver = intent.getParcelableExtra(RECEIVER);
        contactMap = new HashMap<>();
        users = new ArrayList<>();
        contactMap = getNumberMap(this);

        if (getLastSyncedUser().isEmpty()) {  //called for first time only
            addContactsToDB(contactMap);
            searchContacts(new ArrayList<>(contactMap.entrySet()));
        } else {      //subsequent calls
            syncUsersWithPhoneBook();    //removes users from My Network who have been deleted from phone book
            syncNewContactsAddedInDB(getLastSyncedUser());  //syncs new contacts from db

        }
    }

    private void addContactsToDB(Map<String, String> contactMap) {
        List<Contact> contacts = new ArrayList<>();
        for (Map.Entry<String, String> entry : contactMap.entrySet()) {
            contacts.add(new Contact(entry.getKey(), entry.getValue()));
        }

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        realm.beginTransaction();
        realm.where(Contact.class).findAll().deleteAllFromRealm();
        realm.copyToRealmOrUpdate(contacts);
        realm.commitTransaction();
        realm.close();
    }

    private void syncNewPhoneBookContacts() {
        ArrayList<Map.Entry<String, String>> newContacts = new ArrayList<>();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        Map<String, String> newContactMap = new HashMap<>();
        for (Map.Entry<String, String> entry : contactMap.entrySet()) {
            if (realm.where(Contact.class).equalTo("number", entry.getKey()).findFirst() == null) {  // new / edited contact
                newContacts.add(entry);
                newContactMap.put(entry.getKey(), entry.getValue());
            }
        }
        addContactsToDB(contactMap);
        contactMap = newContactMap;
        searchContacts(newContacts);

    }

    private void syncNewContactsAddedInDB(String lastSyncedUser) {
        try {
            DatabaseReference chatUsers = FirebaseDatabase.getInstance().getReference().child(AppContants.CHAT_USER);
            chatUsers.orderByKey().startAt(lastSyncedUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        ContactUsers lastChild = null;
                        for (DataSnapshot userSnap : dataSnapshot.getChildren()) {
                            ContactUsers user = userSnap.getValue(ContactUsers.class);
                            lastChild = user;
                            if (user.getPhoneNumber() == null)
                                continue;
                            user.setPhoneNumber(Utils.formatNumber(user.getPhoneNumber()));
                            if (contactMap.containsKey(user.getPhoneNumber())) {
                                user.setContactName(contactMap.get(user.getPhoneNumber()));
                                users.add(user);
                            }
                        }
                        if (lastChild != null) {
                            setLastSyncedUser(lastChild.getuId());
                        }
                    }
                    syncNewPhoneBookContacts();     //syncs new/edited contacts from phone book
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    syncNewPhoneBookContacts();     //syncs new/edited contacts from phone book
                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - syncNewContactsAddedInDB : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - syncNewContactsAddedInDB : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - syncNewContactsAddedInDB : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - syncNewContactsAddedInDB : ",ex.toString());
        }
    }

    private void syncUsersWithPhoneBook() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        realm.beginTransaction();

        List<ContactUsers> user2 = realm.copyFromRealm(realm.where(ContactUsers.class).findAll());
        for (ContactUsers user : user2) {
            if (!contactMap.containsKey(user.getPhoneNumber())) {
                RealmResults<ContactUsers> toDelete = realm.where(ContactUsers.class).equalTo("uId", user.getuId()).findAll();
                toDelete.deleteAllFromRealm();
            } else {
                RealmResults<ContactUsers> toEdit = realm.where(ContactUsers.class).equalTo("uId", user.getuId()).findAll();
                if (!toEdit.isEmpty())
                    toEdit.get(0).setContactName(contactMap.get(user.getPhoneNumber()));
            }
        }
        realm.commitTransaction();
        realm.close();
    }

    private String getLastSyncedUser() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return prefs.getString("lastUser", "");
    }

    private void setLastSyncedUser(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("lastUser", userId);
        editor.commit();
    }


    private void searchContacts(final ArrayList<Map.Entry<String, String>> entries) {

        if (entries.size() == 0) {
            checkFinished();
        }

        int i = 0;
        for (final Map.Entry<String, String> entry : entries) {
            count++;
            searchContacts(entry.getKey(), new UserRetrieved() {
                @Override
                public void onUserRetrieved(ContactUsers user) {
                    users.add(user);
                    contactMap.remove(entry.getKey());
                    checkFinished();
                }

                @Override
                public void onUserNotExists() {
                    contactMap.remove(entry.getKey());
                    checkFinished();
                }

                @Override
                public void onError(Exception e) {
                    contactMap.remove(entry.getKey());
                    checkFinished();
                }
            }, contactMap);
            i++;
            if (i == CONTACT_BATCH)
                break;
        }
    }


    private void storeData() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm realm = Realm.getInstance(config);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(users);
        realm.commitTransaction();

        /*List<ContactUsers> user2 = realm.where(ContactUsers.class).findAll();
        for (ContactUsers user : user2) {
            Log.d(TAG, user.getName());
        }
*/
        realm.close();
    }

    private void checkFinished() {
        finished++;
        if (count <= finished && contactMap.size() == 0) {
            storeData();
            if (getLastSyncedUser().isEmpty())
                getLastUserInDB();
            else
                sendFinishedResult();

        } else if (count <= finished) {
            searchContacts(new ArrayList<>(contactMap.entrySet()));
        }
    }

    private void getLastUserInDB() {
        try {
            DatabaseReference chatUsers = FirebaseDatabase.getInstance().getReference().child(AppContants.CHAT_USER);
            chatUsers.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        ContactUsers lastChild = null;
                        for (DataSnapshot userSnap : dataSnapshot.getChildren()) {
                            lastChild = userSnap.getValue(ContactUsers.class);
                        }
                        if (lastChild != null) {
                            setLastSyncedUser(lastChild.getuId());
                        }
                    }
                    sendFinishedResult();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    sendFinishedResult();
                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - getLastUserInDB : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - getLastUserInDB : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - getLastUserInDB : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - getLastUserInDB : ",ex.toString());
        }
    }

    private void sendFinishedResult() {
        if (mReceiver != null)
            mReceiver.send(RESULT_OK, new Bundle());
        isRunning = false;
    }

    public interface UserRetrieved {
        void onUserRetrieved(ContactUsers users);

        void onUserNotExists();

        void onError(Exception e);
    }
}
