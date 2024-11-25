package my.vista.com.handheld.UI;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.vista.com.handheld.Business.CacheManager;
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
                            params.put("HandheldCode", info.HandheldCode);
                            params.put("VehicleType", info.VehicleType);
                            params.put("VehicleMakeModel", info.VehicleMakeModel);
                            params.put("VehicleColor", info.VehicleColor);
                            params.put("RoadTaxNo", info.RoadTaxNo);
                            params.put("OffenceActCode", info.OffenceActCode);
                            params.put("OffenceSectionCode", info.OffenceSectionCode);
                            params.put("OffenceArea", info.OffenceLocationArea);
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
                            params.put("OfficerUnit", info.OfficerUnit);
                            params.put("Latitude", info.Latitude);
                            params.put("Longitude", info.Longitude);

                            String url = CacheManager.ServerURL + "UploadNotice";
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

                        File dir = new File("/mnt/sdcard/PendingImageDir");
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
        final File dir = new File("/mnt/sdcard/PendingImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final File file = new File(dir, imageName);
        if(file.exists()) {
            final long fileLength = file.length();

            FileInputStream fileInputStream = null;
            byte[] byteArrayImage;
            byte[] buffer = new byte[8192];
            int bytesRead;
            String imageData = "";

            try {
                fileInputStream = new FileInputStream(file);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try {
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byteArrayImage = output.toByteArray();
                imageData = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            } catch (Exception e) {

            }

            JSONObject obj = new JSONObject();
            try {
                obj.put("ImageName", imageName);
                obj.put("ImageData", imageData);
            }catch (Exception ex) {

            }

            String url = CacheManager.ServerURL + "UploadImageString";
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response != null) {
                                String statusCode = response.getString("StatusCode");
                                if(statusCode.equals("null")) {
                                    File newDir = new File("/mnt/sdcard/ProcessedImageDir");
                                    if (!newDir.exists()) {
                                        newDir.mkdirs();
                                    }

                                    File newFile = new File(newDir, imageName);
                                    FileInputStream fileInputStream = null;
                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        fileInputStream = new FileInputStream(file);
                                        fileOutputStream = new FileOutputStream(newFile);

                                        byte[] buffer = new byte[1024];
                                        while ((fileInputStream.read(buffer)) != -1) {
                                            fileOutputStream.write(buffer);
                                        }
                                        fileInputStream.close();

                                        fileOutputStream.flush();
                                        fileOutputStream.close();

                                        file.delete();
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        }catch(Exception e) {
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
    }

    private void ClearFileData() {
        File dir = new File("/mnt/sdcard/CustomImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for(File file : dir.listFiles()) {
            long diff = ((new Date()).getTime() - file.lastModified()) / 1000 / 60 / 60 / 24;
            if(diff > 14)
                file.delete();
        }

        dir = new File("/mnt/sdcard/PendingImageDir");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for(File file : dir.listFiles()) {
            long diff = ((new Date()).getTime() - file.lastModified()) / 1000 / 60 / 60 / 24;
            if(diff > 14)
                file.delete();
        }

        dir = new File("/mnt/sdcard/ProcessedImageDir");
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
