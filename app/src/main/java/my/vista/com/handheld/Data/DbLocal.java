package my.vista.com.handheld.Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import my.vista.com.handheld.Business.CacheManager;
import my.vista.com.handheld.Business.Encryption;
import my.vista.com.handheld.Entity.BaseEntity;
import my.vista.com.handheld.Entity.DataOfficerInfo;
import my.vista.com.handheld.Entity.NoticeInfo;
import my.vista.com.handheld.Entity.SummonIssuanceInfo;

public class DbLocal
{
    public static void InitDatabase(Context context) {
        try {
            DbUtils obj = new DbUtils(context);
            obj.Open();
            obj.close();
        } catch(SQLException e) {
        	e.printStackTrace();
		}
    }

	public static boolean InsertTransactionHistory(Context context, NoticeInfo info)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlCommand = "INSERT INTO TRANSACTION_HISTORY (NOTICE_NUMBER, RECEIPT_NUMBER, " +
					"OFFICER_ID, CLAMPING_AMOUNT, COMPOUND_AMOUNT, UPDATED_DATE) VALUES " +
					"('" + info.NoticeNo + "', '" + info.ReceiptNumber +
					"', '" + info.OfficerId + "', " + info.ClampingPaidAmount + ", " + info.PaidAmount +
					", '" + CacheManager.GetStandardDateString(new Date()) + "')";
			cur = obj.Query(sqlCommand, null);

