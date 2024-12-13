package my.vista.com.handheld.UI.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.PrinterUtils;
import my.vista.com.handheld.Business.PrintingDocument;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.UploadNoticeService;

/**
 * Created by hp on 24/7/2016.
 */
public class RingkasanFragment extends Fragment {
    private Runnable doPrint;
    private ProgressDialog mProgressDialog = null;
    View rootView;

    public static ProgressDialog m_ProgressDialog = null;

    private static final int TAKE_PICTURE_FINAL = 1;

    private Runnable doSaveImage;

    boolean hasEmptyIndex = false;
    boolean isUriPresent = false;

    public RingkasanFragment() {
    }

    public static RingkasanFragment newInstance() {
        RingkasanFragment fragment = new RingkasanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean fragmentResume=false;
    private boolean fragmentVisible=false;
    private boolean fragmentOnCreated=false;
    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (visible && isResumed()){   // only at fragment screen is resumed
            fragmentResume = true;
            fragmentVisible = false;
            fragmentOnCreated = true;

            try {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = getActivity().getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (Exception e) {

            }

            FillData();
        } else if (visible){        // only at fragment onCreated
            fragmentResume = false;
            fragmentVisible = true;
            fragmentOnCreated = true;
        } else if (!visible && fragmentOnCreated){// only when you go out of fragment screen
            fragmentVisible = false;
            fragmentResume = false;
            fragmentOnCreated = false;
        }
    }

    public void FillData() {
        TextView tNoKenderaan = (TextView)rootView.findViewById(R.id.tvSummaryNoKenderaan);
        tNoKenderaan.setText(CacheManager.SummonIssuanceInfo.VehicleNo);

        TextView tNoCukaiJalan = (TextView)rootView.findViewById(R.id.tvSummaryNoCukaiJalan);
        tNoCukaiJalan.setText(CacheManager.SummonIssuanceInfo.RoadTaxNo);

        TextView tJenama = (TextView)rootView.findViewById(R.id.tvSummaryJenama);
        if(CacheManager.SummonIssuanceInfo.VehicleMake.length() != 0)
        {
            tJenama.setText(CacheManager.SummonIssuanceInfo.VehicleMake);
        }
        else
        {
            tJenama.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleMake);
        }

        TextView tModel = (TextView)rootView.findViewById(R.id.tvSummaryModel);
        if(CacheManager.SummonIssuanceInfo.VehicleModel.length() != 0)
        {
            tModel.setText(CacheManager.SummonIssuanceInfo.VehicleModel);
        }
        else
        {
            tModel.setText(CacheManager.SummonIssuanceInfo.SelectedVehicleModel);
        }

        TextView tType = (TextView)rootView.findViewById(R.id.tvSummaryType);
        tType.setText(CacheManager.SummonIssuanceInfo.VehicleType);

        TextView tColor = (TextView)rootView.findViewById(R.id.tvSummaryColor);
        tColor.setText(CacheManager.SummonIssuanceInfo.VehicleColor);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        ImageView iImage1 = (ImageView)rootView.findViewById(R.id.ivSummaryImage1);
        ImageView iImage2 = (ImageView)rootView.findViewById(R.id.ivSummaryImage2);
        ImageView iImage3 = (ImageView)rootView.findViewById(R.id.ivSummaryImage3);
        ImageView iImage4 = (ImageView)rootView.findViewById(R.id.ivSummaryImage4);
        if(!CacheManager.SummonIssuanceInfo.ImageLocation.isEmpty()) {
            File file = new File(dir, CacheManager.SummonIssuanceInfo.ImageLocation.get(0));
            if (file.exists()) {
                BitmapFactory.Options options;
                Bitmap bitmap = null;
                try {
                    options = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                } catch (Exception ex) {
                }

                iImage1.setImageBitmap(bitmap);
                System.gc();
            }

            if(CacheManager.SummonIssuanceInfo.ImageLocation.size() > 1) {
                file = new File(dir, CacheManager.SummonIssuanceInfo.ImageLocation.get(1));
                if (file.exists()) {
                    BitmapFactory.Options options;
                    Bitmap bitmap = null;
                    try {
                        options = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    } catch (Exception ex) {
                    }

                    iImage2.setImageBitmap(bitmap);
                    System.gc();
                }

                if(CacheManager.SummonIssuanceInfo.ImageLocation.size() > 2) {
                    file = new File(dir, CacheManager.SummonIssuanceInfo.ImageLocation.get(2));
                    if (file.exists()) {
                        BitmapFactory.Options options;
                        Bitmap bitmap = null;
                        try {
                            options = new BitmapFactory.Options();
                            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                        } catch (Exception ex) {
                        }

                        iImage3.setImageBitmap(bitmap);
                        System.gc();
                    }

                    if(CacheManager.SummonIssuanceInfo.ImageLocation.size() > 3) {
                        file = new File(dir, CacheManager.SummonIssuanceInfo.ImageLocation.get(3));
                        if (file.exists()) {
                            BitmapFactory.Options options;
                            Bitmap bitmap = null;
                            try {
                                options = new BitmapFactory.Options();
                                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                            } catch (Exception ex) {
                            }

                            iImage4.setImageBitmap(bitmap);
                            System.gc();
                        }
                    }
                }
            }
        }

        TextView tOffenceAct = (TextView)rootView.findViewById(R.id.tvSummaryUndangUndang);
        tOffenceAct.setText(CacheManager.SummonIssuanceInfo.OffenceAct);

        TextView tOffenceSection = (TextView)rootView.findViewById(R.id.tvSummarySeksyenKaedah);
        tOffenceSection.setText(CacheManager.SummonIssuanceInfo.OffenceSection);

        TextView tZon = (TextView)rootView.findViewById(R.id.tvSummaryZon);
        tZon.setText(CacheManager.SummonIssuanceInfo.OffenceLocationArea);

        TextView tTempatjalan = (TextView)rootView.findViewById(R.id.tvSummaryTempatJalan);
        if(CacheManager.SummonIssuanceInfo.OffenceLocation.length() != 0)
        {
            tTempatjalan.setText(CacheManager.SummonIssuanceInfo.OffenceLocation);
        }
        else
        {
            tTempatjalan.setText(CacheManager.SummonIssuanceInfo.SummonLocation);
        }

        TextView tButiranLokasi = (TextView)rootView.findViewById(R.id.tvSummaryButiranLokasi);
        tButiranLokasi.setText(CacheManager.SummonIssuanceInfo.OffenceLocationDetails);

        TextView tKapit = (TextView)rootView.findViewById(R.id.tvSummaryKapit);
        if(CacheManager.SummonIssuanceInfo.IsClamping.equalsIgnoreCase("True")) {
            tKapit.setText("Ya");
        } else {
            tKapit.setText("Tidak");
        }
    }

