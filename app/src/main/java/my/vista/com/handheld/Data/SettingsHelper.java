package my.vista.com.handheld.Data;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.text.format.Time;

import my.vista.com.handheld.Business.CacheManager;

public class SettingsHelper {
    private static final String PREFS_NAME = "DeviceData";
    private static final String macAddressKey = "MAC_ADDRESS";
    private static final String handheldCodeKey = "HANDHELD_CODE";
    private static final String noticeSerialNumberKey = "NOTICE_SERIAL_NUMBER";
    private static final String yearKey = "YEAR";

	public static String MACAddress = "";
	public static String HandheldCode = "";
	public static String NoticeSerialNumber = "";
    public static String Year = "";

	public static void LoadFile(Context context) {
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		MACAddress = settings.getString(macAddressKey, "");
        HandheldCode = settings.getString(handheldCodeKey, "");
		NoticeSerialNumber = settings.getString(noticeSerialNumberKey, "");
        Year = settings.getString(yearKey, "");

        CompareWithDB(context);
	}

    public static void LoadDevelopment(Context context) {
        Time now = new Time();
        now.setToNow();
        String year = String.valueOf(now.YEAR);
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        MACAddress = "";
        HandheldCode = "ZZ9";
        NoticeSerialNumber = "00001";
        Year = year;
        saveMacAddress(context);
        saveHandheldCode(context);
        saveNoticeSerialNumber(context);
        saveYear(context);

        DbLocal.SaveNoticeNumber(context, NoticeSerialNumber);
    }

    public static void IncrementSerialNumber(Context context)
    {
        CheckYear(context);
        int serialNumber = 1;

        try {
            serialNumber = Integer.parseInt(NoticeSerialNumber);
        } catch(Exception e) {

        }
        serialNumber++;

        NoticeSerialNumber = String.format("%05d", serialNumber);
        saveNoticeSerialNumber(context);

        CompareWithDB(context);
    }

    public static void CheckYear(Context context)
    {
        Time now = new Time();
        now.setToNow();
        String year = String.valueOf(now.YEAR);
        if(!Year.equalsIgnoreCase(year))
        {
            Year = year;
            NoticeSerialNumber = "00001";

            DbLocal.SaveNoticeNumber(context, NoticeSerialNumber);
        }
        saveNoticeSerialNumber(context);
        saveYear(context);
    }

    public static void SaveFile(Context context, String handheldCode, String noticeSerialNumber) {
        Time now = new Time();
        now.setToNow();
        String year = String.valueOf(now.YEAR);

        MACAddress = "";
        HandheldCode = handheldCode;
        NoticeSerialNumber = noticeSerialNumber;
        Year = year;
        saveMacAddress(context);
        saveHandheldCode(context);

        int prevSerialNumber = 1, serialNumber = 1;

        try {
            serialNumber = Integer.parseInt(NoticeSerialNumber);
        } catch(Exception e) {

        }

        try {
            prevSerialNumber = Integer.parseInt(getNoticeSerialNumber(context));
        } catch(Exception e) {

        }

        if(serialNumber < prevSerialNumber) {
            serialNumber = prevSerialNumber;
        }

        NoticeSerialNumber = String.format("%05d", serialNumber);

        saveNoticeSerialNumber(context);
        saveYear(context);

        CompareWithDB(context);
    }

    public static void CompareWithDB(Context context) {
        try {
            String delegate = "yy";
            String year = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());

            String nextNumber = DbLocal.GetNextNoticeNumber(context);
            String lastNumber = DbLocal.GetLastNoticeNumber(context, HandheldCode + year);

            int serialNumber = 1, dbSerialNumber = 1, lastSerialNumber = 1;

            try {
                serialNumber = Integer.parseInt(NoticeSerialNumber);
            } catch(Exception e) {

            }

            try {
                dbSerialNumber = Integer.parseInt(nextNumber);
            } catch(Exception e) {

            }

            try {
                lastSerialNumber = Integer.parseInt(lastNumber);
            } catch(Exception e) {

            }

            if(serialNumber < dbSerialNumber) {
                NoticeSerialNumber = nextNumber;
                saveNoticeSerialNumber(context);
            }

            if(serialNumber < lastSerialNumber) {
                NoticeSerialNumber = lastNumber;
                saveNoticeSerialNumber(context);
            }

            CacheManager.NoticeSerialNo = NoticeSerialNumber;
            DbLocal.SaveNoticeNumber(context, NoticeSerialNumber);
        } catch (Exception e) {

        }
    }

    public static void SaveMacAddress(Context context, String macAddress) {
        MACAddress = macAddress;
        saveMacAddress(context);
    }

    public static String getMacAddress(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(macAddressKey, "");
    }

    public static String getHandheldCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(handheldCodeKey, "");
    }

    public static String getNoticeSerialNumber(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(noticeSerialNumberKey, "");
    }

    public static String getYear(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(yearKey, "");
    }

    private static void saveMacAddress(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(macAddressKey, MACAddress);
        editor.commit();
    }

    private static void saveHandheldCode(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(handheldCodeKey, HandheldCode);
        editor.commit();
    }

    private static void saveNoticeSerialNumber(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(noticeSerialNumberKey, NoticeSerialNumber);
        editor.commit();
    }

    private static void saveYear(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(yearKey, Year);
        editor.commit();
    }
}