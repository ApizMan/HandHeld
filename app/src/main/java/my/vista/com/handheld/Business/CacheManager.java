package my.vista.com.handheld.Business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.widget.Toast;

import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Data.SettingsHelper;

@SuppressWarnings("deprecation")
public final class CacheManager 
{
//	public static String ServerURL = "http://43.252.37.175/ParkingWebService/HandheldService.svc/";
	public static String ServerURL = "http://myenforce.citycarpark.my/HandheldApi_MBK/HandheldService.svc/JSONService/";
	public static String ServerKNURL = "http://myenforce.citycarpark.my/HandheldApi_MBK/HandheldService.svc/JSONService/";

	public static String ServerKuantanURL = "https://mycouncil.citycarpark.my/parking/ctcp/services-listerner_mbk.php?prpid=&action=GetParkingRightByPlateVerify&filterid=";
	/** The User id. */
	public static String UserId = "";
	public static int noticeSerialNumber;
	public static String HandheldId = "";

	public static String officerCode = "";
	public static String officerId = "";
	public static String officerName = "";
	public static String officerDetails = "";
	public static String officerUnit = "";
	public static String finalImage = "";
	public static int publicRequestCode = 0;
	public static int publicResultCode = 0;

	public static double Discount = 20;
	public static double Discount50 = 50;

	public static Uri ImageUri;

	//public static String KodHasil = "703/761310/2B";
	public static String KodHasil = "410/961045";

	public static int imageIndex = 0;

	/** The Log enabled. */
	public static boolean LogEnabled = false;

	public static int BatteryPercentage = 100;
	
	public static Context mContext;

	/** The Summon issuance info. */
	public static my.vista.com.handheld.Entity.SummonIssuanceInfo SummonIssuanceInfo;

	public static my.vista.com.handheld.Entity.NoticeInfo NoticeInfo;

    public static NotificationManager NotificationManagerInstance;

	public static boolean IsNewNotice = true;

	public static boolean HasChecked = false;

	public static boolean IsClearData = false;
	
	// Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    // Message types sent from the BluetoothReadService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;	
    
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	public static final Handler mHandlerBT = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {        	
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                break;
            case MESSAGE_WRITE:
                break;
/*                
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;              
                mEmulatorView.write(readBuf, msg.arg1);
                
                break;
*/                
            case MESSAGE_DEVICE_NAME:
                break;
            case MESSAGE_TOAST:
                Toast.makeText(CacheManager.mContext, msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

	public static BluetoothSerialService mSerialService = null;
    public static String NoticeSerialNo;

	public static String deviceId = null;

	public static void initialize(Context context) {
		mContext = context;
		loadDeviceId();
		loadOfficerId();
		loadImage5();
		loadRequestCode();
		loadResultCode();
	}

	public static void saveOfficerId(String officer) {
		officerId = officer;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("officerId", officer);
		editor.apply();
	}

	private static void loadOfficerId() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		officerId = sharedPreferences.getString("officerId", null);
	}

	public static void saveDeviceId(String device) {
		deviceId = device;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("deviceId", device);
		editor.apply();
	}

	private static void loadDeviceId() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		deviceId = sharedPreferences.getString("deviceId", null);
	}

	public static void saveRequestCode(int requestCode) {
		publicRequestCode = requestCode;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("requestCode", requestCode);
		editor.apply();
	}

