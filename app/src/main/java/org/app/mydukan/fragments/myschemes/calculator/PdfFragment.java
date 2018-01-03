package org.app.mydukan.fragments.myschemes.calculator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.wooplr.spotlight.utils.Utils;

import org.app.mydukan.R;
import org.app.mydukan.fragments.myschemes.adapter.DevicesAdapter;
import org.app.mydukan.fragments.myschemes.adapter.GridSpacingItemDecoration;
import org.app.mydukan.fragments.myschemes.model.Device;
import org.app.mydukan.utils.SharedPrefsUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;


/**
 * Created by rojesharunkumar on 09/11/17.
 */

public class PdfFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = PdfFragment.class.getSimpleName();
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    private File pdfFile;
    private Button btnShare, btnEmail;
    private DevicesAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static PdfFragment newInstance() {

        Bundle args = new Bundle();

        PdfFragment fragment = new PdfFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pdf_viewer, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnShare = (Button) view.findViewById(R.id.btn_share);
        btnShare.setOnClickListener(this);

        btnEmail = (Button) view.findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(this);
        adapter = new DevicesAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) getView().findViewById(R.id.lst_schemes);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(
                new GridSpacingItemDecoration(2, Utils.dpToPx(10), true));

        recyclerView.setAdapter(adapter);

        try {
            checkPermission();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }


    private void checkPermission() throws FileNotFoundException, DocumentException {
        int hasStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("You need to allow access storage", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        }
                    });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else {
            createPdf();
        }
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(), "Schemes_summary.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();

        List<Device> mDevicesList = DeviceHelper.getInstance().getDeviceList();
        for (int i = 0; i < mDevicesList.size(); i++) {
            document.add(new Paragraph(mDevicesList.get(i).getModel()
                    + " " + mDevicesList.get(i).getTarget()
                    + " " + mDevicesList.get(i).getQuantity()
                    + " " + mDevicesList.get(i).getDp()
                    + " " + mDevicesList.get(i).getValue()
                    + " " + mDevicesList.get(i).getIncentiveAmount()
                    + " " + mDevicesList.get(i).getPercent()
            ));
        }
        document.close();
        previewPdf();


    }

    private void previewPdf() {
        PackageManager packageManager = getActivity().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        } else {
 //           Toast.makeText(getActivity(), "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        checkPermission();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_email:
                emailMyPdf();
                break;
            case R.id.btn_share:
                shareMyPdf();
                break;
        }


    }

    private void emailMyPdf() {
        String email = SharedPrefsUtils.getStringPreference(getActivity(),"logged_in_user_email");
        File reportFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/" + "Schemes_summary.pdf");
        Intent emailIntent  = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"Summary");
        emailIntent.putExtra(Intent.EXTRA_TEXT,"PFA");
        emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(reportFile));
        startActivity(emailIntent);
    }

    private void shareMyPdf() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/" + "Schemes_summary.pdf");
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(file), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent chooser = Intent.createChooser(target, "Open File");
        try {
            // Verify the intent will resolve to at least one activity
            if (chooser.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(chooser);
            }
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }

    }
}

/* // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page
        View content = view;
        content.draw(page.getCanvas());



        // finish the page
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/test.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(getActivity(), "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Something wrong: " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();*/