package my.vista.com.handheld.UI.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.PrinterUtils;
import my.vista.com.handheld.Business.PrintingDocument;
import my.vista.com.handheld.Business.VolleySingleton;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;

public class NoticeActivity extends AppCompatActivity {
	private Runnable doPrint;
	private ProgressDialog mProgressDialog = null;
	int retry = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		if(CacheManager.HandheldId.isEmpty()) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finishAffinity();
		} else {
			((CheckBox)findViewById(R.id.chkClamping)).setChecked(CacheManager.NoticeInfo.IsClamping);
			((TextView)findViewById(R.id.tvNoticeNo)).setText(CacheManager.NoticeInfo.NoticeNo);
			((TextView)findViewById(R.id.tvVehicleNo)).setText(CacheManager.NoticeInfo.VehicleNo);
			((TextView)findViewById(R.id.tvOffenceLocation)).setText(CacheManager.NoticeInfo.OffenceLocation);

			if(!CacheManager.NoticeInfo.IsClamping) {
				((CheckBox)findViewById(R.id.chkCompound)).setChecked(true);
				findViewById(R.id.chkCompound).setEnabled(false);
			}

			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmaa");
			String date = "";
			try {
				CacheManager.NoticeInfo.OffenceDateTime = format.parse(CacheManager.NoticeInfo.OffenceDateString);
				String delegate = "dd/MM/yyyy";
				date = (String)DateFormat.format(delegate, CacheManager.NoticeInfo.OffenceDateTime);
			} catch (Exception e) {
				e.printStackTrace();
			}

			((TextView)findViewById(R.id.tvOffenceDate)).setText(date);
			((TextView)findViewById(R.id.tvCompoundAmount)).setText(String.format("%.2f", CacheManager.NoticeInfo.CompoundAmount));
			((TextView)findViewById(R.id.tvClampingAmount)).setText(String.format("%.2f", CacheManager.NoticeInfo.ClampingAmount));
			findViewById(R.id.tvClampingAmount).setVisibility(CacheManager.NoticeInfo.IsClamping ? View.VISIBLE : View.INVISIBLE);
			findViewById(R.id.chkClamping).setVisibility(CacheManager.NoticeInfo.IsClamping ? View.VISIBLE : View.INVISIBLE);
			findViewById(R.id.layDiscount).setVisibility(CacheManager.NoticeInfo.IsClamping ? View.VISIBLE : View.INVISIBLE);

			findViewById(R.id.btnPay).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					AlertMessage(view.getContext(), "BAYAR", "Bayar Notis?", 1);
				}
			});

			((CheckBox)findViewById(R.id.chkClamping)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					if(b) {
						findViewById(R.id.layDiscount).setVisibility(View.VISIBLE);
						findViewById(R.id.chkCompound).setEnabled(true);
						setDiscount();
					} else {
						((CheckBox)findViewById(R.id.chkCompound)).setChecked(true);
						findViewById(R.id.chkCompound).setEnabled(false);
						findViewById(R.id.layDiscount).setVisibility(View.GONE);
						((TextView)findViewById(R.id.tvClampingAmount)).setText(String.format("%.2f", 0f));
					}
				}
			});

			((CheckBox)findViewById(R.id.chkDiscount)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
					if(b) {
						findViewById(R.id.radDiscount).setVisibility(View.VISIBLE);
						setDiscount();
					} else {
						findViewById(R.id.radDiscount).setVisibility(View.GONE);
						((TextView)findViewById(R.id.tvClampingAmount)).setText(String.format("%.2f", CacheManager.NoticeInfo.ClampingAmount));
					}
				}
			});

			findViewById(R.id.rad20).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					setDiscount();
				}
			});

			findViewById(R.id.rad50).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					setDiscount();
				}
			});
		}
	}

	public void setDiscount()
	{
		if(((RadioButton)findViewById(R.id.rad20)).isChecked()) {
			((TextView)findViewById(R.id.tvClampingAmount)).setText(String.format("%.2f", CacheManager.NoticeInfo.ClampingAmount - CacheManager.Discount));
		} else {
			((TextView)findViewById(R.id.tvClampingAmount)).setText(String.format("%.2f", CacheManager.NoticeInfo.ClampingAmount - CacheManager.Discount50));
		}
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
					if(((CheckBox)findViewById(R.id.chkClamping)).isChecked() ||
							((CheckBox)findViewById(R.id.chkCompound)).isChecked()) {
						retry = 0;
						ProcessUpdateClamping();

						mProgressDialog = new ProgressDialog(NoticeActivity.this, R.style.AppTheme_Dialog);
						mProgressDialog.setMessage("Loading");
						mProgressDialog.setTitle("");
						mProgressDialog.setCancelable(false);
						mProgressDialog.setIndeterminate(true);
						mProgressDialog.show();
					} else {
						Toast.makeText(NoticeActivity.this, "Sila pilih bayaran", Toast.LENGTH_SHORT).show();
					}
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
					mProgressDialog = new ProgressDialog(NoticeActivity.this, R.style.AppTheme_Dialog);
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
					finish();
				}
			});
		}
		if(type == 3)
		{
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					AlertMessage(NoticeActivity.this, "CETAK", "Cetak Salinan Kedua?", 2);
				}
			});
		}
		builder.show();
	}

	private void ProcessUpdateClamping() {
		String paymentMode = "CASH";
		if(CacheManager.NoticeInfo.IsClamping) {
			CacheManager.NoticeInfo.ClampingPaidAmount = CacheManager.NoticeInfo.ClampingAmount;
			if(((CheckBox)findViewById(R.id.chkClamping)).isChecked()) {
				if (((CheckBox)findViewById(R.id.chkDiscount)).isChecked()) {
					if (((RadioButton)findViewById(R.id.rad20)).isChecked()) {
						CacheManager.NoticeInfo.ClampingPaidAmount = CacheManager.NoticeInfo.ClampingAmount - CacheManager.Discount;
					} else {
						CacheManager.NoticeInfo.ClampingPaidAmount = CacheManager.NoticeInfo.ClampingAmount - CacheManager.Discount50;
					}
				}
			} else {
				CacheManager.NoticeInfo.ClampingPaidAmount = 0;
			}
		} else {
			CacheManager.NoticeInfo.ClampingPaidAmount = 0;
		}

		if (((RadioButton)findViewById(R.id.radTransfer)).isChecked()) {
			paymentMode = "TRANSFER";
		}
		if (((RadioButton)findViewById(R.id.radCard)).isChecked()) {
			paymentMode = "CARD";
		}

		if(((CheckBox)findViewById(R.id.chkCompound)).isChecked()) {
			CacheManager.NoticeInfo.PaidAmount = CacheManager.NoticeInfo.CompoundAmount;
		} else {
			CacheManager.NoticeInfo.PaidAmount = 0;
		}

		Map<String, Object> params = new HashMap<>();
		params.put("NoticeNo", CacheManager.NoticeInfo.NoticeNo);
		params.put("HandheldCode", SettingsHelper.HandheldCode);
		params.put("OfficerID", CacheManager.UserId);
		if(CacheManager.NoticeInfo.ClampingPaidAmount > 0) {
			params.put("ClampingPaidAmount", CacheManager.NoticeInfo.ClampingPaidAmount);
		}
		if(CacheManager.NoticeInfo.PaidAmount > 0) {
			params.put("PaidAmount", CacheManager.NoticeInfo.PaidAmount);
		}
		params.put("PaymentMode", paymentMode);

		String url = CacheManager.ServerURL + "UpdateClamping";
		JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response != null) {
								String statusCode = response.getString("StatusCode");
								String status = response.getString("StatusDescription");
								if(status.isEmpty()) {
									CacheManager.NoticeInfo.ReceiptNumber = statusCode;
									onCheckingSuccess();
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
							ProcessUpdateClamping();
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

	private void onCheckingSuccess() {
		try {
			DbLocal.InsertTransactionHistory(CacheManager.mContext, CacheManager.NoticeInfo);

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onCheckingFailed() {
		Toast.makeText(this, "Bayaran Gagal", Toast.LENGTH_SHORT).show();
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
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

	private void DoPrint()
	{
		try
		{
			if(CheckPrint()) {
				PrintingDocument doc = null;

				try {
					PrinterUtils.OpenConnection();
					if(PrinterUtils.Connection.isConnected()) {
						doc = PrinterUtils.CreateReceipt(CacheManager.NoticeInfo);
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
						doc = PrinterUtils.CreateReceipt(CacheManager.NoticeInfo);
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
}
