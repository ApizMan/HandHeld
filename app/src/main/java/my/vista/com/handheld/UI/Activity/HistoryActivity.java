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

import java.util.ArrayList;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.PrinterUtils;
import my.vista.com.handheld.Business.PrintingDocument;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;
import my.vista.com.handheld.UI.CustomControl.HistoryAdapter;

public class HistoryActivity extends AppCompatActivity {
	private Runnable doPrint;
	private ProgressDialog mProgressDialog = null;
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
						AlertPrint();
					}
				});
			} else {
				listView.setVisibility(View.GONE);
				tvInfo.setVisibility(View.VISIBLE);
			}
		}
	}

	private void AlertPrint() {
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
						DoPrint();
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

	private void DoPrint()
	{
		try
		{
			if(CheckPrint()) {
				PrintingDocument doc = null;

				try {
					PrinterUtils.OpenConnection();
					if(PrinterUtils.Connection.isConnected()) {
						doc = PrinterUtils.CreateNotice(selected);
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
