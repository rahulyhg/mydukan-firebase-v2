package org.app.mydukan.emailSending;

/**
 * Created by amit on 14/12/17.
 */


import org.app.mydukan.utils.AppContants;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends Authenticator {
    public SMTPAuthenticator() {

        super();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String username = AppContants.GMAIL_EMAIL_ID;
        String password = AppContants.GMAIL_PASSWORD;
        if ((username != null) && (username.length() > 0) && (password != null)
                && (password.length() > 0)) {

            return new PasswordAuthentication(username, password);
        }

        return null;
    }
}
