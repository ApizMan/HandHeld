package my.vista.com.handheld.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import my.vista.com.handheld.Business.CacheManager;

public class DbUtils extends SQLiteOpenHelper {
	// The Android's default system path of your application database.
	/** The Db_ path. */
	private static String DB_PATH = "/data/data/my.vista.com.handheld/databases/";
	private static String DB_PATH2 = "/mnt/sdcard/Download/";

	/** The Db_ name. */
	private static String DB_NAME = "vista_handheld.db";

	/** The my database. */
	private SQLiteDatabase myDataBase;

	/** The my context. */
	private final Context myContext;
	
	public DbUtils(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}

	public boolean databaseExist() {
		if (Build.VERSION.SDK_INT >= 4.2) {
			DB_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
		}
		File dbFile = new File(DB_PATH + DB_NAME);		
		return dbFile.exists();
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();

		super.close();
	}

	public void Close() {
		if (myDataBase != null)
			myDataBase.close();

		this.close();
	}

	private void copyDataBase() throws IOException {
		try {
			InputStream myInput;
			Log.e("copyDataBase", "Get Database from package");
			myInput = myContext.getAssets().open(DB_NAME);
			Log.e("copyDataBase", "After Get Database from package");
			if (Build.VERSION.SDK_INT >= 4.2) {
				DB_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
			}

			String outFileName = DB_PATH + DB_NAME;

			OutputStream myOutput = new FileOutputStream(outFileName);

			byte[] buffer = new byte[9216];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			myOutput.flush();
			myOutput.close();
			myInput.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void copyOutDataBase() throws IOException {
		try {
			InputStream myInput;
			if (Build.VERSION.SDK_INT >= 4.2) {
				DB_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
			}
			myInput = new FileInputStream(DB_PATH + DB_NAME);
			String outFileName = DB_PATH2 + DB_NAME;

			OutputStream myOutput = new FileOutputStream(outFileName);

			byte[] buffer = new byte[9216];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			myOutput.flush();
			myOutput.close();
			myInput.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void createDataBase() throws IOException {
		boolean dbExist = databaseExist();
		try {
			if (!dbExist) {
				this.getReadableDatabase();
				this.close();
				copyDataBase();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void Open() {
		try {
			createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Error("Unable to create database");
		}

		try {
			openDataBase();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void openDataBase() throws SQLException {
		if (Build.VERSION.SDK_INT >= 4.2) {
			DB_PATH = myContext.getApplicationInfo().dataDir + "/databases/";
		}
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}

	public Cursor Query(String sqlquery, String[] parameters) {
		Cursor a = null;
		a = myDataBase.rawQuery(sqlquery, parameters);
		return a;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