    @Override
    public void onViewStateRestored (Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        FillData();
    }

    private void captureImage4(Context context, SummonIssuanceInfo info) {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String delegate = "yy";
        String year = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(dir, SettingsHelper.HandheldCode + year + SettingsHelper.getNoticeSerialNumber(CacheManager.mContext) + "Pic" + CacheManager.imageIndex + ".jpg");
        try {
            photo.createNewFile();
            photo.setReadable(true);
            photo.setWritable(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MESSAGE", e.getMessage());
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        CacheManager.ImageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE_FINAL);
    }

    private void storeCaptureImage4(Uri imageUri) {
        // Ensure imageUri is not null
        if (imageUri != null) {
            // Extract the image file name from the URI
            String path = imageUri.getPath(); // Get the full path
            if (path != null && path.contains("/")) {
                String image5path = path.substring(path.lastIndexOf("/") + 1); // Extract the file name

                CacheManager.SummonIssuanceInfo.ImageLocation.add(image5path);
                CacheManager.imageIndex++;
                // Safely set the value at index 4
//                CacheManager.finalImage = image5path;

                if(!image5path.isEmpty()){
                    UploadNoticeService.mRun.run();
                }

            } else {
                Log.e("storeCaptureImage4", "Invalid image URI path.");
            }
        } else {
            Log.e("storeCaptureImage4", "Image URI is null.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CacheManager.publicRequestCode == TAKE_PICTURE_FINAL) {
            if (CacheManager.publicResultCode == Activity.RESULT_OK) {

                storeCaptureImage4(CacheManager.ImageUri);
                DbLocal.InsertNotice(CacheManager.mContext, CacheManager.SummonIssuanceInfo);

                m_ProgressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
                m_ProgressDialog.setMessage("Saving Gambar Selepas..");
                m_ProgressDialog.setTitle("");
                m_ProgressDialog.setCancelable(false);
                m_ProgressDialog.setIndeterminate(true);
                m_ProgressDialog.show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ringkasan, container, false);

        System.gc();

        final TextView tTarikh = (TextView)rootView.findViewById(R.id.tvSummaryTarikh);
        final TextView tMasa = (TextView)rootView.findViewById(R.id.tvSummaryMasa);

        Button btnCetak = (Button)rootView.findViewById(R.id.btncetak);
        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheManager.SummonIssuanceInfo.OffenceDateTime = new Date();

                if(SettingsHelper.MACAddress.isEmpty()) {
                    Toast.makeText(getActivity(), "Sila Masukkan MAC Address Printer", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getActivity(), StatusActivity.class);
                    startActivity(i);
                } else {
                    if (ValidateData()) {
                        doPrint = new Runnable() {
                            @Override
                            public void run() {
                                Looper.prepare();
                                DoPrint();
                                Looper.loop();
                                Looper.myLooper().quit();
                            }
                        };

                        final Handler timeHandler = new Handler();
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                captureImage4(getActivity(), CacheManager.SummonIssuanceInfo);
                            }
                        };
                        timeHandler.postDelayed(run, 2000);

                        mProgressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
                        mProgressDialog.setMessage("Loading");
                        mProgressDialog.setTitle("");
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setIndeterminate(true);
                        mProgressDialog.show();

                        Thread thread = new Thread(null, doPrint, "PrintProcess");
                        thread.start();
                    }
                }
            }
        });

        final Handler timeHandler = new Handler();
        Runnable run = new Runnable() {

            @Override
            public void run() {
                tTarikh.setText(CacheManager.GetDate());
                tMasa.setText(CacheManager.GetTime().toUpperCase());
                timeHandler.postDelayed(this, 500);
            }
        };
        timeHandler.postDelayed(run, 500);

        FillData();

        return rootView;
    }

    public void AlertMessage(final Context context, String title,String message,int type)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
        builder.setTitle(title);
        builder.setMessage(message);
        if( type == 0)
        {
            builder.setPositiveButton("OK", null);
        }
        if(type == 1)
        {
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cancel", null);
        }
        if( type == 2)
        {
            builder.setCancelable(false);
            builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    doPrint = new Runnable() {
                        @Override
                        public void run()
                        {
                            Looper.prepare();
                            DoPrintCopy();
                            Looper.loop();
                            Looper.myLooper().quit();
                        }
                    };
                    mProgressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
                    mProgressDialog.setMessage("Loading");
                    mProgressDialog.setTitle("");
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.show();

                    Thread thread = new Thread(null, doPrint, "LoginProcess");
                    thread.start();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    CacheManager.IsNewNotice = true;
                    System.gc();
//                    ClearFileData();
                    Intent i = new Intent(getActivity(), NoticeIssuanceActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(i);
                    getActivity().finish();
                }
            });
        }
        if(type == 3)
        {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    AlertMessage(getActivity(), "CETAK", "Cetak Salinan Kedua?", 2);
                }
            });
        }
        builder.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        System.gc();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    boolean ValidateData()
    {
        if (CacheManager.SummonIssuanceInfo.VehicleNo.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "NO. KEND.", "Sila Isikan No. Kend.", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.VehicleType.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "JENIS BADAN", "Sila Pilih Jenis Badan Kenderaan", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.OffenceAct.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "UNDANG-UNDANG", "Sila Pilih Peruntukan Undang-Undang", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.OffenceSection.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "UNDANG-UNDANG", "Sila Pilih Seksyen/Kaedah", 3);
            return false;
        }
        if (!CacheManager.HasChecked && CacheManager.SummonIssuanceInfo.OffenceSectionCode.equalsIgnoreCase("3")) {
            CustomAlertDialog.Show(getActivity(), "NO. KEND.", "Sila Semak No. Kend.", 3);
            return false;
        }
        if (CacheManager.SummonIssuanceInfo.OffenceLocationArea.length() == 0 && CacheManager.SummonIssuanceInfo.SummonLocation.length() == 0)
        {
            CustomAlertDialog.Show(getActivity(), "NAMA JALAN", "Sila Pilih Nama Jalan", 3);
            return false;
        }
        if(CacheManager.SummonIssuanceInfo.ImageLocation.size() < 2)
        {
            CustomAlertDialog.Show(getActivity(), "GAMBAR", "Sila Tangkap 2 Gambar", 3);
            return false;
        }

        return true;
    }

    void CreateStorageDirectory()
    {
        // create a File object for the parent directory
        File summonsDirectory = new File("/mnt/sdcard/OfflineSummons/");
        // have the object build the directory structure, if needed.
        if(!summonsDirectory.exists())
        {
            summonsDirectory.mkdirs();
        }
    }

    private void GenerateXmlNotice(SummonIssuanceInfo summons){
        CreateStorageDirectory();
        File newxmlfile = new File("/mnt/sdcard/OfflineSummons/" + summons.NoticeSerialNo + ".xml");
        try{
            newxmlfile.createNewFile();
        }catch(IOException e)
        {
        }

        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(newxmlfile, false));

        }catch(Exception e)
        {
        }

        String delegateDate = "yyyy-MM-dd";
        String delegateTime = "HH:mm:ss";
        String date = "";
        String time = "";
        String makeModel = "";
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter data = new StringWriter();
        try {
            serializer.setOutput(data);
            serializer.startDocument(null, null);
            serializer.startTag(null, "ns0:Notice");
            serializer.startTag(null, "No");
            serializer.text(summons.NoticeSerialNo);
            serializer.endTag(null, "No");
            serializer.startTag(null, "HandheldID");
            serializer.text(SettingsHelper.HandheldCode);
            serializer.endTag(null,"HandheldID");
            serializer.startTag(null, "OfficerID");
            serializer.text(CacheManager.UserId);
            serializer.endTag(null,"OfficerID");

            //Vehicle
            serializer.startTag(null, "Vehicle");
            serializer.startTag(null, "No");
            serializer.text(summons.VehicleNo);
            serializer.endTag(null, "No");
            serializer.startTag(null, "Type");
            serializer.text(summons.VehicleType);
            serializer.endTag(null,"Type");
            serializer.startTag(null, "MakeModel");
            if(summons.VehicleMake.length() != 0)
                makeModel += summons.VehicleMake;
            else
                makeModel += summons.SelectedVehicleMake;

            if(summons.VehicleModel.length() != 0)
                makeModel += " " + summons.VehicleModel;
            else
                makeModel += " " + summons.SelectedVehicleModel;
            serializer.text(makeModel);
            serializer.endTag(null, "MakeModel");
            serializer.startTag(null, "RoadTaxNo");
            serializer.text(summons.RoadTaxNo);
            serializer.endTag(null, "RoadTaxNo");
            serializer.startTag(null, "Color");
            serializer.text(summons.VehicleColor);
            serializer.endTag(null, "Color");
            serializer.endTag(null, "Vehicle");

            //Offence
            serializer.startTag(null, "Offence");
            serializer.startTag(null, "Date");
            date = (String) DateFormat.format(delegateDate, summons.OffenceDateTime);
            time = (String) DateFormat.format(delegateTime,summons.OffenceDateTime);
            serializer.text(date + "T" + time);
            serializer.endTag(null, "Date");
            serializer.startTag(null, "OffenceActCode");
            serializer.text(summons.OffenceActCode);
            serializer.endTag(null, "OffenceActCode");
            serializer.startTag(null, "OffenceSectionCode");
            serializer.text(summons.OffenceSectionCode);
            serializer.endTag(null, "OffenceSectionCode");
            serializer.startTag(null, "IsClamping");
            serializer.text(summons.IsClamping);
            serializer.endTag(null, "IsClamping");
            serializer.startTag(null, "LocationArea");
            serializer.text(summons.OffenceLocationArea);
            serializer.endTag(null, "LocationArea");
            serializer.startTag(null, "Location");
            if(summons.OffenceLocation.length() != 0)
            {
                serializer.text(summons.OffenceLocation);
            }
            else
            {
                serializer.text(summons.SummonLocation);
            }
            serializer.endTag(null, "Location");
            serializer.startTag(null, "LocationDetails");
            serializer.text(summons.OffenceLocationDetails);
            serializer.endTag(null, "LocationDetails");
            serializer.startTag(null, "Notes");
            serializer.text(summons.Notes);
            serializer.endTag(null, "Notes");
            serializer.startTag(null, "OfficerUnit");
            serializer.text(summons.OfficerUnit);
            serializer.endTag(null, "OfficerUnit");

            String image1 = "";
            String image2 = "";
            String image3 = "";
            String image4 = "";
            String image5 = "";
            try {
                if (summons.ImageLocation.size() >= 1) {
                    image1 = summons.ImageLocation.get(0);
                }
                if (summons.ImageLocation.size() >= 2) {
                    image2 = summons.ImageLocation.get(1);
                }
                if (summons.ImageLocation.size() >= 3) {
                    image3 = summons.ImageLocation.get(2);
                }
                if (summons.ImageLocation.size() >= 4) {
                    image4 = summons.ImageLocation.get(3);
                }
                if (!CacheManager.finalImage.isEmpty())
                {
                    image5 = CacheManager.finalImage;
                }
            } catch (Exception e) {

            }

            serializer.startTag(null, "Image");
            serializer.startTag(null, "Image1");
            serializer.text(image1);
            serializer.endTag(null, "Image1");
            serializer.startTag(null, "Image2");
            serializer.text(image2);
            serializer.endTag(null, "Image2");
            serializer.startTag(null, "Image3");
            serializer.text(image3);
            serializer.endTag(null, "Image3");
            serializer.startTag(null, "Image4");
            serializer.text(image4);
            serializer.endTag(null, "Image4");
            serializer.startTag(null, "Image5");
            serializer.text(image5);
            serializer.endTag(null, "Image5");
            serializer.endTag(null, "Image");
            serializer.endTag(null, "Offence");

            //Compound
            serializer.startTag(null, "Compound");
            serializer.startTag(null, "Amount");
            serializer.text(String.valueOf(summons.CompoundAmount1));
            serializer.endTag(null, "Amount");
            serializer.startTag(null, "Latitude");
            serializer.text(String.valueOf(summons.Latitude));
            serializer.endTag(null, "Latitude");
            serializer.startTag(null, "Longitude");
            serializer.text(String.valueOf(summons.Longitude));
            serializer.endTag(null, "Longitude");
            serializer.endTag(null,"Compound");

            serializer.endTag(null,"ns0:Notice");
            serializer.endDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            writer.write(data.toString());
            writer.close();
        } catch (Exception e) {
        }
    }

    private boolean CheckPrint()
    {
        if(!CacheManager.CheckBluetoothStatus())
        {
            CacheManager.EnableBluetooth();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            PrinterUtils.CreateConnection(SettingsHelper.MACAddress);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void ClearFileData() {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            for (File file : dir.listFiles()) {
                file.delete();
            }
        } catch (Exception ex) {

        }
    }

    private void MovePictures()
    {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for(File file : dir.listFiles()) {
            try {
                File newDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
                if (!newDir.exists()) {
                    newDir.mkdirs();
                }

                File newFile = new File(newDir, file.getName());
                FileInputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                    fileOutputStream = new FileOutputStream(newFile);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer);
                    }
                    fileInputStream.close();
                    fileInputStream = null;

                    fileOutputStream.flush();
                    fileOutputStream.close();
                    fileOutputStream = null;

                    file.delete();
                } catch (Exception e) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Location getLocationWithCheckNetworkAndGPS(Context mContext) {
        LocationManager lm = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        boolean isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkLocationEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location networkLocation = null, gpsLocation = null;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        if (isGpsEnabled)
            gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (isNetworkLocationEnabled)
            networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (gpsLocation != null && networkLocation != null) {
            //smaller the number more accurate result will
            if (gpsLocation.getAccuracy() > networkLocation.getAccuracy())
                return networkLocation;
            else
                return gpsLocation;
        } else {
            if (gpsLocation != null) {
                return gpsLocation;
            } else if (networkLocation != null) {
                return networkLocation;
            }
        }

        return null;
    }

    private void DoPrint()
    {
        try
        {
            if(CheckPrint()) {
                PrintingDocument doc = null;

                try {
                    PrinterUtils.OpenConnection();

                    if(PrinterUtils.Connection.isConnected()) {
                        doc = PrinterUtils.CreateNotice(CacheManager.SummonIssuanceInfo);
                        PrinterUtils.Print(doc);

                        Location coordinate = getLocationWithCheckNetworkAndGPS(CacheManager.mContext);

                        if(coordinate != null) {
                            CacheManager.SummonIssuanceInfo.Latitude = coordinate.getLatitude();
                            CacheManager.SummonIssuanceInfo.Longitude = coordinate.getLongitude();
                        }

                        SettingsHelper.IncrementSerialNumber(CacheManager.mContext);

                        try {
                            GenerateXmlNotice(CacheManager.SummonIssuanceInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        try {
//                            MovePictures();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                        final Handler timeHandler = new Handler();
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                AlertMessage(getActivity(), "CETAK", "Cetak Salinan Kedua?", 2);
                            }
                        };
                        timeHandler.postDelayed(run, 8000);

                    } else {
                        AlertMessage(getActivity(),"ERROR", "FAILED TO CONNECT", 1);
                    }
                } catch(Exception e) {
                    AlertMessage(getActivity(),"ERROR", "FAILED TO PRINT", 1);
                } finally {
                    PrinterUtils.CloseConnection();
                }
            }
            else
            {
                CustomAlertDialog.Show(getActivity(), "PRINTER", "CETAK SAMAN GAGAL", 0);
            }
        }
        catch(Exception ex)
        {
            AlertMessage(getActivity(), "Printer", "CETAK SAMAN GAGAL", 0);
        }

        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    private void DoPrintCopy()
    {
        try
        {
            if(CheckPrint())
            {
                PrintingDocument doc = null;

                try {
                    PrinterUtils.OpenConnection();
                    if(PrinterUtils.Connection.isConnected()) {
                        doc = PrinterUtils.CreateNotice(CacheManager.SummonIssuanceInfo);
                        PrinterUtils.Print(doc);

                        AlertMessage(getActivity(), "CETAK", "Cetak Salinan Kedua?", 2);
                    } else {
                        AlertMessage(getActivity(),"ERROR", "FAILED TO CONNECT", 3);
                    }
                } catch(Exception e) {
                    AlertMessage(getActivity(),"ERROR", "FAILED TO PRINT", 3);
                } finally {
                    PrinterUtils.CloseConnection();
                }
            }
            else
            {
                CacheManager.DisableBluetooth();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                AlertMessage(getActivity(), "Printer", "CETAK SAMAN GAGAL", 3);
            }
        }
        catch(Exception ex)
        {
            AlertMessage(getActivity(), "Printer", "CETAK SAMAN GAGAL", 3);
        }

        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }
}
