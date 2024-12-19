package my.vista.com.handheld.UI.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.VolleySingleton;
import my.vista.com.handheld.Entity.NoticeInfo;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;

public class SearchActivity extends AppCompatActivity {
	private ProgressDialog mProgressDialog = null;
	int retry = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		if(CacheManager.HandheldId.isEmpty()) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finishAffinity();
		} else {
			findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
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

		String url = CacheManager.ServerURL + "SearchNotice";
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
			CacheManager.NoticeInfo.ClampingAmount = info.getDouble("ClampingAmount");
			CacheManager.NoticeInfo.CompoundAmount = info.getDouble("CompoundAmount");
			CacheManager.NoticeInfo.IsClamping = info.getBoolean("IsClamping");

			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmaa");
				CacheManager.NoticeInfo.OffenceDateTime = format.parse(CacheManager.NoticeInfo.OffenceDateString);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Intent i = new Intent(this, NoticeActivity.class);
			startActivity(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onCheckingFailed() {
		Toast.makeText(this, "Semakan Gagal", Toast.LENGTH_SHORT).show();
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
