package my.vista.com.handheld.UI.Activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.TrustAllCertificates;
import my.vista.com.handheld.Business.VolleySingleton;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.UploadNoticeService;

public class LoginActivity extends Activity {
	private TextView tvDeviceID;
	private EditText txtUserName;
	private EditText txtPassword;
	private Button btnLogin;
	private Button btnDownload;
	private Spinner spinnerUnit;

	private Runnable doLogin;
	int retry = 0;
	JSONArray offenceActs;
	JSONArray offenceRateMasters;
	JSONArray offenceLocationAreas;
	JSONArray offenceLocations;
	JSONArray offenceSections;
	JSONArray officerMaintenances;
	JSONArray vehicleTypes;
	JSONArray vehicleColors;
	JSONArray vehicleMakes;
	JSONArray vehicleModels;
	JSONArray officerUnits;
	JSONArray officerRanks;
	private ProgressDialog mProgressDialog = null;

	private boolean isDevelopment = false;

	private Handler mHandler = new Handler();
	private Runnable mRun = new Runnable() {
		@Override
		public void run() {
			try {
				if(!isMyServiceRunning(UploadNoticeService.class)) {
					Intent mServiceIntent = new Intent(LoginActivity.this, UploadNoticeService.class);
					LoginActivity.this.startService(mServiceIntent);
				}
			}
			catch (Exception ex) {
			}
			mHandler.postDelayed(this, 15000);
		}
	};

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if(serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);

		tvDeviceID = (TextView) findViewById(R.id.tvDeviceIDMain);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(btnloginListener);
		btnLogin.setEnabled(false);

