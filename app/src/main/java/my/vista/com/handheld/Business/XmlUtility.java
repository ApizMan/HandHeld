package my.vista.com.handheld.Business;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtility {

	/**
	 * Builds the error xml.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorDescription
	 *            the error description
	 * @return the string
	 */
	public static String buildErrorXml(String errorCode, String errorDescription)
	{
		StringBuilder obj = new StringBuilder();
		obj.append("<ErrorMessage>");
		obj.append(String.format("<%1$s>%2$s</%1$s>", "ErrorCode", "0000"));
		obj.append("</ErrorMessage>");

		return obj.toString();
	}

	/**
	 * Clear xml doc.
	 * 
	 * @param xmlSource
	 *            the xml source
	 * @return the string
	 */
	public static String clearXmlDoc(String xmlSource)
	{
		return xmlSource.replace("\r", "").replace("\n", "").replace("\t", "").trim();
	}
	
	/**
	 * Element to string.
	 * 
	 * @param n
	 *            the n
	 * @return the string
	 */
	public static String elementToString(Node n)
	{

		String name = n.getNodeName();

		short type = n.getNodeType();

		if (Node.CDATA_SECTION_NODE == type)
		{
			return "<![CDATA[" + n.getNodeValue() + "]]&gt;";
		}

		if (name.startsWith("#"))
		{
			return "";
		}

		StringBuffer sb = new StringBuffer();

		sb.append('<').append(name);

		NamedNodeMap attrs = n.getAttributes();
		if (attrs != null)
		{
			int length = attrs.getLength();
			for (int i = 0; i < length; i++)
			{
				Node attr = attrs.item(i);
				sb.append(' ').append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append("\"");
			}
		}

		String textContent = null;
		NodeList children = n.getChildNodes();

		if (children.getLength() == 0)
		{
			if ((textContent = n.getTextContent()) != null && !"".equals(textContent))
			{
				sb.append(textContent).append("</").append(name).append('>');
				;
			} else
			{
				sb.append("/>").append('\n');
			}
		} else
		{

			sb.append('>').append('\n');
			boolean hasValidChildren = false;
			int childlength = children.getLength();
			for (int i = 0; i < childlength; i++)
			{
				String childToString = elementToString(children.item(i));
				if (!"".equals(childToString))
				{
					sb.append(childToString);
					hasValidChildren = true;
				}
			}

			if (!hasValidChildren && ((textContent = n.getTextContent()) != null))
			{
				sb.append(textContent);
			}

			sb.append("</").append(name).append('>');
		}

		return sb.toString();
	}

	/**
	 * Gets the doc from xml string.
	 * 
	 * @param xmlString
	 *            the xml string
	 * @return the doc from xml string
	 */
	public static Document getDocFromXmlString(String xmlString)
	{
		Document doc = null;
		try
		{
			InputSource is = new InputSource(new StringReader(xmlString));

			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse(is);
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			CacheManager.ErrorLog(e);
		} catch (SAXException e)
		{
			// TODO Auto-generated catch block
			CacheManager.ErrorLog(e);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			CacheManager.ErrorLog(e);
		}

		return doc;
	}
	
	/*
	 * Load XML template file from resource into String
	 */
	/**
	 * Gets the xml file.
	 * 
	 * @param context
	 *            the context
	 * @param filename
	 *            the filename
	 * @return the xml file
	 */
	public static String getXmlFile(String filename)
	{

		String s = "";
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream(new File(filename));
			s = readTextFile(inputStream);

		} catch (IOException e)
		{
			CacheManager.ErrorLog(e);
			s = e.toString();
		}

		return s;
	}
	
	/**
	 * Gets the xml string from doc.
	 * 
	 * @param xmlDocument
	 *            the xml document
	 * @return the xml string from doc
	 * @throws TransformerException
	 *             the transformer exception
	 */
	public static String getXmlStringFromDoc(Document xmlDocument) throws TransformerException
	{
		// set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "true");
		// create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(xmlDocument);
		trans.transform(source, result);
		String xmlString = sw.toString();

		return xmlString;
	}
	
	/* This class */
	/**
	 * Read text file.
	 * 
	 * @param inputStream
	 *            the input stream
	 * @return the string
	 */
	private static String readTextFile(InputStream inputStream)
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte buf[] = new byte[1024];
		int len;

		try
		{
			while ((len = inputStream.read(buf)) != -1)
			{
				outputStream.write(buf, 0, len);
			}

			outputStream.close();
			inputStream.close();
		} catch (IOException e)
		{
			CacheManager.ErrorLog(e);
		}

		return outputStream.toString();
	}
}
