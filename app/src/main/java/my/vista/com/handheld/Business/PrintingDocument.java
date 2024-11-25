package my.vista.com.handheld.Business;

// TODO: Auto-generated Javadoc
/**
 * The Class PrintingDocument.
 */
public class PrintingDocument
{
    /** The offence counter. */
    // private int offenceCounter = 0;

    /** The increment postion y. */
    private int incrementPositionY = 0;

    /** The Printing data. */
    String PrintingData = "";

    /** The Paper length. */
    public String PaperLength = "2000";

    /**
     * Instantiates a new printing document.
     */
    public PrintingDocument()
    {
    }

    /**
     * Instantiates a new printing document.
     *
     * @param paperLength
     *            the paper length
     */
    public PrintingDocument(String paperLength)
    {
        PaperLength = paperLength;
    }

    /**
     * Draw box.
     *
     * @param positionXStart
     *            the position x start
     * @param positionYStart
     *            the position y start
     * @param positionXEnd
     *            the position x end
     * @param positionYEnd
     *            the position y end
     */
    public void DrawBox(int positionXStart, int positionYStart, int positionXEnd, int positionYEnd)
    {
        PrintingData += "BOX " + positionXStart + " " + positionYStart + " " + positionXEnd + " " + positionYEnd + " 1 \r\n";
    }

    public void DrawImageName(int positionXStart, int positionYStart, String imageName) {
        incrementPositionY += positionYStart;

        PrintingData += "^FO" + positionXStart + "," + incrementPositionY;
        PrintingData += "^IME:" + imageName + "^FS" + " \r\n";
    }

    /**
     * Draw line.
     *
     * @param positionXStart
     *            the position x start
     * @param positionYStart
     *            the position y start
     * @param positionXEnd
     *            the position x end
     * @param positionYEnd
     *            the position y end
     */
    public void DrawLine(int positionXStart, int positionYStart, int positionXEnd, int positionYEnd)
    {
        PrintingData += "LINE " + positionXStart + " " + positionYStart + " " + positionXEnd + " " + positionYEnd + " 1 \r\n";
    }

    public void DrawBarcode128(int positionX, int positionY, int height, String data) {
        incrementPositionY += positionY;

        PrintingData += "^FO" + positionX + "," + incrementPositionY + "^BY2";

        PrintingData += "^BCN," + height + ",N,N,N" + "^FD" + data + "^FS" + " \r\n";
    }

    public void DrawBarcode39(int positionX, int positionY, int height, String data) {
        incrementPositionY += positionY;

        PrintingData += "^FO" + positionX + "," + incrementPositionY + "^BY3";

        PrintingData += "^B3N,N," + height + ",Y,N" + "^FD" + data + "^FS" + " \r\n";
    }

    public void DrawQRCode(int positionX, int positionY, int size, String data) {
        incrementPositionY += positionY;

        PrintingData += "^FO" + positionX + "," + incrementPositionY;

        PrintingData += "^BQN,2," + size + "^FD" + data + "^FS" + " \r\n";
    }

    /**
     * Draw text.
     *
     * @param positionX
     *            the position x
     * @param positionY
     *            the position y
     * @param fontSize
     *            the font size
     * @param data
     *            the data
     */
    public void DrawText(int positionX, int positionY, int fontSize, String data)
    {
        incrementPositionY += positionY;

        PrintingData += "^FO" + positionX + "," + incrementPositionY + "^A0,I," + fontSize + "," + fontSize + "^FD" + data + "^FS" + " \r\n";
    }

    public void DrawTextFlow(int positionX, int positionY, int fontSize, int width, String justification, String data)
    {
        incrementPositionY += positionY;

        PrintingData += "^CF0," + fontSize + "," + fontSize + "^FO" + positionX + "," + incrementPositionY +
                "^FB" + width + ",10,1," + justification;
        PrintingData += "^FD" + data + "^FS" + " \r\n";
    }

    /**
     * Draw text in box.
     *
     * @param positionX
     *            the position x
     * @param positionY
     *            the position y
     * @param data
     *            the data
     */
    public void DrawTextInBox(int positionX, int positionY, String data)
    {
        incrementPositionY += positionY;
        for (int i = 0; i < data.length(); i++)
        {
            PrintingData += "T TNR08BO.cpf 0 " + positionX + " " + incrementPositionY + " " + data.substring(i, i + 1) + " \r\n";
            positionX += 40;
        }
    }

    /**
     * Gets the printing data.
     *
     * @return the string
     */
    public String GetPrintingData()
    {
        return PrintingData;
    }
}