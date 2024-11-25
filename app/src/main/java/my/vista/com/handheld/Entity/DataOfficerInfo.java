package my.vista.com.handheld.Entity;

public class DataOfficerInfo
{
	/**
	 * The Enum ColumnName.
	 */
	public enum ColumnName {
		ID,
		USER_ID,
		NAME,
		PASSWORD
	}

	/** The Code. */
	public String ID;
	public String UserID;
	public String Name;
	public String Password;
	
	public DataOfficerInfo(String id, String userID, String name, String password) {
		this.ID = id;
		this.UserID = userID;
		this.Name = name;
		this.Password = password;
	}
}
