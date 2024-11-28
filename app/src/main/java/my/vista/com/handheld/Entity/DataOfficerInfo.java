package my.vista.com.handheld.Entity;

public class DataOfficerInfo
{
	/**
	 * The Enum ColumnName.
	 */
	public enum ColumnName {
		ID,
		NAME,
		PASSWORD
	}

	/** The Code. */
	public String ID;
	public String Name;
	public String Password;
	
	public DataOfficerInfo(String id, String name, String password) {
		this.ID = id;
		this.Name = name;
		this.Password = password;
	}
}