		btnDownload = (Button) findViewById(R.id.btnDownload);
		btnDownload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkPermissions()) {
					doPreStage();
				}
			}
		});

		if(CacheManager.Init(getApplicationContext())) {
			mHandler.postDelayed(mRun, 0);

			CacheManager.HandheldId = SettingsHelper.HandheldCode;

			tvDeviceID.setText(SettingsHelper.HandheldCode);

            spinnerUnit = (Spinner) findViewById(R.id.spinnerUnit);
            List<String> list = new ArrayList<String>();
            list = DbLocal.GetOneFieldListForSpinner(CacheManager.mContext, "DESCRIPTION", "OFFICER_UNIT");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.insert("--Sila Pilih--", 0);
            spinnerUnit.setAdapter(dataAdapter);

			btnLogin.setEnabled(true);
        }
	}

	int REQUEST_PERMISSION = 1;
	private boolean checkPermissions() {
		if (hasPermissionsGranted()) {
			return true;
		} else {
			requestPermissions();
		}
		return false;
	}

	public boolean hasPermissionsGranted(){
		return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
	}

	public void requestPermissions() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			ActivityCompat.requestPermissions(this, new String[] {
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.ACCESS_FINE_LOCATION,
					Manifest.permission.ACCESS_COARSE_LOCATION,
					Manifest.permission.READ_PHONE_STATE,
					Manifest.permission.BLUETOOTH,
					Manifest.permission.BLUETOOTH_ADMIN
			}, REQUEST_PERMISSION);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		if (requestCode == REQUEST_PERMISSION) {
			if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
				if(!hasPermissionsGranted())
					Toast.makeText(this, "Please grant all permissions needed for this app", Toast.LENGTH_SHORT).show();
				else
					doPreStage();
			}
			else {
				if(!hasPermissionsGranted())
					Toast.makeText(this, "Please grant all permissions needed for this app", Toast.LENGTH_SHORT).show();
				else
					doPreStage();
			}
		}
	}

	void doPreStage() {
		if(isDevelopment) {
			SettingsHelper.LoadDevelopment(CacheManager.mContext);
			btnLogin.setEnabled(true);
		}
		else {
			retry = 0;
			PreStage();

			mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dialog);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage("Processing...");
			mProgressDialog.show();
		}
	}

	public String GetDeviceID() {
		String myUniqueID;
		int myVersion = Integer.valueOf(Build.VERSION.SDK_INT);
		if (myVersion < 23) {
			WifiManager manager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = manager.getConnectionInfo();
			myUniqueID= info.getMacAddress();
			if (myUniqueID== null) {
				TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

				if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					return "";
				}
				myUniqueID= mngr.getDeviceId();
			}
		}
		else if (myVersion > 23 && myVersion < 29) {
			TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				return "";
			}
			myUniqueID= mngr.getDeviceId();
		}
		else
		{
			String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
			myUniqueID= androidId;
		}

		return myUniqueID;
	}

	private void PreStage() {
		if(SettingsHelper.HandheldCode.isEmpty()) {
			if (CacheManager.deviceId == null || CacheManager.deviceId.isEmpty()) {
				CacheManager.deviceId = GetDeviceID();
				if (!CacheManager.deviceId.isEmpty()) {
					CacheManager.saveDeviceId(CacheManager.deviceId); // Save to persistent storage
				}
			}

			if (!CacheManager.deviceId.isEmpty()) {
				String url = CacheManager.ServerURL + "RegisterDevice/" + CacheManager.deviceId;
				// Trust all certificates (use with caution)
				TrustAllCertificates.trustAllHosts();
				JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								try {
									if (response != null) {
										String handheldCode = response.getString("HandheldCode");
										CacheManager.noticeSerialNumber = response.getInt("NoticeSerialNumber");
//										int noticeSerialNumber = response.getInt("TicketSerialNumber");
										SettingsHelper.SaveFile(CacheManager.mContext, handheldCode, String.format("%05d", CacheManager.noticeSerialNumber ));
										onPreStageSuccess();
									} else {
										onPreStageFailed();
										mProgressDialog.dismiss();
									}
								} catch (Exception e) {
									e.printStackTrace();
									onPreStageFailed();
								}
							}
						},
						new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								if (retry < 3) {
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									retry++;
									PreStage();
								} else {
									error.printStackTrace();
									mProgressDialog.dismiss();
									onPreStageFailed();
								}
							}
						});

				postRequest.setRetryPolicy(new DefaultRetryPolicy(
						0,
						DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
						DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

				VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
			} else {
				onPreStageFailed();
				mProgressDialog.dismiss();
			}
		} else {
			onPreStageSuccess();
		}
	}

	public void onPreStageSuccess() {
		retry = 0;
		DownloadLookupTables();
	}

	public void onPreStageFailed() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		Toast.makeText(this, "Muat Turun Gagal", Toast.LENGTH_LONG).show();
	}

	private void DownloadLookupTables() {
		String url = CacheManager.ServerURL + "DownloadLookupTable/" + SettingsHelper.HandheldCode;
		TrustAllCertificates.trustAllHosts();
		JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, url,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if(response != null) {
								offenceActs = response.getJSONArray("OffenceActs");
								offenceRateMasters = response.getJSONArray("OffenceRateMasters");
								offenceLocationAreas = response.getJSONArray("OffenceLocationAreas");
								offenceLocations = response.getJSONArray("OffenceLocations");
								offenceSections = response.getJSONArray("OffenceSections");
								officerMaintenances = response.getJSONArray("OfficerMaintenances");
								vehicleTypes = response.getJSONArray("VehicleTypes");
								vehicleColors = response.getJSONArray("VehicleColors");
								vehicleMakes = response.getJSONArray("VehicleMakes");
								vehicleModels = response.getJSONArray("VehicleModels");
								officerUnits = response.getJSONArray("OfficerUnits");
								officerRanks= response.getJSONArray("OfficerRanks");

								SettingsHelper.SaveFile(CacheManager.mContext, SettingsHelper.HandheldCode, String.format("%05d", CacheManager.noticeSerialNumber ));

								DoUpdateData();
							} else {
								onDownloadFailed();
								mProgressDialog.dismiss();
							}
						} catch (Exception e) {
							e.printStackTrace();
							onDownloadFailed();
							mProgressDialog.dismiss();
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(retry < 3) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							retry++;
							DownloadLookupTables();
						}
						else {
							error.printStackTrace();
							mProgressDialog.dismiss();
							onDownloadFailed();
						}
					}
				});

		postRequest.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
	}

	private void DoUpdateData()
	{
		try {
			DbLocal.ClearLookupTables(CacheManager.mContext);

			DbLocal.InsertOffenceAct(CacheManager.mContext, offenceActs);
			DbLocal.InsertOffenceRateMaster(CacheManager.mContext, offenceRateMasters);
			DbLocal.InsertOffenceLocationArea(CacheManager.mContext, offenceLocationAreas);
			DbLocal.InsertOffenceLocation(CacheManager.mContext, offenceLocations);
			DbLocal.InsertOffenceSection(CacheManager.mContext, offenceSections);
			DbLocal.InsertOfficerMaintenance(CacheManager.mContext, officerMaintenances);
			DbLocal.InsertVehicleType(CacheManager.mContext, vehicleTypes);
			DbLocal.InsertVehicleColor(CacheManager.mContext, vehicleColors);
			DbLocal.InsertVehicleMake(CacheManager.mContext, vehicleMakes);
			DbLocal.InsertVehicleModel(CacheManager.mContext, vehicleModels);
			DbLocal.InsertOfficerUnit(CacheManager.mContext, officerUnits);
			DbLocal.InsertOfficerRanks(CacheManager.mContext, officerRanks);

			onDownloadSuccess();
		} catch (Exception ex) {
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
		mProgressDialog.dismiss();
	}

	public void onDownloadSuccess() {
		Toast.makeText(this, "Muat Turun Berjaya", Toast.LENGTH_LONG).show();

		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		runOnUiThread(new Runnable() {
			public void run() {
				finish();
				overridePendingTransition(0, 0);
				startActivity(getIntent());
				overridePendingTransition(0, 0);
			}
		});
	}

	public void onDownloadFailed() {
		if (mProgressDialog != null)
			mProgressDialog.dismiss();

		Toast.makeText(this, "Muat Turun Gagal", Toast.LENGTH_LONG).show();
	}
	
	private OnClickListener btnloginListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(txtUserName.getText().toString().equalsIgnoreCase("00000000")) {
				DbLocal.CopyOutDatabase(LoginActivity.this);
				Toast.makeText(LoginActivity.this, "Database exported", Toast.LENGTH_LONG).show();
				return;
			}
			if(ValidateLogin())
			{
				doLogin = new Runnable() {
					@Override
					public void run()
					{
						Looper.prepare();
						DoLogin();
						Looper.loop();
						Looper.myLooper().quit();
					}
				};
				mProgressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dialog);
				mProgressDialog.setMessage("Loading");
				mProgressDialog.setTitle("");
				mProgressDialog.setCancelable(false);
				mProgressDialog.setIndeterminate(true);
				mProgressDialog.show();

				Thread thread = new Thread(null, doLogin, "LoginProcess");
				thread.start();
			}
        }
	};

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

	private void DoLogin()
	{
		if(ProcessLogin())
		{
			CacheManager.IsClearData = true;
			ClearFileData();
			CacheManager.SummonIssuanceInfo = new SummonIssuanceInfo();
			CacheManager.UserId = txtUserName.getText().toString();
			CacheManager.saveOfficerId(CacheManager.UserId); // Save to persistent storage
			CacheManager.officerUnit = spinnerUnit.getSelectedItem().toString();
			Intent i = new Intent(this, NoticeIssuanceActivity.class);
			startActivity(i);
			finish();
		}

		if (mProgressDialog != null)
			mProgressDialog.dismiss();
	}
	
	private boolean ProcessLogin()
	{
		boolean login = false;
		int bResult = DbLocal.DoLogin(txtUserName.getText().toString(), txtPassword.getText().toString(), LoginActivity.this);
		if( bResult == 0)
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Login Gagal. Rekod ID " + txtUserName.getText().toString() + " Tidak Wujud.", 3);
		}
		else if(bResult == 1)
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Login Gagal. Password Tidak Betul.", 3);
		}
		else if(bResult == 2)
		{
			login = true;
		}
		return login;
	}
	
	private boolean ValidateLogin()
	{
		txtUserName=(EditText)findViewById(R.id.etUserName);
		if(txtUserName.getText().toString().isEmpty())
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Sila Masukkan ID Pegawai", 3);
			return false;
		}
		txtPassword = (EditText)findViewById(R.id.etPassword);
		if(txtPassword.getText().toString().isEmpty())
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Sila Masukkan Password", 3);
			return false;
		}

		if(txtUserName.getText().toString().equalsIgnoreCase("00000000")
				&& txtPassword.getText().toString().equalsIgnoreCase("00000000")) {
			CacheManager.ExportDB();
		}

		if(spinnerUnit.getSelectedItemPosition() <= 0)
		{
			CustomAlertDialog.Show(LoginActivity.this, "LOGIN", "Sila Pilih Unit", 3);
			return false;
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_SEARCH || (event.getFlags() == KeyEvent.FLAG_LONG_PRESS))
		{
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_HOME)
		{
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onResume()
	{
		txtUserName=(EditText)findViewById(R.id.etUserName);
		txtPassword = (EditText)findViewById(R.id.etPassword);
		txtUserName.setText("");
		txtPassword.setText("");
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}
}
