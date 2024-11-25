package my.vista.com.handheld.Entity;

import java.util.Date;

public class NoticeInfo
{
	public String NoticeNo = "";
	public String VehicleNo="";
	public String HandheldCode = "";
	public String OfficerId = "";
	public String OffenceLocation = "";
	public String ReceiptNumber = "";
	public double CompoundAmount = 0;
	public double ClampingAmount = 0;
	public double PaidAmount = 0;
	public double ClampingPaidAmount = 0;
	public Date OffenceDateTime = null;
	public String OffenceDateString = null;

	public boolean IsClamping = false;

    public NoticeInfo()
    {

    }

    public NoticeInfo(boolean demo)
    {
        NoticeNo = "1234567890";
		PaidAmount = 10;
        ClampingPaidAmount = 100;
        ReceiptNumber = "ABC1800001";
    }
}