			if( (cur != null) && cur.moveToFirst() )
			{

			}
		}
		catch(Exception ex)
		{
			return false;
		}
		obj.Close();
		return true;
	}

	public static List<String> GetListForSpinner(Context context, String tableName)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT * FROM " + tableName + " ORDER BY DESCRIPTION";

		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(1));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}

	public static List<String>GetOneFieldListForSpinner(Context context, String fieldName, String tableName)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT " + fieldName + " FROM " + tableName + " ORDER BY " + fieldName;

		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(0));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}

	public static List<String>GetOneFieldListForSpinner(Context context, String fieldName, String tableName, String orderBy)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT " + fieldName + " FROM " + tableName + " ORDER BY " + orderBy;

		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				list.add(cur.getString(0));
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		return list;
	}

	public static List<String>GetListForOffenceLocationSpinner(Context context, String strlocation)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT OL.DESCRIPTION FROM OFFENCE_LOCATION OL, OFFENCE_AREA OA WHERE OA.ID = OL.AREA_ID AND OA.DESCRIPTION = '" + strlocation + "' ORDER BY OL.DESCRIPTION";

		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(0));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}

	public static List<String>GetListForVehicleModelSpinner(Context context, String vehicleMake)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT VMO.DESCRIPTION FROM VEHICLE_MODEL VMO, VEHICLE_MAKE VMA WHERE VMA.ID = VMO.MAKE_ID AND VMA.DESCRIPTION = '" + vehicleMake+ "' ORDER BY VMO.DESCRIPTION";
		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{				
				list.add(cur.getString(0));
            } while (cur.moveToNext());
			cur.close();
        }
		obj.Close();
		return list;
	}

	public static List<String> GetListForOffenceSectionSpinner(Context context, String offenceActShortDesc)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();
		
		String sqlCommand = "SELECT OA.ID, OA.DESCRIPTION, OS.NO_CODE, OS.SUBSECTION_NO FROM OFFENCE_ACT OA, OFFENCE_SECTIONS OS WHERE OA.CODE = OS.OFFENCE_ACT_CODE AND OA.SHORT_DESCRIPTION = '" + offenceActShortDesc + "' ORDER BY OS.NO_CODE";
		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				list.add(cur.getString(2) + " " +cur.getString(3));
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		return list;
	}

	public static ArrayList<BaseEntity> GetListForOffenceSectionSpinnerToObject(Context context, String offenceActShortDesc)
	{
		ArrayList<BaseEntity> list = new ArrayList<BaseEntity>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT OA.ID, OS.CODE, OA.DESCRIPTION, OS.NO_CODE, OS.SUBSECTION_NO FROM OFFENCE_ACT OA, OFFENCE_SECTIONS OS WHERE OA.CODE = OS.OFFENCE_ACT_CODE AND OA.SHORT_DESCRIPTION = '" + offenceActShortDesc + "' ORDER BY OS.NO_CODE";
		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				BaseEntity entity = new BaseEntity();
				entity.Code = cur.getString(1);
				entity.Text = cur.getString(2) + " " + cur.getString(3);
				list.add(entity);
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		return list;
	}

	public static List<String> GetListForOffenceSectionCodeSpinnerNew(Context context, String offenceSectionCode, String offenceActDescription)
	{
		List<String> list = new ArrayList<String>();
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT OS.OFFENCE_ACT_CODE, OS.CODE, OS.DESCRIPTION, OS.NO_CODE, OS.SUBSECTION_NO FROM OFFENCE_SECTIONS OS, OFFENCE_ACT OA WHERE OA.CODE = OS.OFFENCE_ACT_CODE AND OA.SHORT_DESCRIPTION = '" + offenceActDescription + "' AND OS.CODE = '" + offenceSectionCode + "'";
		Cursor cur = obj.Query(sqlCommand, null);

		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				list.add(cur.getString(0));
				list.add(cur.getString(1));
				list.add(cur.getString(2));
				list.add(cur.getString(3));
				list.add(cur.getString(4));
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		return list;
	}

	public static Cursor GetCompoundAmountDescription(Context context, String offenceSectionCode, String offenceActCode) {
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try {
			String sqlCommand =
					"SELECT orm.ZONE1, orm.AMOUNT1, orm.SMALL_AMOUNT1, orm.DESC1, " +
							"orm.ZONE2, orm.AMOUNT2, orm.SMALL_AMOUNT2, orm.DESC2, " +
							"orm.ZONE3, orm.AMOUNT3, orm.SMALL_AMOUNT3, orm.DESC3, " +
							"orm.ZONE4, orm.AMOUNT4, orm.SMALL_AMOUNT4, orm.DESC4, " +
							"orm.MAX_AMOUNT, os.Description AS OffenceDescription " +
							"FROM OFFENCE_RATE_MASTER orm " +
							"JOIN OFFENCE_SECTIONS os ON orm.SECTION_CODE = os.CODE " +
							"WHERE os.CODE = ? AND orm.ACT_CODE = ?";

			String[] selectionArgs = new String[] { offenceSectionCode, offenceActCode };
			cur = obj.Query(sqlCommand, selectionArgs);

			if (cur != null && cur.moveToFirst()) {
				// Process data as needed
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		obj.Close();
		return cur;
	}

	public static Cursor GetActDescription(Context context, String offenceActCode)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlCommand = "SELECT SHORT_DESCRIPTION FROM OFFENCE_ACT WHERE CODE = '" + offenceActCode + "'";
			cur = obj.Query(sqlCommand, null);

			if( (cur != null) && cur.moveToFirst() )
			{
				//cur.close();
			}
		}
		catch(Exception ex)
		{

		}
		obj.Close();
		return cur;
	}

	public static Cursor GetSectionData(Context context, String offenceSectionCode, String offenceActCode)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlCommand = "SELECT NO_CODE, SUBSECTION_NO, DESCRIPTION FROM OFFENCE_SECTIONS WHERE OFFENCE_ACT_CODE = '" + offenceActCode + "' AND CODE = '" + offenceSectionCode + "'";
			cur = obj.Query(sqlCommand, null);

			if( (cur != null) && cur.moveToFirst() )
			{
				//cur.close();
			}
		}
		catch(Exception ex)
		{

		}
		obj.Close();
		return cur;
	}

	public static void CopyOutDatabase(Context context) {
		DbUtils obj = new DbUtils(context);
		try {
			obj.copyOutDataBase();
		}
		catch(Exception ex)
		{
			return;
		} finally {
		}
	}

	public static void ClearLookupTables(Context context)
	{
		DbUtils obj = new DbUtils(context);
		Cursor cur = null;
		obj.Open();
		try
		{
			String sqlCommand = "DELETE FROM OFFENCE_SECTION";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM OFFENCE_ACT";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM OFFENCE_LOCATION";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM OFFENCE_AREA";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM OFFICER_MAINTENANCE";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;

			sqlCommand = "DELETE FROM OFFICER_UNIT";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM VEHICLE_COLOR";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM VEHICLE_TYPE";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM VEHICLE_MODEL";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
			sqlCommand = "DELETE FROM VEHICLE_MAKE";
			cur = obj.Query(sqlCommand, null);
			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
		}
		catch(Exception ex)
		{
			return;
		} finally {
			obj.Close();
		}
	}

	public static boolean InsertOffenceAct(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO OFFENCE_ACT (CODE, DESCRIPTION, SHORT_DESCRIPTION) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("Description") +
							"', '" + info.getString("ShortDescription") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertOffenceRateMaster(Context context, JSONArray list) {
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try {
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					// Prepare SQL values, handle nulls and type conversions
					int actCode = info.getInt("ActCode");
					String sectionCode = info.getString("SectionCode");
					String zone1 = info.optString("Zone1", "NULL");
					String zone2 = info.optString("Zone2", "NULL");
					String zone3 = info.optString("Zone3", "NULL");
					String zone4 = info.optString("Zone4", "NULL");
					String amount1 = info.optString("Amount1", "NULL");
					String amount2 = info.optString("Amount2", "NULL");
					String amount3 = info.optString("Amount3", "NULL");
					String amount4 = info.optString("Amount4", "NULL");
					String smallAmount1 = info.optString("Amount11", "NULL");
					String smallAmount2 = info.optString("Amount21", "NULL");
					String smallAmount3 = info.optString("Amount31", "NULL");
					String smallAmount4 = info.optString("Amount41", "NULL");
					String amountDesc1 = info.optString("AmountDesc1", "");
					String amountDesc2 = info.optString("AmountDesc2", "");
					String amountDesc3 = info.optString("AmountDesc3", "");
					String amountDesc4 = info.optString("AmountDesc4", "");
					String maxCompoundAmount = info.optString("MaxCompoundAmount", "NULL");
					String sNo = info.getString("SNo");

					// Construct the SQL command
					String sqlCommand = "INSERT INTO OFFENCE_RATE_MASTER (ACT_CODE, SECTION_CODE, SECTION_NO, " +
							"ZONE1, ZONE2, ZONE3, ZONE4, AMOUNT1, AMOUNT2, AMOUNT3, AMOUNT4, SMALL_AMOUNT1, SMALL_AMOUNT2, " +
							"SMALL_AMOUNT3, SMALL_AMOUNT4, DESC1, DESC2, DESC3, DESC4, MAX_AMOUNT, RESULT_CODE) VALUES " +
							"('" + actCode + "', '" + sectionCode + "', '" + sNo + "', " +
							zone1 + ", " + zone2 + ", " + zone3 + ", " + zone4 + ", " +
							amount1 + ", " + amount2 + ", " + amount3 + ", " + amount4 + ", " + smallAmount1 + ", " + smallAmount2 + ", " +
							smallAmount3 + ", " + smallAmount4 + ", '" + amountDesc1 + "', '" + amountDesc2 + "', '" + amountDesc3 + "', '" +
							amountDesc4 + "', " + maxCompoundAmount + ", NULL " + ")";

					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {
						// Process result if needed
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (cur != null) {
						cur.close();
						cur = null;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertOffenceLocationArea(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO OFFENCE_AREA (ID, DESCRIPTION) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("Description") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertOffenceLocation(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO OFFENCE_LOCATION (CODE, AREA_ID, DESCRIPTION) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("OffenceLocationAreaCode") + "', '" +
							info.getString("Description") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertOffenceSection(Context context, JSONArray list) {
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try {
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO OFFENCE_SECTIONS (CODE, DESCRIPTION, NO_CODE, SUBSECTION_NO, OFFENCE_ACT_CODE) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("Description") + "', '" +
							info.getString("No") + "', '" + info.optString("SubsectionNo", "") + "', " +
							info.getString("OffenceActCode") + ")";

					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {
						// Process result if needed
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (cur != null) {
						cur.close();
					}
					cur = null;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertOfficerMaintenance(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO OFFICER_MAINTENANCE (ID, USER_ID, NAME, PASSWORD, RANK, RANK_NO) VALUES " +
							"('" + info.getString("ID") + "', '" + info.getString("Code") + "', '" +
					info.getString("Name") + "', '" + info.getString("Password") + "', " + info.getString("Rank") + ", '" + info.getString("RankNo") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertOfficerUnit(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO OFFICER_UNIT (ID, DESCRIPTION) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("Description") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertOfficerRanks(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO OFFICER_UNIT (ID, RANK) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("Rank") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertVehicleColor(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO VEHICLE_COLOR (ID, DESCRIPTION) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("Description") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertVehicleType(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO VEHICLE_TYPE (ID, DESCRIPTION) VALUES " +
							"('" + info.getString("Code") + "', '" + info.getString("Description") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertVehicleMake(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO VEHICLE_MAKE (ID, DESCRIPTION) VALUES " +
							"(" + info.getString("Code") + ", '" + info.getString("Description") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static boolean InsertVehicleModel(Context context, JSONArray list)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			for (int i = 0; i < list.length(); i++) {
				try {
					JSONObject info = list.getJSONObject(i);

					String sqlCommand = "INSERT INTO VEHICLE_MODEL (CODE, MAKE_ID, DESCRIPTION) VALUES " +
							"(" + info.getString("Code") + ", " + info.getString("VehicleMakeCode") + ", '" +
							info.getString("Description") + "')";
					cur = obj.Query(sqlCommand, null);

					if ((cur != null) && cur.moveToFirst()) {

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cur.close();
					cur = null;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		} finally {
			obj.Close();
		}
		return true;
	}

	public static void UpdateSentStatus(Context context, int id)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlCommand = "UPDATE OFFENCE_NOTICE_MAINTENANCE SET SENT_DATE = '" + CacheManager.GetStandardDateString(new Date()) + "' WHERE ID = " + id;
			cur = obj.Query(sqlCommand, null);

			if( (cur != null) && cur.moveToFirst() )
			{
				//cur.close();
			}
			cur.close();
			cur = null;
		}
		catch(Exception ex)
		{
		}
		obj.Close();
	}

	public static void SaveNoticeNumber(Context context, String number)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlCommand = "UPDATE DEVICE_INFO SET NOTICE_NUMBER = '" + number + "'";
			cur = obj.Query(sqlCommand, null);

			if( (cur != null) && cur.moveToFirst() )
			{
			}
			cur.close();
		}
		catch(Exception ex)
		{
		}
		obj.Close();
	}

	public static String GetNextNoticeNumber(Context context)
	{
		String strResult = "";

		try {
			DbUtils obj = new DbUtils(context);
			obj.Open();

			String sqlCommand = "SELECT NOTICE_NUMBER FROM DEVICE_INFO";

			Cursor cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					strResult = cur.getString(0);
				} while (cur.moveToNext());
				cur.close();
			}
			obj.Close();
		}catch (Exception e) {
			Log.e("Error", e.getMessage());
		}

		return strResult;
	}

	public static String GetLastNoticeNumber(Context context, String deviceYear)
	{
		String strResult = "";

		try {
			DbUtils obj = new DbUtils(context);
			obj.Open();

			String sqlCommand = "SELECT SUBSTR(NOTICE_NO, 8) " +
					"FROM OFFENCE_NOTICE_MAINTENANCE " +
					"WHERE NOTICE_NO LIKE '" + deviceYear + "%' " +
					"ORDER BY NOTICE_NO DESC " +
					"LIMIT 1";

			Cursor cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					strResult = cur.getString(0);
				} while (cur.moveToNext());
				cur.close();
			}
			obj.Close();
		}catch (Exception e) {
			Log.e("Error", e.getMessage());
		}

		return strResult;
	}

	public static void CleanOffenceNoticeMaintenance(Context context) {
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlCommand = "DELETE FROM OFFENCE_NOTICE_MAINTENANCE WHERE SENT_DATE <= '" + CacheManager.GetStandardDateString(CacheManager.addDay(new Date(), -7)) + "'";
			cur = obj.Query(sqlCommand, null);

			if( (cur != null) && cur.moveToFirst() )
			{
				//cur.close();
			}
			cur.close();
			cur = null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		obj.Close();
	}

    public static void CleanTransaction(Context context) {
        DbUtils obj = new DbUtils(context);
        obj.Open();
        Cursor cur = null;
        try
        {
            String sqlCommand = "DELETE FROM TRANSACTION_HISTORY WHERE UPDATED_DATE <= '" + CacheManager.GetStandardDateString(CacheManager.addDay(new Date(), -7)) + "'";
            cur = obj.Query(sqlCommand, null);

            if( (cur != null) && cur.moveToFirst() )
            {
                //cur.close();
            }
            cur.close();
            cur = null;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        obj.Close();
    }

	public static boolean IsNoticeExists(Context context, String noticeNo)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String sqlCommand = "SELECT NOTICE_NO FROM OFFENCE_NOTICE_MAINTENANCE WHERE NOTICE_NO = '" + noticeNo + "'";
			cur = obj.Query(sqlCommand, null);

			if((cur != null) && cur.moveToFirst())
			{
				cur.moveToFirst();
				if(cur.getString(0).equalsIgnoreCase(noticeNo)) {
					cur.close();
					obj.Close();
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			return false;
		}
		obj.Close();
		return false;
	}

	public static boolean InsertNotice(Context context, SummonIssuanceInfo info)
	{
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;
		try
		{
			String makeModel = "";
			if(info.VehicleMake.length() != 0)
				makeModel += info.VehicleMake;
			else
				makeModel += info.SelectedVehicleMake;

			if(info.VehicleModel.length() != 0)
				makeModel += " " + info.VehicleModel;
			else
				makeModel += " " + info.SelectedVehicleModel;

			String location = "";
			if(info.OffenceLocation.length() != 0)
				location = info.OffenceLocation;
			else
				location = info.SummonLocation;

			String image1 = "";
			String image2 = "";
			String image3 = "";
			String image4 = "";
			String image5 = "";
			try {
				if (info.ImageLocation.size() >= 1) {
					image1 = info.ImageLocation.get(0);
				}
				if (info.ImageLocation.size() >= 2) {
					image2 = info.ImageLocation.get(1);
				}
				if (info.ImageLocation.size() >= 3) {
					image3 = info.ImageLocation.get(2);
				}
				if (info.ImageLocation.size() >= 4) {
					image4 = info.ImageLocation.get(3);
				}
				if (info.ImageLocation.size() >= 5) {
					image5 = info.ImageLocation.get(4);
				}
			} catch (Exception e) {

			}

			String offenceDateTime = CacheManager.GetStandardDateString(info.OffenceDateTime);

			String sqlCommand = "INSERT INTO OFFENCE_NOTICE_MAINTENANCE (NOTICE_NO, VEHICLE_NO, OFFENCE_DATE, OFFICER_ID, " +
				"HANDHELD_CODE, VEHICLE_TYPE, VEHICLE_MAKE_MODEL, VEHICLE_COLOR, ROAD_TAX_NO, OFFENCE_ACT_CODE, " +
                "OFFENCE_SECTION_CODE, OFFENCE_LOCATION_AREA, OFFENCE_LOCATION, OFFENCE_LOCATION_DETAILS, COMPOUND_AMOUNT, " +
                "IMAGE_NAME1, IMAGE_NAME2, IMAGE_NAME3, IMAGE_NAME4, IMAGE_NAME5, IS_CLAMPING, NOTES, OFFICER_UNIT, LATITUDE, LONGITUDE, VEHICLE_BOX_NO) VALUES " +
				"('" + info.NoticeSerialNo + "', '" + info.VehicleNo + "', '" + offenceDateTime + "', " +
				"'" + CacheManager.UserId + "', '" + SettingsHelper.HandheldCode + "', '" + info.VehicleType + "', " +
				"'" + makeModel + "', '" + info.VehicleColor + "', '" + info.RoadTaxNo + "', '" +
				info.OffenceActCode + "', '" + info.OffenceSectionCode + "', '" + info.OffenceLocationArea + "', '" +
                location + "', '" + info.OffenceLocationDetails + "', " +
				"'" + info.CompoundAmount1 + "', '" + image1 + "', " +
				"'" + image2 + "', '" + image3 + "', '" + image4 + "', '" + image5 + "', '" +
				info.IsClamping + "', '" + info.Notes + "', '" + CacheManager.officerUnit +  "', " + info.Latitude + ", " + info.Longitude + ", '" + info.PetakVehicle + "')";
			cur = obj.Query(sqlCommand, null);

			if( (cur != null) && cur.moveToFirst() )
			{

			}
			cur.close();
			cur = null;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		obj.Close();
		return true;
	}

	public static int GetNumberOfImage(Context context)
	{
		int bResult1 = 0, bResult2 = 0, bResult3 = 0, bResult4 = 0, bResult5 = 0;

		try {
			DbUtils obj = new DbUtils(context);
			obj.Open();

			// Get the start and end of the day
			String startOfDay = CacheManager.GetStandardDateString(getStartOfDay()).substring(0, 8); // Extract "yyyyMMdd"
			String endOfDay = CacheManager.GetStandardDateString(getEndOfDay()).substring(0, 8); // Extract "yyyyMMdd"

			String sqlCommand = "SELECT COUNT(1) AS TOTAL_NOTICES FROM OFFENCE_NOTICE_MAINTENANCE WHERE IMAGE_NAME1 IS NOT NULL AND IMAGE_NAME1 <> '' AND SUBSTR(OFFENCE_DATE, 1, 8) >= '" + startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

			Cursor cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult1 = cur.getInt(0);
				} while (cur.moveToNext());
				cur.close();
			}

			sqlCommand = "SELECT COUNT(1) AS TOTAL_NOTICES FROM OFFENCE_NOTICE_MAINTENANCE WHERE IMAGE_NAME2 IS NOT NULL AND IMAGE_NAME2 <> '' AND SUBSTR(OFFENCE_DATE, 1, 8) >= '" + startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

			cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult2 = cur.getInt(0);
				} while (cur.moveToNext());
				cur.close();
			}

			sqlCommand = "SELECT COUNT(1) AS TOTAL_NOTICES FROM OFFENCE_NOTICE_MAINTENANCE WHERE IMAGE_NAME3 IS NOT NULL AND IMAGE_NAME3 <> '' AND SUBSTR(OFFENCE_DATE, 1, 8) >= '" + startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

			cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult3 = cur.getInt(0);
				} while (cur.moveToNext());
				cur.close();
			}

			sqlCommand = "SELECT COUNT(1) AS TOTAL_NOTICES FROM OFFENCE_NOTICE_MAINTENANCE WHERE IMAGE_NAME4 IS NOT NULL AND IMAGE_NAME4 <> '' AND SUBSTR(OFFENCE_DATE, 1, 8) >= '" + startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

			cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult4 = cur.getInt(0);
				} while (cur.moveToNext());
				cur.close();
			}

			sqlCommand = "SELECT COUNT(1) AS TOTAL_NOTICES FROM OFFENCE_NOTICE_MAINTENANCE WHERE IMAGE_NAME5 IS NOT NULL AND IMAGE_NAME5 <> '' AND SUBSTR(OFFENCE_DATE, 1, 8) >= '" + startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

			cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult5 = cur.getInt(0);
				} while (cur.moveToNext());
				cur.close();
			}

			obj.Close();
		}catch (Exception e) {
			Log.e("Error", e.getMessage());
		}

		return bResult1 + bResult2 + bResult3 + bResult4 + bResult5;
	}

	public static int GetNumberOfSummonsPending(Context context)
	{
		int bResult = 0;

		try {
			DbUtils obj = new DbUtils(context);
			obj.Open();

			String sqlCommand = "SELECT COUNT(1) AS TOTAL_NOTICES FROM OFFENCE_NOTICE_MAINTENANCE WHERE SENT_DATE IS NULL OR SENT_DATE = ''";

			Cursor cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult = cur.getInt(0);
				} while (cur.moveToNext());
				cur.close();
			}
			obj.Close();
		}catch (Exception e) {
			Log.e("Error", e.getMessage());
		}

		return bResult;
	}

	public static ArrayList<SummonIssuanceInfo> GetSummonsPending(Context context)
	{
		ArrayList<SummonIssuanceInfo> list = new ArrayList<SummonIssuanceInfo>();
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;

		String sqlCommand = "SELECT * FROM OFFENCE_NOTICE_MAINTENANCE WHERE SENT_DATE IS NULL OR SENT_DATE = ''";

		cur = obj.Query(sqlCommand, null);
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				SummonIssuanceInfo info = new SummonIssuanceInfo();
				info.NoticeId = cur.getInt(0);
				info.NoticeSerialNo = cur.getString(1);
				info.VehicleNo = cur.getString(2);
				info.OffenceDateString = cur.getString(3);
				info.OfficerId = cur.getString(4);
				info.HandheldCode = cur.getString(5);
				info.VehicleType = cur.getString(6);
				info.VehicleMakeModel = cur.getString(7);
				info.VehicleColor = cur.getString(8);
				info.RoadTaxNo = cur.getString(9);
				info.OffenceActCode = cur.getString(10);
                info.OffenceSectionCode = cur.getString(11);
                info.OffenceLocationArea = cur.getString(12);
				info.OffenceLocation = cur.getString(13);
				info.OffenceLocationDetails = cur.getString(14);
				info.CompoundAmount = cur.getFloat(15);
				info.ImageLocation1 = cur.getString(16);
				info.ImageLocation2 = cur.getString(17);
				info.ImageLocation3 = cur.getString(18);
				info.ImageLocation4 = cur.getString(19);
				info.ImageLocation5 = cur.getString(20);
				info.IsClamping = cur.getString(21);
				info.Notes = cur.getString(22);
				info.OfficerUnit = cur.getString(23);
				info.PetakVehicle = cur.getString(27);

				list.add(info);
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		return list;
	}

	public static int GetNumberOfSummonsIssued(Context context) {
		int bResult = 0;

		try {
			DbUtils obj = new DbUtils(context);
			obj.Open();

			// Get the start and end of the day
			String startOfDay = CacheManager.GetStandardDateString(getStartOfDay()).substring(0, 8); // Extract "yyyyMMdd"
			String endOfDay = CacheManager.GetStandardDateString(getEndOfDay()).substring(0, 8); // Extract "yyyyMMdd"

			// Update the SQL command to filter by today's range
			String sqlCommand = "SELECT COUNT(1) AS TOTAL_NOTICES FROM OFFENCE_NOTICE_MAINTENANCE WHERE SUBSTR(OFFENCE_DATE, 1, 8) >= '"
					+ startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

			Cursor cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult = cur.getInt(0);
				} while (cur.moveToNext());
				cur.close();
			}
			obj.Close();
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
		}

		return bResult;
	}

	private static Date getStartOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	private static Date getEndOfDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}


	public static ArrayList<SummonIssuanceInfo> GetSummonsHistory(Context context)
	{
		ArrayList<SummonIssuanceInfo> list = new ArrayList<SummonIssuanceInfo>();
		DbUtils obj = new DbUtils(context);
		obj.Open();
		Cursor cur = null;

		// Get the start and end of the day
		String startOfDay = CacheManager.GetStandardDateString(getStartOfDay()).substring(0, 8); // Extract "yyyyMMdd"
		String endOfDay = CacheManager.GetStandardDateString(getEndOfDay()).substring(0, 8); // Extract "yyyyMMdd"

		// Query modified to use only the date part of OFFENCE_DATE
		String sqlCommand = "SELECT * FROM OFFENCE_NOTICE_MAINTENANCE " +
				"WHERE SUBSTR(OFFENCE_DATE, 1, 8) >= '" + startOfDay + "' " +
				"AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

		cur = obj.Query(sqlCommand, null);
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				SummonIssuanceInfo info = new SummonIssuanceInfo();
				info.NoticeId = cur.getInt(0);
				info.NoticeSerialNo = cur.getString(1);
				info.VehicleNo = cur.getString(2);
				info.OffenceDateString = cur.getString(3);
				info.OfficerId = cur.getString(4);
				info.HandheldCode = cur.getString(5);
				info.VehicleType = cur.getString(6);
				info.VehicleMakeModel = cur.getString(7);
				info.VehicleColor = cur.getString(8);
				info.RoadTaxNo = cur.getString(9);
				info.OffenceActCode = cur.getString(10);
				info.OffenceSectionCode = cur.getString(11);
				info.OffenceLocationArea = cur.getString(12);
				info.OffenceLocation = cur.getString(13);
				info.OffenceLocationDetails = cur.getString(14);
				info.CompoundAmount = cur.getFloat(15);
				info.ImageLocation1 = cur.getString(16);
				info.ImageLocation2 = cur.getString(17);
				info.ImageLocation3 = cur.getString(18);
				info.ImageLocation4 = cur.getString(19);
				info.ImageLocation5 = cur.getString(20);
				info.IsClamping = cur.getString(21);
				info.Notes = cur.getString(22);
				info.OfficerUnit = cur.getString(23);
				info.PetakVehicle = cur.getString(27);

				list.add(info);
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		return list;
	}

    public static int GetNumberOfTransactions(Context context)
    {
        int bResult = 0;

        try {
            DbUtils obj = new DbUtils(context);
            obj.Open();

			// Get the start and end of the day
			String startOfDay = CacheManager.GetStandardDateString(getStartOfDay()).substring(0, 8); // Extract "yyyyMMdd"
			String endOfDay = CacheManager.GetStandardDateString(getEndOfDay()).substring(0, 8); // Extract "yyyyMMdd"

            String sqlCommand = "SELECT COUNT(1) AS TOTAL_TRANSACTIONS FROM TRANSACTION_HISTORY WHERE SUBSTR(UPDATED_DATE, 1, 8) >= '" +  startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

            Cursor cur = obj.Query(sqlCommand, null);
            if ((cur != null) && cur.moveToFirst()) {
                cur.moveToFirst();
                do {
                    bResult = cur.getInt(0);
                } while (cur.moveToNext());
                cur.close();
            }
            obj.Close();
        }catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        return bResult;
    }



	public static float GetTotalAmountOfTransactions(Context context)
	{
		float bResult = 0;

		try {
			DbUtils obj = new DbUtils(context);
			obj.Open();

			// Get the start and end of the day
			String startOfDay = CacheManager.GetStandardDateString(getStartOfDay()).substring(0, 8); // Extract "yyyyMMdd"
			String endOfDay = CacheManager.GetStandardDateString(getEndOfDay()).substring(0, 8); // Extract "yyyyMMdd"

			String sqlCommand = "SELECT SUM(COMPOUND_AMOUNT) AS TOTAL_COMPOUND, SUM(CLAMPING_AMOUNT) AS TOTAL_CLAMPING FROM TRANSACTION_HISTORY WHERE SUBSTR(UPDATED_DATE, 1, 8) >= '" + startOfDay + "' AND SUBSTR(OFFENCE_DATE, 1, 8) <= '" + endOfDay + "'";

			Cursor cur = obj.Query(sqlCommand, null);
			if ((cur != null) && cur.moveToFirst()) {
				cur.moveToFirst();
				do {
					bResult = cur.getFloat(0);
					bResult += cur.getFloat(1);
				} while (cur.moveToNext());
				cur.close();
			}
			obj.Close();
		}catch (Exception e) {
			Log.e("Error", e.getMessage());
		}

		return bResult;
	}

	public static int DoLogin(String userId, String password, Context context)
	{
		int bResult = 0;
		String encryptedPassword = "";
		ArrayList<DataOfficerInfo> list = new ArrayList<DataOfficerInfo>();
		list.clear();
		
		DbUtils obj = new DbUtils(context);
		obj.Open();

		String sqlCommand = "SELECT ID, NAME, PASSWORD FROM OFFICER_MAINTENANCE WHERE ID = '" + userId + "'";

		Cursor cur = obj.Query(sqlCommand, null);
		if( (cur != null) && cur.moveToFirst() )
		{
			cur.moveToFirst();
			do
			{
				DataOfficerInfo category = new DataOfficerInfo(
						cur.getString(DataOfficerInfo.ColumnName.ID.ordinal()),
						cur.getString(DataOfficerInfo.ColumnName.NAME.ordinal()),
						cur.getString(DataOfficerInfo.ColumnName.PASSWORD.ordinal())
						);

				list.add(category);
			} while (cur.moveToNext());
			cur.close();
		}
		obj.Close();
		if( ( list.size() >= 1 ) && (!list.isEmpty() ) )
		{
			encryptedPassword = list.get(0).Password;

			//if(password.equalsIgnoreCase(encryptedPassword))
			if(Encryption.VerifyHash(password, encryptedPassword, "MD5"))
			{
				CacheManager.officerCode = list.get(0).ID;
				CacheManager.officerName = list.get(0).Name;
				CacheManager.officerDetails = CacheManager.officerName.trim();
				CacheManager.officerUnit = "";
				bResult = 2;
			}
			else
			{
				bResult = 1;
			}
		}
		return bResult;
	}
}