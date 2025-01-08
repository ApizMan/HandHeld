package my.vista.com.handheld.UI.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.PrinterUtils;
import my.vista.com.handheld.Business.PrintingDocument;
import my.vista.com.handheld.Business.TrustAllCertificates;
import my.vista.com.handheld.Business.VolleySingleton;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;
import my.vista.com.handheld.UI.CustomControl.HistoryAdapter;

public class HistoryActivity extends AppCompatActivity {
	private Runnable doPrint;
	private ProgressDialog mProgressDialog = null;

	int retry = 0;
	SummonIssuanceInfo selected = new SummonIssuanceInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		if(CacheManager.HandheldId.isEmpty()) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finishAffinity();
		} else {
			ListView listView = (ListView) findViewById(R.id.lvHistory);
			TextView tvInfo = (TextView) findViewById(R.id.tvInfo);

			ArrayList<SummonIssuanceInfo> list = DbLocal.GetSummonsHistory(CacheManager.mContext);

			if(!list.isEmpty()) {
				listView.setVisibility(View.VISIBLE);
				tvInfo.setVisibility(View.GONE);

				HistoryAdapter adapter = new HistoryAdapter(this.getApplicationContext(), R.layout.history_item, list);
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
						selected = (SummonIssuanceInfo) parent.getAdapter().getItem(position);
						selected = CacheManager.updateData(selected);
						AlertPrint(selected);
					}
				});
			} else {
				listView.setVisibility(View.GONE);
				tvInfo.setVisibility(View.VISIBLE);
			}
		}
	}

	private void AlertPrint(my.vista.com.handheld.Entity.SummonIssuanceInfo info) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog);
		builder.setTitle("Salinan Pendua");
		builder.setMessage("Cetak salinan notis?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				doPrint = new Runnable() {
					@Override
					public void run()
					{
						Looper.prepare();
						getQRPegeypay(info);
						Looper.loop();
						Looper.myLooper().quit();
					}
				};
				mProgressDialog = new ProgressDialog(HistoryActivity.this, R.style.AppTheme_Dialog);
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
			}
		});
		builder.show();
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

	private void getQRPegeypay(my.vista.com.handheld.Entity.SummonIssuanceInfo info) {
		String url = CacheManager.qrPegeypay;

		Map<String, Object> params = new HashMap<>();
		params.put("order_output", "online");
		params.put("order_no", info.NoticeSerialNo);
		params.put("override_existing_unprocessed_order_no", "Yes");
		params.put("order_amount", String.format("%.2f", info.CompoundAmount1));
		params.put("qr_validity", 43200);
		params.put("store_id", "Kompund");
		params.put("terminal_id", "Phone");
		params.put("shift_id", "SHIFT 1");
		params.put("language", "en_us");

		TrustAllCertificates.trustAllHosts();

		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response != null) {
								String status = response.getString("status");
								if ("success".equals(status)) {
									JSONObject content = response.getJSONObject("content");
//                                    CacheManager.SummonIssuanceInfo.QRLink = content.getString("iframe_url");
									CacheManager.saveQR(content.getString("iframe_url"));
									DoPrint(info);
								} else {
									Toast.makeText(HistoryActivity.this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(HistoryActivity.this, "Generate QR Failed", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
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
							refreshToken(info);
						} else {
							error.printStackTrace();
							mProgressDialog.dismiss();
						}
					}
				}) {
			@Override
			public Map<String, String> getHeaders() {
				Map<String, String> headers = new HashMap<String, String>();
//                                headers.put("Accept", "application/json"); // Set the content type
				headers.put("Authorization", "Bearer " + CacheManager.token); // Add the Bearer token
				return headers;
			}
		};

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		VolleySingleton.getInstance(CacheManager.mContext).addToRequestQueue(request);
	}

	private void refreshToken(my.vista.com.handheld.Entity.SummonIssuanceInfo info) {
		String url = CacheManager.refreshPegeypay;

		TrustAllCertificates.trustAllHosts();

		JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(),
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							if (response != null) {
								String accessToken = response.getString("access_token");
								CacheManager.saveToken(accessToken);
								getQRPegeypay(info);
							} else {
								Toast.makeText(HistoryActivity.this, "Access Token Failed", Toast.LENGTH_SHORT).show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						mProgressDialog.dismiss();
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
						} else {
							error.printStackTrace();
							mProgressDialog.dismiss();
						}
					}
				}) {
		};

		request.setRetryPolicy(new DefaultRetryPolicy(
				0,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

		VolleySingleton.getInstance(HistoryActivity.this).addToRequestQueue(request);
	}

	private void DoPrint(my.vista.com.handheld.Entity.SummonIssuanceInfo info)
	{
		try
		{
			if(CheckPrint()) {
				PrintingDocument doc = null;

				try {
					PrinterUtils.OpenConnection();
					if(PrinterUtils.Connection.isConnected()) {
						doc = PrinterUtils.CreateNotice(info);
						PrinterUtils.Print(doc);
					} else {
						CustomAlertDialog.Show(this, "ERROR", "FAILED TO CONNECT", 3);
					}
				} catch(Exception e) {
					CustomAlertDialog.Show(this, "ERROR", "FAILED TO PRINT", 3);
				} finally {
					PrinterUtils.CloseConnection();
				}
			}
		}
		catch(Exception ex)
		{
			CustomAlertDialog.Show(this, "Printer", "CETAK SAMAN GAGAL", 0);
		}

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
}
