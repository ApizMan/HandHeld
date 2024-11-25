package my.vista.com.handheld.UI.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.UI.CustomControl.CustomAlertDialog;
import my.vista.com.handheld.Data.SettingsHelper;
import my.vista.com.handheld.R;

public class ImageActivity extends AppCompatActivity {
	private static final int TAKE_PICTURE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_image);

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		System.gc();

		if(CacheManager.HandheldId.isEmpty()) {
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finishAffinity();
		} else {
			RefreshData();
			Button btn_continue = (Button) findViewById(R.id.btn_ok);
			btn_continue.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					finish();
				}
			});

			Button btn_capture = (Button) findViewById(R.id.btn_capture);
			btn_capture.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					File dir = new File("/mnt/sdcard/CustomImageDir");
					if (!dir.exists()) {
						dir.mkdirs();
					}

					if (CheckMaximumPicture()) {
						String delegate = "yy";
						String year = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());

						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						File photo = new File(dir, SettingsHelper.HandheldCode + year + SettingsHelper.getNoticeSerialNumber(CacheManager.mContext) + "Pic" + CacheManager.imageIndex + ".jpg");
						try {
							photo.createNewFile();
							photo.setReadable(true);
							photo.setWritable(true);
						} catch (Exception e) {
							e.printStackTrace();
							Log.e("MESSAGE", e.getMessage());
						}
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(photo));
						CacheManager.ImageUri = Uri.fromFile(photo);
						startActivityForResult(intent, TAKE_PICTURE);
					}
				}
			});

			Button btn_delete = (Button) findViewById(R.id.btn_delete);
			btn_delete.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Spinner spinner_image = (Spinner) findViewById(R.id.spinner_images);
					if (spinner_image.getSelectedItemPosition() > 0) {
						try {
							File dir = new File("/mnt/sdcard/CustomImageDir");
							if (!dir.exists()) {
								dir.mkdirs();
							}

							File file = new File(dir, spinner_image.getSelectedItem().toString());
							if (file.exists()) {
								if (file.getAbsoluteFile().delete()) {
									if (!file.exists()) {
										CacheManager.SummonIssuanceInfo.ImageLocation.remove(spinner_image.getSelectedItem().toString());
										RefreshData();
									}
								}
							}
						} catch (Exception e) {

						}
					}
				}
			});
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.gc();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}

	//decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(String path){
		try {
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(path),null,o);

			//The new size we want to scale to
			final int REQUIRED_SIZE=300;

			//Find the correct scale value. It should be the power of 2.
			int scale=1;
			while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
				scale*=2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}

	private void RefreshData()
	{
		List<String> list = new ArrayList<String>();
		for(String item : CacheManager.SummonIssuanceInfo.ImageLocation) {
			list.add(item);
		}

		Spinner spinner_image = (Spinner) findViewById(R.id.spinner_images);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.insert("--Sila Pilih--", 0);
		spinner_image.setAdapter(adapter);
		spinner_image.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// TODO Auto-generated method stub
				ImageView img = (ImageView) findViewById(R.id.img_view);
				if (pos > 0) {
					File dir = new File("/mnt/sdcard/CustomImageDir");
					if (!dir.exists()) {
						dir.mkdirs();
					}

					File file = new File(dir, parent.getSelectedItem().toString());
					if (file.exists() && file.length() > 0) {
						BitmapFactory.Options options;
						Bitmap bitmap = null;
						try {
							options = new BitmapFactory.Options();
							bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
						} catch (Exception ex) {
						}

						img.setImageBitmap(bitmap);
						System.gc();
					} else {
						img.setImageBitmap(null);
					}
				} else
					img.setImageBitmap(null);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	public static Bitmap rotateImage(Bitmap source, float angle) {
		Bitmap retVal;

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

		return retVal;
	}

	private boolean CheckMaximumPicture()
	{
		try {
			if (CacheManager.SummonIssuanceInfo.ImageLocation.size() >= 5) {
				CustomAlertDialog.Show(this, "GAMBAR", "Hanya 5 gambar dibenarkan. Sila padam yang tidak berkenan.", 0);
				return false;
			}
		}
		catch (Exception ex) {
			return false;
		}
		return true;
	}

	private Runnable doSaveImage;
	private ProgressDialog m_ProgressDialog = null;

	private void DoSaveImage(Uri imageUri)
	{
		System.gc();

		m_ProgressDialog.dismiss();

		File image = new File(imageUri.getPath());

		int max = 0;
		if(!image.exists() || image.length() <= 0) {
			while (max <= 8000) {
				try {
					max += 100;
					Thread.sleep(100);
					image = new File(imageUri.getPath());
					if (image.exists() && image.length() > 0) {
						max += 8000;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if(image.exists() && image.length() > 0) {
			try {
				Bitmap bitmap = null;
				try {
					bitmap = decodeFile(image.getPath());

					try {
						ExifInterface ei = new ExifInterface(image.getPath());
						int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

						switch (orientation) {
							case ExifInterface.ORIENTATION_ROTATE_90:
								bitmap = rotateImage(bitmap, 90);
								break;
							case ExifInterface.ORIENTATION_ROTATE_180:
								bitmap = rotateImage(bitmap, 180);
								break;
							case ExifInterface.ORIENTATION_ROTATE_270:
								bitmap = rotateImage(bitmap, 270);
								break;
							case ExifInterface.ORIENTATION_NORMAL:
							default:
								break;
						}
					} catch(Exception e) {
						e.printStackTrace();
						Log.e("MESSAGE", e.getMessage());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.e("MESSAGE", ex.getMessage());
				}

				File newImage = new File(image.getPath());
				try {
					FileOutputStream output = new FileOutputStream(newImage);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 50, output);
					output.close();
					bitmap.recycle();
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("MESSAGE", e.getMessage());
				}

				this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						RefreshData();
					}
				});

				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("MESSAGE", e.getMessage());
			}
		} else {
			String test = "No Image Found";
			Log.e("MESSAGE", test);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case TAKE_PICTURE:
				if (resultCode == Activity.RESULT_OK) {
					doSaveImage = new Runnable() {
						@Override
						public void run()
						{
							Looper.prepare();

							String delegate = "yy";
							String year = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());

							CacheManager.SummonIssuanceInfo.ImageLocation.add(SettingsHelper.HandheldCode +  year + SettingsHelper.getNoticeSerialNumber(CacheManager.mContext) + "Pic" + CacheManager.imageIndex + ".jpg");
							CacheManager.imageIndex++;
							DoSaveImage(CacheManager.ImageUri);
							Looper.loop();
							Looper.myLooper().quit();
						}
					};
					m_ProgressDialog = new ProgressDialog(ImageActivity.this, R.style.AppTheme_Dialog);
					m_ProgressDialog.setMessage("Saving");
					m_ProgressDialog.setTitle("");
					m_ProgressDialog.setCancelable(false);
					m_ProgressDialog.setIndeterminate(true);
					m_ProgressDialog.show();

					Thread thread = new Thread(null, doSaveImage, "LoginProcess");
					thread.start();
				}
		}
	}
}