	private static void loadRequestCode() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		publicRequestCode = sharedPreferences.getInt("requestCode", 0);
	}

	public static void saveResultCode(int resultCode) {
		publicResultCode = resultCode;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("resultCode", resultCode);
		editor.apply();
	}

	private static void loadResultCode() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		publicResultCode = sharedPreferences.getInt("resultCode", 0);
	}

	public static void saveImage5(String image) {
		finalImage = image;
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("finalImage", image);
		editor.apply();
	}

	private static void loadImage5() {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
		finalImage = sharedPreferences.getString("finalImage", null);
	}

    public static my.vista.com.handheld.Entity.SummonIssuanceInfo updateData(my.vista.com.handheld.Entity.SummonIssuanceInfo info) {
		try {
			info.OffenceDateTime = new Date();

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmaa");
			try {
				info.OffenceDateTime = format.parse(info.OffenceDateString);
			} catch (Exception e) {
				e.printStackTrace();
			}

			info.VehicleMake = info.VehicleMakeModel;
			Cursor act = DbLocal.GetActDescription(mContext, info.OffenceActCode);
			Cursor section = DbLocal.GetSectionData(mContext, info.OffenceSectionCode, info.OffenceActCode);
			info.OffenceAct = act.getString(0);
			info.OffenceSection = section.getString(0) + " " + section.getString(1);
			info.Offence = section.getString(2);
			info.CompoundAmount1 = 0;
			if (info.OffenceSectionCode.length() != 0) {
				Cursor compoundList = DbLocal.GetCompoundAmountDescription(mContext, info.OffenceSectionCode, info.OffenceActCode);
				try {
					if (compoundList != null) {
						if (Float.parseFloat(compoundList.getString(1)) != 0) {
							info.CompoundAmount1 = Float.parseFloat(compoundList.getString(2));
							if(info.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
								info.CompoundAmount1 = Float.parseFloat(compoundList.getString(1));
							info.CompoundAmountDesc1 = compoundList.getString(3);
							if (compoundList.getString(3).length() != 0) {
								if (compoundList.getString(7).length() != 0) {
									info.CompoundAmount2 = Float.parseFloat(compoundList.getString(6));
									if(info.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
										info.CompoundAmount2 = Float.parseFloat(compoundList.getString(5));
									info.CompoundAmountDesc2 = compoundList.getString(7);
								}
								if (compoundList.getString(11).length() != 0) {
									info.CompoundAmount3 = Float.parseFloat(compoundList.getString(10));
									if(info.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
										info.CompoundAmount3 = Float.parseFloat(compoundList.getString(9));
									info.CompoundAmountDesc3 = compoundList.getString(11);
								}
								if (compoundList.getString(15) != null && compoundList.getString(15).length() != 0 && !compoundList.getString(15).equals("null")) {
									info.CompoundAmount4 = Float.parseFloat(compoundList.getString(14));
									if(info.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
										info.CompoundAmount4 = Float.parseFloat(compoundList.getString(13));
									info.CompoundAmountDesc4 = compoundList.getString(15);
								}
							}
						}
					}
				} catch (Exception ex) {

				}
				info.CompoundAmountDescription = CacheManager.GenerateCompoundAmountDescription(String.valueOf(info.CompoundAmount1));
			}
		} catch (Exception ex) {

		}

		return info;
	}

	public static void SetCompoundAmount() {
		try {
			CacheManager.SummonIssuanceInfo.CompoundAmount1 = 0;
			if (CacheManager.SummonIssuanceInfo.OffenceSectionCode.length() != 0) {
				Cursor compoundList = DbLocal.GetCompoundAmountDescription(CacheManager.mContext, CacheManager.SummonIssuanceInfo.OffenceSectionCode, CacheManager.SummonIssuanceInfo.OffenceActCode);
//				Cursor compoundDescriptionList = DbLocal.GetCompoundDescription(mContext, CacheManager.SummonIssuanceInfo.OffenceSectionCode, CacheManager.SummonIssuanceInfo.OffenceActCode);
				try {
					if (compoundList != null) {
						if (Float.parseFloat(compoundList.getString(1)) != 0) {
							CacheManager.SummonIssuanceInfo.CompoundAmount1 = Float.parseFloat(compoundList.getString(2));
							if(CacheManager.SummonIssuanceInfo.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
								CacheManager.SummonIssuanceInfo.CompoundAmount1 = Float.parseFloat(compoundList.getString(1));
							CacheManager.SummonIssuanceInfo.CompoundAmountDesc1 = compoundList.getString(3);
							if (compoundList.getString(3).length() != 0) {
								if (compoundList.getString(7).length() != 0) {
									CacheManager.SummonIssuanceInfo.CompoundAmount2 = Float.parseFloat(compoundList.getString(6));
									if(CacheManager.SummonIssuanceInfo.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
										CacheManager.SummonIssuanceInfo.CompoundAmount2 = Float.parseFloat(compoundList.getString(5));
									CacheManager.SummonIssuanceInfo.CompoundAmountDesc2 = compoundList.getString(7);
								}
								if (compoundList.getString(11).length() != 0) {
									CacheManager.SummonIssuanceInfo.CompoundAmount3 = Float.parseFloat(compoundList.getString(10));
									if(CacheManager.SummonIssuanceInfo.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
										CacheManager.SummonIssuanceInfo.CompoundAmount3 = Float.parseFloat(compoundList.getString(9));
									CacheManager.SummonIssuanceInfo.CompoundAmountDesc3 = compoundList.getString(11);
								}
								if (compoundList.getString(15) != null && compoundList.getString(15).length() != 0 && !compoundList.getString(15).equals("null")) {
									CacheManager.SummonIssuanceInfo.CompoundAmount4 = Float.parseFloat(compoundList.getString(14));
									if(CacheManager.SummonIssuanceInfo.VehicleType.equalsIgnoreCase("MOTOSIKAL"))
										CacheManager.SummonIssuanceInfo.CompoundAmount4 = Float.parseFloat(compoundList.getString(13));
									CacheManager.SummonIssuanceInfo.CompoundAmountDesc4 = compoundList.getString(15);
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.toString();
				}
				CacheManager.SummonIssuanceInfo.CompoundAmountDescription = CacheManager.GenerateCompoundAmountDescription(String.valueOf(CacheManager.SummonIssuanceInfo.CompoundAmount1));
			}
		} catch (Exception ex) {

		}
	}
	
	public static Date GetCompoundDate()
	{
		Date compoundDate = addDate(new Date(), 14);
        return  compoundDate; 
	}

    public static String CompileAddress(String address)
    {
        String strTemp = "";
        for(int i=0;i<address.length();i++)
        {
            strTemp += address.charAt(i);
            if(i%2 == 1 && i != (address.length() - 1))
                strTemp += ':';
        }

        return strTemp;
    }

    public static void ExportDB() {
		try {
			File newDir = new File("/mnt/sdcard/HandheldDatabase");
			if (!newDir.exists()) {
				newDir.mkdirs();
			}
			File dir = new File("/data/data/my.vista.com.handheld/databases");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String fileName = "vista_handheld.db";

			File newFile = new File(newDir, fileName);
			File file = new File(dir, fileName);
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

				//file.delete();

				System.gc();
			} catch (Exception e) {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean CheckBluetoothStatus()
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		return btAdapter.isEnabled();
	}
	
	public static void EnableBluetooth()
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(!btAdapter.isEnabled())
			btAdapter.enable();
	}
	
	public static void DisableBluetooth()
	{
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(btAdapter.isEnabled())
			btAdapter.disable();
	}
	
	public static void DisableWifi(Context context)
	{
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(false);
	}
	
	public static void EnableWifi(Context context)
	{
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}
	
	public static String GetOtherDateString(Date compoundDate)
	{
		String delegate = "dd-MM-yyyy";
		if(compoundDate != null)
			return  (String) DateFormat.format(delegate,compoundDate);
		else
			return "";
	}
	
	static Date addDate(Date date, int d)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, d); // add d days
		date = cal.getTime();

		return date;
	}
	
	static Date addMonth(Date date, int d)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, d); // add d days
		date = cal.getTime();

		return date;
	}

	public static Date addHour(Date oldDate, int numberOfHours) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(oldDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.add(Calendar.HOUR, numberOfHours);
		Date newDate = new Date(c.getTimeInMillis());
		return newDate;
	}

	public static String GetOffenceDateString(Date date)
	{
		String delegate = "yyyyMMddhhmmaa";
		if(date != null)
			return ((String) DateFormat.format(delegate,date)).toUpperCase().replace(".", "");
		else
			return "";
	}

    public static String GetBatteryPercentage() {
		try {
			IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = mContext.registerReceiver(null, iFilter);

			int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
			int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

			float batteryPct = level / (float) scale;

			return String.valueOf((int) (batteryPct * 100));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
    }

	public static String GetDate()
	{
		String delegate = "dd/MM/yyyy";
        return  (String) DateFormat.format(delegate,Calendar.getInstance().getTime()); 
	}

	public static Date addDay(Date oldDate, int numberOfDays) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(oldDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.add(Calendar.DAY_OF_YEAR,numberOfDays);
		Date newDate = new Date(c.getTimeInMillis());
		return newDate;
	}

	public static String GetStandardDateString(Date date)
	{
		String delegate = "yyyyMMddhhmmaa";
		if(date != null)
			return  (String) DateFormat.format(delegate,date);
		else
			return "";
	}
	public static String GetLongDateString(Date date)
	{
		String delegate = "dd MMM yyyy";
		if(date != null)
			return  (String) DateFormat.format(delegate,date);
		else
			return "";
	}
	public static String GetDateString(Date date)
	{
		String delegate = "dd/MM/yyyy";
		if(date != null)
			return  (String) DateFormat.format(delegate,date);
		else
			return "";
	}
	public static String GetTimeString(Date date)
	{
		String delegate = "hh:mm aa";
		if(date != null)
			return  ((String) DateFormat.format(delegate,date)).toUpperCase().replace(".", "");
		else
			return "";
	}
	public static String GetTime()
	{
		String delegate = "hh:mm:ss aa";
        return  ((String) DateFormat.format(delegate,Calendar.getInstance().getTime())).replace(".", "");
	}

	public static boolean Init(Context appContext)
	{
		mContext = appContext;
		return DeviceVerify();
	}
	
	static void copyFile(String inputPath, String inputFile, String outputPath)
	{
		InputStream in = null;
	    OutputStream out = null;
	    try {

	        //create output directory if it doesn't exist
	        File dir = new File (outputPath); 
	        if (!dir.exists())
	        {
	            dir.mkdirs();
	        }


	        in = new FileInputStream(inputPath + inputFile);        
	        out = new FileOutputStream(outputPath + inputFile);

	        byte[] buffer = new byte[1024];
	        int read;
	        while ((read = in.read(buffer)) != -1) {
	            out.write(buffer, 0, read);
	        }
	        in.close();
	        in = null;

	            // write the output file (You have now copied the file)
	            out.flush();
	        out.close();
	        out = null;        

	    }  catch (FileNotFoundException fnfe1) {
	        
	    }
	       catch (Exception e) {
	        
	    }
	}

	public static boolean DeviceVerify()
	{
		SettingsHelper.LoadFile(mContext);
		DbLocal.InitDatabase(mContext);
		DbLocal.CleanTransaction(CacheManager.mContext);
		return SettingsHelper.HandheldCode.length() > 0;
	}

	public static String GetDeviceSerialNo()
	{
		String serialNumber = "";

		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);

			serialNumber = (String) get.invoke(c, "gsm.sn1");
			if (serialNumber.equals(""))
				serialNumber = (String) get.invoke(c, "ril.serialnumber");
			if (serialNumber.equals(""))
				serialNumber = (String) get.invoke(c, "ro.serialno");
			if (serialNumber.equals(""))
				serialNumber = (String) get.invoke(c, "sys.serialnumber");
			if (serialNumber.equals(""))
				serialNumber = Build.SERIAL;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return serialNumber;
	}

	public static void ErrorLog(Exception e)
	{
	}
	
	public static String GenerateCompoundAmountDescription(String strCompoundAmount)
    {
        boolean bValidCompoundAmount = false;
        int nCompoundAmountMaxLen = 12;
        int nCharIndex = 0;
        int nCharProcessed = 0;
        int nChar = 0;
        String strCompoundAmountDes = "";
        String strCharDes = "";
        String strGroupDes = "";
        String strIntegralDes = "";     // Part that is to the left of the decimal separator(.)
        String strFractionalDes = "";   // The part that is to the right of the decimal separator(.)
        String[] strCharDesList = { "", "SATU", "DUA", "TIGA", "EMPAT", "LIMA", "ENAM", "TUJUH", "LAPAN", "SEMBILAN" };

        if (strCompoundAmount.indexOf(".") == -1)
        {
            // Compound amount max length - 3 for ".00" characters
            if (strCompoundAmount.length() > (nCompoundAmountMaxLen - 3))
            {
                ; // Compound amount exceeds max length
            }
            else
            {
                strCompoundAmount += ".00";
                bValidCompoundAmount = true;
            }
        }
        else if ((strCompoundAmount.indexOf(".")) != (strCompoundAmount.length() - 3))
        {
        	if((strCompoundAmount.indexOf(".")) == (strCompoundAmount.length() - 2))
        	{
        		strCompoundAmount += "0";
                bValidCompoundAmount = true;
        	}
            ; // Compound amount has invalid format
        }
        else
            bValidCompoundAmount = true;

        if (bValidCompoundAmount)
        {
            while ((nCharIndex = strCompoundAmount.length() - (nCharProcessed)) > 0)
            {
                if (nCharIndex != 3)
                {
                    nChar = Integer.parseInt(strCompoundAmount.substring(nCharProcessed, nCharProcessed + 1));
                    strCharDes = strCharDesList[nChar];
                }

                switch (nCharIndex)
                {
                    case 1:
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? strCharDes : " " + strCharDes;

                        if (strGroupDes != "")
                            strFractionalDes += (strFractionalDes == "") ? strGroupDes : " " + strGroupDes;

                        if (strFractionalDes != "")
                            strFractionalDes += " SEN";
                        break;

                    case 2:
                        strGroupDes = "";
                        if (strCharDes != "")
                            strGroupDes += strCharDes + " PULUH";
                        break;

                    case 3: break;
                    case 4: // SA
                        switch (Integer.parseInt(strCompoundAmount.substring(nCharProcessed - 1, nCharProcessed)))
                        {
                            case 0:
                                if (strCharDes != "")
                                    strGroupDes += (strGroupDes == "") ? strCharDes : (" " + strCharDes);
                                break;

                            case 1:
                                if (nChar == 0)
                                    strGroupDes += (strGroupDes == "") ? "SEPULUH" : " SEPULUH";
                                else if (nChar == 1)
                                    strGroupDes += (strGroupDes == "") ? "SEBELAS" : " SEBELAS";
                                else
                                    strGroupDes += (strGroupDes == "") ? (strCharDes + " BELAS") : (" " + strCharDes + " BELAS");
                                break;

                            default:
                                strGroupDes += (nChar == 0) ? " PULUH" : (" PULUH " + strCharDes);
                                break;
                        }

                        if (strGroupDes != "")
                            strIntegralDes += (strIntegralDes == "") ? strGroupDes : " " + strGroupDes;

                        if (strIntegralDes != "")
                            strIntegralDes += " RINGGIT";
                        break;

                    case 5:  // PULUH
                        if ((strCharDes != "") && (nChar != 1))
                            strGroupDes += (strGroupDes == "") ? strCharDes : (" " + strCharDes);
                        break;

                    case 7: // RIBU
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? strCharDes : " " + strCharDes;

                        if (strGroupDes != "")
                        {
                            strGroupDes += " RIBU";
                            strIntegralDes += (strIntegralDes == "") ? strGroupDes : " " + strGroupDes;
                        }
                        break;

                    case 10: // JUTA
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? strCharDes : " " + strCharDes;

                        if (strGroupDes != "")
                        {
                            strGroupDes += " JUTA";
                            strIntegralDes += (strIntegralDes == "") ? strGroupDes : " " + strGroupDes;
                        }
                        break;

                    case 8:  // RIBUAN PULUH
                    case 11: // JUTAAN PULUH
                        if (strCharDes != "")
                            strGroupDes += (strGroupDes == "") ? (strCharDes + " PULUH") : (" " + strCharDes + " PULUH");
                        break;

                    case 6:  // RATUS
                    case 9:  // RIBUAN RATUS
                    case 12: // JUTAAN RATUS
                        strGroupDes = "";
                        if (strCharDes != "")
                            strGroupDes += strCharDes + " RATUS";
                        break;
                }

                nCharProcessed++;
            }

            if ((strIntegralDes != "") && (strFractionalDes != ""))
                strCompoundAmountDes = strIntegralDes + " DAN " + strFractionalDes;
            else if (strIntegralDes != "")
                strCompoundAmountDes = strIntegralDes;
            else
                strCompoundAmountDes = strFractionalDes;
        }

        return strCompoundAmountDes;
    }
}
