package org.app.mydukan.emailSending;

import android.os.AsyncTask;

import org.app.mydukan.utils.AppContants;

/**
 * Created by amit on 14/12/17.
 */

public class SendEmail {


    public void sendEmail(String tag,String msg){

        SendMail sendMail = new SendMail();
        String[] params = new String[2];
        params[0] = tag;
        params[1] = msg;
        sendMail.execute(params);
    }

    private class SendMail extends AsyncTask<String, Integer, Void> {

        protected Void doInBackground(String... params) {
            try {
                Mail m = new Mail(AppContants.GMAIL_EMAIL_ID, AppContants.GMAIL_PASSWORD);

                String[] toArr = {AppContants.GMAIL_EMAIL_ID};
                m.setTo(toArr);
                m.setFrom(AppContants.GMAIL_EMAIL_ID);
                m.setSubject(params[0]);
                m.setBody(params[1]);


                if (m.send()) {
                    int a = 2 + 2;
                }else{
                    int a = 2 + 2;
                }
            }catch (Throwable ex){
                System.out.print(ex.toString());
            }
            return null;
        }
    }
}
