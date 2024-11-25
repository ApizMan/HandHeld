package my.vista.com.handheld.Entity;

public class HandheldInfo
{
	public String OfficerZone = "";
	public String HandheldID = "";
	public String OfficerDetails = "";
	public String TotalNoticeIssued = "";
	public String TotalImage = "";
	public String TotalTransaction = "";
	public String TotalAmountCollected = "";
	
	public HandheldInfo()
	{}

	public HandheldInfo(boolean demo)
	{
		OfficerZone = "DATA";
		HandheldID = "DATA";
		OfficerDetails = "DATA";
		TotalNoticeIssued = "DATA";
		TotalImage = "DATA";
	}
}