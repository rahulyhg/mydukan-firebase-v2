package org.app.mydukan.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Harshit Agarwal on 12-11-2017.
 */
public class Contact extends RealmObject {
    @Required
    @PrimaryKey
    String number;
    String name;

    public Contact() {
    }

    public Contact(String number, String name) {
        this.number = number;
        this.name = name;
    }
}
