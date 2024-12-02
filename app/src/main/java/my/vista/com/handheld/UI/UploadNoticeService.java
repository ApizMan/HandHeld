package my.vista.com.handheld.UI;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.TrustAllCertificates;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.Business.VolleySingleton;

/**
 * Created by hp on 18/8/2016.
 */
public class UploadNoticeService extends Service {

    private Handler mHandler = new Handler();
    private Runnable mRun = new Runnable() {
        @Override
        public void run() {
            try {
                try {
                    ClearFileData();
                    if(isInternetAvailable()) {
                        ArrayList<SummonIssuanceInfo> list = DbLocal.GetSummonsPending(CacheManager.mContext);
                        for(SummonIssuanceInfo info : list) {
                            final SummonIssuanceInfo model = info;

                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddkkmmss");
                            try {
                                info.OffenceDateTime = format.parse(info.OffenceDateString);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            final int id = info.NoticeId;
                            Map<String, Object> params = new HashMap<>();
                            params.put("NoticeNo", info.NoticeSerialNo);
                            params.put("VehicleNo", info.VehicleNo);
                            params.put("OffenceDateString", CacheManager.GetOffenceDateString(info.OffenceDateTime));
                            params.put("OfficerID", info.OfficerId);
                            params.put("OfficerSaksi", "TIADA");
                            params.put("HandheldCode", info.HandheldCode);
                            params.put("VehicleType", info.VehicleType);
                            params.put("VehicleMakeModel", info.VehicleMakeModel);
                            params.put("VehicleColor", info.VehicleColor);
                            params.put("RoadTaxNo", info.RoadTaxNo);
                            params.put("OffenceActCode", info.OffenceActCode);
                            params.put("OffenceSectionCode", info.OffenceActCode);
                            params.put("OffenceLocationArea", info.OffenceLocationArea);
                            params.put("OffenceLocation", info.OffenceLocation);
                            params.put("OffenceLocationDetails", info.OffenceLocationDetails);
                            params.put("CompoundAmount", String.valueOf(info.CompoundAmount));
                            params.put("ImageName1", info.ImageLocation1);
                            params.put("ImageName2", info.ImageLocation2);
                            params.put("ImageName3", info.ImageLocation3);
                            params.put("ImageName4", info.ImageLocation4);
                            params.put("ImageName5", info.ImageLocation5);
                            params.put("IsClamping", info.IsClamping);
                            params.put("Notes", info.Notes);
                            params.put("SquarePoleNo", "SQP001");
//                            params.put("Latitude", info.Latitude);
//                            params.put("Longitude", info.Longitude);

                            String url = CacheManager.ServerURL + "UploadNotice";
                            TrustAllCertificates.trustAllHosts();
                            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if(response != null) {
                                                    String statusCode = response.getString("StatusCode");
                                                    if(statusCode.equals("null")) {
                                                        DbLocal.UpdateSentStatus(CacheManager.mContext, id);

                                                        if(!model.ImageLocation1.isEmpty())
                                                            UploadImage(model.ImageLocation1);
                                                        if(!model.ImageLocation2.isEmpty())
                                                            UploadImage(model.ImageLocation2);
                                                        if(!model.ImageLocation3.isEmpty())
                                                            UploadImage(model.ImageLocation3);
                                                        if(!model.ImageLocation4.isEmpty())
                                                            UploadImage(model.ImageLocation4);
                                                        if(!model.ImageLocation5.isEmpty())
                                                            UploadImage(model.ImageLocation5);
                                                    }
                                                    try {
                                                        if(response.getString("StatusDescription").toUpperCase().contains("DUPLICATE")) {
                                                            DbLocal.UpdateSentStatus(CacheManager.mContext, id);
                                                        }
                                                    } catch(Exception e) {

                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            error.printStackTrace();
                                        }
                                    });
									
							postRequest.setRetryPolicy(new DefaultRetryPolicy(
								0,
								DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
								DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            VolleySingleton.getInstance(CacheManager.mContext).addToRequestQueue(postRequest);
                        }

                        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        for(File file : dir.listFiles()) {
                            try {
                                UploadImage(file.getName());
                            }
                            catch (Exception ex) {

                            }
                        }
                    }
                }
                catch (Exception ex) {

                }
            }
            catch (Exception ex) {
            }
            mHandler.postDelayed(this, 30000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void UploadImage(final String imageName) {
        // Define the directory path
        final File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");

        // Check if the directory exists, otherwise create it
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Define the file that needs to be uploaded
        final File file = new File(dir, imageName);

        // Ensure the file exists and is not empty
        if (file.exists() && file.length() > 0) {
            final long fileLength = file.length();
            FileInputStream fileInputStream = null;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] byteArrayImage = null;
            String imageData = "";

            try {
                // Open the file input stream
                fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[8192];  // Buffer for reading the file
                int bytesRead;

                // Read the file and write to ByteArrayOutputStream
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                // Convert the output stream to a byte array
                byteArrayImage = output.toByteArray();

                // Encode the byte array to Base64 string
                imageData = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                // Log the Base64 encoded image data for debugging
                Log.d("ImageData", "Base64 Encoded Image: " + imageData);
                Log.d("ImageData", "Image Data Size: " + byteArrayImage.length);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Ensure input stream is closed
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Prepare JSON object with image data
            JSONObject obj = new JSONObject();
            try {
                obj.put("ImageName", imageName);
                obj.put("ImageData", imageData);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Define the server URL for the upload request
            String url = CacheManager.ServerURL + "UploadImageString";

            // Trust all certificates (use with caution)
            TrustAllCertificates.trustAllHosts();

            // Create a POST request using Volley
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null) {
                                    String statusCode = response.getString("StatusCode");

                                    // Check if the upload was successful based on server response
                                    if (statusCode.equals("null")) {
                                        // Move the uploaded file to the ProcessedImageDir
                                        File newDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ProcessedImageDir");
                                        if (!newDir.exists()) {
                                            newDir.mkdirs();
                                        }

                                        // Create a new file in the ProcessedImageDir
                                        File newFile = new File(newDir, imageName);

                                        // Copy the file to the new directory and delete the original file
                                        FileInputStream fileInputStream = null;
                                        FileOutputStream fileOutputStream = null;
                                        try {
                                            fileInputStream = new FileInputStream(file);
                                            fileOutputStream = new FileOutputStream(newFile);

                                            byte[] buffer = new byte[1024];
                                            int bytesRead;
                                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                                fileOutputStream.write(buffer, 0, bytesRead);
                                            }

                                            // Close streams after file operations
                                            fileInputStream.close();
                                            fileOutputStream.flush();
                                            fileOutputStream.close();

                                            // Delete the original file after processing
                                            file.delete();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error response
                            error.printStackTrace();
                        }
                    });

            // Set the retry policy for the request
            postRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Add the request to the Volley request queue
            VolleySingleton.getInstance(CacheManager.mContext).addToRequestQueue(postRequest);

        } else {
            // Log error if file doesn't exist or is empty
            Log.e("UploadImage", "File does not exist or is empty: " + imageName);
        }
    }

    private void ClearFileData() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for(File file : dir.listFiles()) {
            long diff = ((new Date()).getTime() - file.lastModified()) / 1000 / 60 / 60 / 24;
            if(diff > 14)
                file.delete();
        }

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CustomImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for(File file : dir.listFiles()) {
            long diff = ((new Date()).getTime() - file.lastModified()) / 1000 / 60 / 60 / 24;
            if(diff > 14)
                file.delete();
        }

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ProcessedImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for(File file : dir.listFiles()) {
            long diff = ((new Date()).getTime() - file.lastModified()) / 1000 / 60 / 60 / 24;
            if(diff > 14)
                file.delete();
        }

        dir = new File("/mnt/sdcard/OfflineSummons");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for(File file : dir.listFiles()) {
            long diff = ((new Date()).getTime() - file.lastModified()) / 1000 / 60 / 60 / 24;
            if(diff > 14)
                file.delete();
        }

        DbLocal.CleanOffenceNoticeMaintenance(CacheManager.mContext);
    }

    public boolean isInternetAvailable() {
        return true;
        /*
        try {
            Runtime runtime = Runtime.getRuntime();
            try {
                //10.10.0.78
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 10.10.0.78");
                //Process ipProcess = runtime.exec("/system/bin/ping -c 1 192.168.0.102");
                int     exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            } catch (Exception e) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
        */
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.postDelayed(mRun, 0);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
