package org.app.mydukan.data;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

/**
 * Created by Harshit Agarwal on 05-11-2017.
 */
@RealmClass
public class ContactUsers  implements RealmModel {
    private String name;
    private String email;
    @PrimaryKey @Required
    private String uId;
    private String photoUrl;
    private String phoneNumber;
    private String userType;
    private String contactName;

    public ContactUsers() {
    }

    public ContactUsers(String name, String email, String uId, String photoUrl, String phoneNumber, String userType) {
        this.name = name;
        this.email = email;
        this.uId = uId;
        this.photoUrl = photoUrl;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getuId() {
        return uId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserType() {
        return userType;
    }
}
