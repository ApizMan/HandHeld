package my.vista.com.handheld.UI.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.PrinterUtils;
import my.vista.com.handheld.Business.PrintingDocument;
import my.vista.com.handheld.Data.DbLocal;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.Entity.HandheldInfo;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.R;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;

public class StatusActivity extends AppCompatActivity {
	private Runnable doPrint;
	private ProgressDialog mProgressDialog = null;
	HandheldInfo info = new HandheldInfo();

	private EditText txtMACAddress;
	private CheckBox chkNewPrinter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		if(CacheManager.HandheldId.isEmpty()) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finishAffinity();
		} else {
			int nAmountNoticeIssued = 0, nAmountTransactionIssued = 0;
			double dTotalAmount = 0;
			try {
				nAmountNoticeIssued = DbLocal.GetNumberOfSummonsPending(CacheManager.mContext);
			} catch (Exception e) {

			}
			((TextView) findViewById(R.id.tvStatusCountPendingNotice)).setText(String.valueOf(nAmountNoticeIssued));


			nAmountNoticeIssued = 0;
			try {
				nAmountNoticeIssued = DbLocal.GetNumberOfSummonsIssued(CacheManager.mContext);
			} catch (Exception e) {

			}
			((TextView) findViewById(R.id.tvStatusCountNotice)).setText(String.valueOf(nAmountNoticeIssued));
			info.TotalNoticeIssued = String.valueOf(nAmountNoticeIssued);

			nAmountTransactionIssued = 0;
			try {
				nAmountTransactionIssued = DbLocal.GetNumberOfTransactions(CacheManager.mContext);
			} catch (Exception e) {

			}
			((TextView) findViewById(R.id.tvStatusCountTransaction)).setText(String.valueOf(nAmountTransactionIssued));
			info.TotalTransaction = String.valueOf(nAmountTransactionIssued);

			dTotalAmount = 0;
			try {
				dTotalAmount = DbLocal.GetTotalAmountOfTransactions(CacheManager.mContext);
			} catch (Exception e) {

			}
			((TextView) findViewById(R.id.tvStatusAmount)).setText("RM " + String.format("%.2f", dTotalAmount));
			info.TotalAmountCollected = String.format("%.2f", dTotalAmount);

			((TextView) findViewById(R.id.tvStatusUnit)).setText(CacheManager.officerUnit);
			((TextView) findViewById(R.id.tvStatusHandheldID)).setText(SettingsHelper.HandheldCode);
			((TextView) findViewById(R.id.tvStatusLogin)).setText(CacheManager.officerId + " - " + CacheManager.officerDetails);

			info.OfficerZone = CacheManager.officerUnit;
			info.HandheldID = SettingsHelper.HandheldCode;
			info.OfficerDetails = CacheManager.officerId + " - " + CacheManager.officerDetails;

			int nAmountImage = 0;
			try {
				nAmountImage = DbLocal.GetNumberOfImage(CacheManager.mContext);
			} catch (Exception e) {

			}
			((TextView) findViewById(R.id.tvStatusCountImage)).setText(String.valueOf(nAmountImage));
			info.TotalImage = String.valueOf(nAmountImage);

			findViewById(R.id.btnCetak).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if(ValidatePrinter()) {
						doPrint = new Runnable() {
							@Override
							public void run() {
								Looper.prepare();
								DoPrint();
								Looper.loop();
								Looper.myLooper().quit();
							}
						};
						mProgressDialog = new ProgressDialog(StatusActivity.this, R.style.AppTheme_Dialog);
						mProgressDialog.setMessage("Loading");
						mProgressDialog.setTitle("");
						mProgressDialog.setCancelable(false);
						mProgressDialog.setIndeterminate(true);
						mProgressDialog.show();

						Thread thread = new Thread(null, doPrint, "LoginProcess");
						thread.start();
					}
				}
			});

			txtMACAddress = (EditText) findViewById(R.id.etMACAddress);
			txtMACAddress.setEnabled(false);
			txtMACAddress.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {

					// TODO Auto-generated method stub
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

					// TODO Auto-generated method stub
				}

				@Override
				public void afterTextChanged(Editable s) {

					// TODO Auto-generated method stub
					String filtered_str = s.toString();
					if (filtered_str.matches(".*[^A-F^0-9].*")) {
						filtered_str = filtered_str.replaceAll("[^A-F^0-9]", "");
						s.clear();
						s.insert(0, filtered_str);
					}
				}
			});

			txtMACAddress.setText(SettingsHelper.MACAddress);
			chkNewPrinter = (CheckBox) findViewById(R.id.cbPrinter);

			chkNewPrinter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
				{
					if (isChecked)
					{
						txtMACAddress=(EditText)findViewById(R.id.etMACAddress);
						txtMACAddress.setEnabled(true);
						txtMACAddress.setFocusable(true);
					}
					else
					{
						txtMACAddress=(EditText)findViewById(R.id.etMACAddress);
						txtMACAddress.setText(SettingsHelper.MACAddress);
						txtMACAddress.setEnabled(false);
					}
				}
			});

			chkNewPrinter = (CheckBox) findViewById(R.id.cbPrinter);
			/*
			if (txtMACAddress.getText().toString().length() == 0) {
				chkNewPrinter.setChecked(true);
				txtMACAddress = (EditText) findViewById(R.id.etMACAddress);
				txtMACAddress.setEnabled(true);
				txtMACAddress.setFocusable(true);
			}
			 */

			findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					ValidatePrinter();
					finish();
				}
			});
		}
	}

	private boolean ValidatePrinter() {
		if (txtMACAddress.getText().toString().length() == 0) {
			Toast.makeText(StatusActivity.this, "Sila Masukkan MAC Address Printer", Toast.LENGTH_LONG).show();
		} else {
			if (txtMACAddress.getText().toString().length() != 12) {
				Toast.makeText(StatusActivity.this, "Bluetooth string is not a valid bluetooth address", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(StatusActivity.this, "Printer berjaya disimpan", Toast.LENGTH_LONG).show();
				SettingsHelper.SaveMacAddress(CacheManager.mContext, txtMACAddress.getText().toString());
				return true;
			}
		}

		return false;
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
						doc = PrinterUtils.CreateReportPrint(info);
						//doc = PrinterUtils.CreateNotice(new SummonIssuanceInfo(true));
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
			CustomAlertDialog.Show(this, "Printer", "CETAK GAGAL", 0);
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
