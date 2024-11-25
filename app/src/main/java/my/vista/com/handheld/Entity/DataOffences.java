package my.vista.com.handheld.Entity;

public class DataOffences
{
	/**
	 * The Enum ColumnName.
	 */
	public enum ColumnName {
		OFFENCE_ACT_CODE,
		OFFENCE_SECTION_CODE,
		DESCRIPTION,
		NO,
		SUBSECTION_NO		
	}

	/** The Code. */
	public int OffenceActCode;
	public int OffenceSectionCode;
	public String Description;
	public String No;
	public String SubsectionNo;
	
	

	
	public DataOffences(int actCode, int sectionCode, String description, String no, String subsection_no)
	{
		this.OffenceActCode = actCode;
		this.OffenceSectionCode = sectionCode;
		this.Description = description;
		this.No = no;
		this.SubsectionNo=subsection_no;
		// TODO Auto-generated constructor stub
	}

}
