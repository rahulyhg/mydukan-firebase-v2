package org.app.mydukan.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.moe.pushlibrary.MoEHelper;

import org.app.mydukan.R;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.utils.ProgressSpinner;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by arpithadudi on 7/27/16.
 */
public class BaseActivity extends AppCompatActivity {

    private ProgressSpinner mProgress;
    private MoEHelper mHelper;
    public void showProgress() {
        try{
            if (!isFinishing()) {
                if (mProgress != null && mProgress.isShowing()) {
                    mProgress.dismiss();
                }
                mProgress = ProgressSpinner.show(this, "", "");
                mProgress.setCancelable(true);
                mProgress.setCanceledOnTouchOutside(false);
            }
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() +" - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            Crashlytics.log(0,this.getClass().getSimpleName() +" - onCreate : ",ex.toString());
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
        }
    }

    public void dismissProgress() {
        try {
            if (!isFinishing() && mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = null;
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - dismissProgress : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() +" - dismissProgress : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - dismissProgress : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() +" - dismissProgress : ",ex.toString());
        }
    }

    public void showErrorToast(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public void showNetworkConnectionError(Context context) {
        showErrorToast(context, getString(R.string.error_network));
    }

    public static void showOkAlert(Context context, String title, String message, String btnText){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, btnText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = MoEHelper.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.onStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHelper.onResume(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try{
            super.onSaveInstanceState(outState);
            mHelper.onSaveInstanceState(outState);
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onSaveInstanceState : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() +" - onSaveInstanceState : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onSaveInstanceState : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() +" - onSaveInstanceState : ",ex.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try{
            super.onRestoreInstanceState(savedInstanceState);
            mHelper.onRestoreInstanceState(savedInstanceState);
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onRestoreInstanceState : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() +" - onRestoreInstanceState : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onRestoreInstanceState : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() +" - onRestoreInstanceState : ",ex.toString());
        }
    }
}

