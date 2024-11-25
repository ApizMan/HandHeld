package my.vista.com.handheld.UI.Activity;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;
import my.vista.com.handheld.UI.CustomControl.TabsPagerAdapter;
import my.vista.com.handheld.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class NoticeIssuanceActivity extends AppCompatActivity {

	TabLayout tabLayout;
	ViewPager viewPager;

	public static String POSITION = "POSITION";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noticeissuance);

		if(CacheManager.HandheldId.isEmpty()) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finishAffinity();
		} else {
			// Get the ViewPager and set it's PagerAdapter so that it can display items
			tabLayout = (TabLayout) findViewById(R.id.tabs);
			viewPager = (ViewPager) findViewById(R.id.pager);
			TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), getApplicationContext());

			// Give the TabLayout the ViewPager
			viewPager.setAdapter(adapter);
			tabLayout.setupWithViewPager(viewPager);

			viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
			tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
				@Override
				public void onTabSelected(TabLayout.Tab tab) {
					viewPager.setCurrentItem(tab.getPosition());
				}

				@Override
				public void onTabUnselected(TabLayout.Tab tab) {
				}

				@Override
				public void onTabReselected(TabLayout.Tab tab) {

				}
			});

			if (CacheManager.SummonIssuanceInfo == null) {
				CacheManager.SummonIssuanceInfo = new SummonIssuanceInfo();
			}

			if (CacheManager.IsNewNotice) {
				SummonIssuanceInfo temp = CacheManager.SummonIssuanceInfo;
				CacheManager.SummonIssuanceInfo = new SummonIssuanceInfo();
				CacheManager.SummonIssuanceInfo.OffenceActPos = temp.OffenceActPos;
				CacheManager.SummonIssuanceInfo.OffenceSectionPos = temp.OffenceSectionPos;
				CacheManager.SummonIssuanceInfo.OffenceLocationPos = temp.OffenceLocationPos;
				CacheManager.SummonIssuanceInfo.OffenceLocationAreaPos = temp.OffenceLocationAreaPos;
				CacheManager.imageIndex = 0;
				CacheManager.IsNewNotice = false;
				CacheManager.HasChecked = false;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.menu_reprint) {
			Intent i = new Intent(this, HistoryActivity.class);
			startActivity(i);
			return true;
		}

		if (id == R.id.menu_payment) {
			Intent i = new Intent(this, SearchActivity.class);
			startActivity(i);
			return true;
		}

		if (id == R.id.menu_receipt) {
			Intent i = new Intent(this, ReceiptActivity.class);
			startActivity(i);
			return true;
		}

		if (id == R.id.menu_settings) {
			Intent i = new Intent(this, StatusActivity.class);
			startActivity(i);
			return true;
		}

		if (id == R.id.menu_logout) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		return;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
	}
}
