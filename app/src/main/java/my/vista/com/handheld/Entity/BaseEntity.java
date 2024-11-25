package my.vista.com.handheld.Entity;

public class BaseEntity
{

	/** The Code. */
	public String Code;

	/** The Text. */
	public String Text;

	/**
	 * Instantiates a new base entity.
	 */
	public BaseEntity()
	{
	}

	/**
	 * Instantiates a new base entity.
	 * 
	 * @param code
	 *            the code
	 * @param text
	 *            the text
	 */
	public BaseEntity(String code, String text)
	{
		this.Code = code;
		this.Text = text;
	}
	

}
