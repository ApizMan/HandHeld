package my.vista.com.handheld.UI.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.PrinterUtils;
import my.vista.com.handheld.Business.PrintingDocument;
import my.vista.com.handheld.Business.VolleySingleton;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.Entity.NoticeInfo;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;

public class ReceiptActivity extends AppCompatActivity {
	private Runnable doPrint;
	private ProgressDialog mProgressDialog = null;
	int retry = 0;
	Date paymentDate = new Date();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receipt);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		if(CacheManager.HandheldId.isEmpty()) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finishAffinity();
		} else {
			findViewById(R.id.btnPrint).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					EditText txtSearch = (EditText)findViewById(R.id.etNoticeNo);
					if(txtSearch.getText().toString().isEmpty()) {
						CustomAlertDialog.Show(view.getContext(), "Error", "Sila isikan nombor kompaun", 3);
					} else {
						retry = 0;
						SearchNoticeNo(txtSearch.getText().toString());

						mProgressDialog = new ProgressDialog(view.getContext(), R.style.AppTheme_Dialog);
						mProgressDialog.setIndeterminate(true);
						mProgressDialog.setMessage("Checking...");
						mProgressDialog.show();
					}
				}
			});
		}
	}

	private void SearchNoticeNo(final String noticeNo) {
		Map<String, Object> params = new HashMap<>();
		params.put("NoticeNo", noticeNo);
		params.put("VehicleNo", "");

		String url = CacheManager.ServerURL + "GetReceiptNotice";
		JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response != null) {
								JSONArray notices = response.getJSONArray("Notices");
								if(notices.length() > 0) {
									onCheckingSuccess(notices.getJSONObject(0));
								} else {
									onCheckingFailed();
								}
							} else {
								onCheckingFailed();
							}
						} catch (Exception e) {
							e.printStackTrace();
							onCheckingFailed();
						}
						mProgressDialog.dismiss();
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
							SearchNoticeNo(noticeNo);
						}
						else {
							error.printStackTrace();
							mProgressDialog.dismiss();
							onCheckingFailed();
						}
					}
				});

		postRequest.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		VolleySingleton.getInstance(this).addToRequestQueue(postRequest);
	}

	private void onCheckingSuccess(JSONObject info) {
		try {
			CacheManager.NoticeInfo = new NoticeInfo();
			CacheManager.NoticeInfo.NoticeNo = info.getString("NoticeNo");
			CacheManager.NoticeInfo.VehicleNo = info.getString("VehicleNo");
			CacheManager.NoticeInfo.OfficerId = info.getString("OfficerID");
			CacheManager.NoticeInfo.OffenceDateString = info.getString("OffenceDateString");
			CacheManager.NoticeInfo.OffenceLocation = info.getString("OffenceArea");

			try {
				CacheManager.NoticeInfo.ClampingAmount = info.getDouble("ClampingAmount");
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				CacheManager.NoticeInfo.CompoundAmount = info.getDouble("CompoundAmount");
			} catch (Exception e) {
				e.printStackTrace();
			}

			CacheManager.NoticeInfo.IsClamping = info.getBoolean("IsClamping");

			try {
				CacheManager.NoticeInfo.PaidAmount = info.getDouble("PaidCompoundAmount");
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				CacheManager.NoticeInfo.ClampingPaidAmount = info.getDouble("PaidClampingAmount");
			} catch (Exception e) {
				e.printStackTrace();
			}

			CacheManager.NoticeInfo.ReceiptNumber = info.getString("ReceiptNumber");
			if(CacheManager.NoticeInfo.ReceiptNumber.isEmpty() || CacheManager.NoticeInfo.ReceiptNumber.equalsIgnoreCase("null")) {
				CacheManager.NoticeInfo.ReceiptNumber = info.getString("ClampingReceiptNumber");
			}

			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				CacheManager.NoticeInfo.OffenceDateTime = format.parse(CacheManager.NoticeInfo.OffenceDateString);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String paymentDateString = info.getString("PaymentDateString");
				if(paymentDateString.isEmpty() || paymentDateString.equalsIgnoreCase("null") || paymentDateString.equalsIgnoreCase("00010101000000")) {
					paymentDateString = info.getString("ClampingPaymentDateString");
				}
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
				paymentDate = format.parse(paymentDateString);
			} catch (Exception e) {
				e.printStackTrace();
			}

			AlertMessage(this, "CETAK", "Cetak Salinan Resit?", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onCheckingFailed() {
		Toast.makeText(this, "Semakan Gagal", Toast.LENGTH_SHORT).show();
	}

	public void AlertMessage(final Context context, String title, String message, int type)
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
			builder.setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mProgressDialog = new ProgressDialog(ReceiptActivity.this, R.style.AppTheme_Dialog);
					mProgressDialog.setMessage("Loading");
					mProgressDialog.setTitle("");
					mProgressDialog.setCancelable(false);
					mProgressDialog.setIndeterminate(true);
					mProgressDialog.show();

					doPrint = new Runnable() {
						@Override
						public void run()
						{
							Looper.prepare();
							DoPrint();
							Looper.loop();
							Looper.myLooper().quit();
						}
					};

					Thread thread = new Thread(null, doPrint, "PrintProcess");
					thread.start();
				}
			});
			builder.setNegativeButton("No", null);
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
					mProgressDialog = new ProgressDialog(ReceiptActivity.this, R.style.AppTheme_Dialog);
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
					//finish();
				}
			});
		}
		if(type == 3)
		{
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					AlertMessage(ReceiptActivity.this, "CETAK", "Cetak Salinan Kedua?", 2);
				}
			});
		}
		builder.show();
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
						doc = PrinterUtils.CreateReceipt(CacheManager.NoticeInfo, paymentDate);
						PrinterUtils.Print(doc);

						AlertMessage(this, "CETAK", "Cetak Salinan Kedua?", 2);
					} else {
						AlertMessage(this,"CETAK", "Cetak Gagal. Cetak Semula?", 2);
					}
				} catch(Exception e) {
					AlertMessage(this,"CETAK", "Cetak Gagal. Cetak Semula?", 2);
				} finally {
					PrinterUtils.CloseConnection();
				}
			}
			else
			{
				CustomAlertDialog.Show(this, "CETAK", "Cetak Gagal. Cetak Semula?", 2);
			}
		}
		catch(Exception ex)
		{
			AlertMessage(this, "CETAK", "Cetak Gagal. Cetak Semula?", 2);
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
						doc = PrinterUtils.CreateReceipt(CacheManager.NoticeInfo, paymentDate);
						PrinterUtils.Print(doc);

						AlertMessage(this, "CETAK", "Cetak Salinan Kedua?", 2);
					} else {
						AlertMessage(this,"CETAK", "Cetak Gagal. Cetak Semula?", 2);
					}
				} catch(Exception e) {
					AlertMessage(this,"CETAK", "Cetak Gagal. Cetak Semula?", 2);
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

				AlertMessage(this, "CETAK", "Cetak Gagal. Cetak Semula?", 2);
			}
		}
		catch(Exception ex)
		{
			AlertMessage(this, "CETAK", "Cetak Gagal. Cetak Semula?", 2);
		}

		if (mProgressDialog != null)
			mProgressDialog.dismiss();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}

		return super.onOptionsItemSelected(item);
	}
}
