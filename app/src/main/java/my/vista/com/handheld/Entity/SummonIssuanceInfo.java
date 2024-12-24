package my.vista.com.handheld.Entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SummonIssuanceInfo
{
	public int NoticeId = 0;
	public String VehicleMakeModel = "";
	public String HandheldCode = "";
	public String OffenceDateString = "";
	public String OfficerId = "";
	public float CompoundAmount = 0;
	public String CompoundExpiryDateString = "";
	public String ImageLocation1;
	public String ImageLocation2;
	public String ImageLocation3;
	public String ImageLocation4;
	public String ImageLocation5;

	public String RoadTaxNo = "-";
	public String SelectedVehicleMake = "";
	public String SelectedVehicleModel = "";
	public String VehicleMake = "";
	public int VehicleMakePos = 0;
	public String VehicleModel = "";
	public int VehicleModelPos = 0;
	public String VehicleType = "";
	public int VehicleTypePos = 0;
	public String VehicleColor = "";
	public int VehicleColorPos = 0;
	public String VehicleNo="";
	public String PetakVehicle="";
	public String OffenceAct="";
	public int OffenceActPos=0;
	public String OffenceActCode="";
	public String OffenceSection="";
	public int OffenceSectionPos=0;
	public String OffenceSectionCode="";
	public String ResultCode="";
	public String Offence="";
	public String OffenceLocation="";
	public int OffenceLocationPos=0;
	public String OffenceLocationArea="";
	public String SummonLocation="";
	public int OffenceLocationAreaPos=0;
	public String OffenceLocationDetails="-";
	public float CompoundAmount1=0;
	public float CompoundAmount2=0;
	public float CompoundAmount3=0;
	public float CompoundAmount4=0;
	public String CompoundAmountDescription="";
	public String CompoundAmountDesc1="";
	public String CompoundAmountDesc2="";
	public String CompoundAmountDesc3="";
	public String CompoundAmountDesc4="";
	public List<String> ImageLocation = new ArrayList<String>();

	public double Latitude=0;
	public double Longitude=0;

	public Date OffenceDateTime = null;
	public String NoticeSerialNo = "";

	public String IsClamping = "";
	public String Notes = "";
	public String OfficerUnit = "";
	
	public SummonIssuanceInfo()
	{
		
	}

	public SummonIssuanceInfo(boolean demo)
	{
		NoticeSerialNo = "KN0022000001";
		RoadTaxNo = "1234567890";
		VehicleMake = "PROTON";
		VehicleModel = "PERSONA";
		VehicleColor = "PUTIH";
		VehicleType = "KERETA";
		VehicleNo = "ABCD1234";
		OffenceAct = "AKTA PENGANGKUTAN JALAN (TLK) 1987";
		OffenceSection = "PERINTAH 12";
		Offence = "MENARIK, MENOLAK,MEMANDU KERETA DENGAN TIDAK TERATUR TANPA MEMBERI PERHATIAN KEPADA KERETA MOTOR LAIN@ORANG YANG BERADA DI TEMPAT LETAK KERETA";
		OffenceLocationArea = "2";
		OffenceLocation = "JALAN HULU KOTA";
		CompoundAmount = 10;
		CompoundAmount1 = 10;
		CompoundAmount2 = 30;
		CompoundAmount3 = 100;
		CompoundAmountDesc1 = "Bayaran Dalam 14 Hari";
		CompoundAmountDesc2 = "Bayaran Selepas 14 Hari";
		CompoundAmountDesc3 = "Bayaran Selepas 28 Hari";
	}
}