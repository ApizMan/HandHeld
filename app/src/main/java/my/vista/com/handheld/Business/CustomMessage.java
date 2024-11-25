package my.vista.com.handheld.Business;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CustomMessage {

	/** The Xml document. */
	public Document XmlDocument;

	public CustomMessage(String XmlSource)
	{

		if (XmlSource.equals(""))
		{
			XmlSource = XmlUtility.buildErrorXml("NullSource", "Null Xml Source");
		}

		XmlDocument = XmlUtility.getDocFromXmlString(XmlUtility.clearXmlDoc(XmlSource));

	}

	/**
	 * Gets the elements.
	 * 
	 * @param nodeName
	 *            the node name
	 * @return the elements
	 */
	public NodeList getElements(String nodeName)
	{
		if (XmlDocument == null)
		{
			return null;
		}

		if (XmlDocument.getElementsByTagName(nodeName).getLength() > 0)
		{
			return XmlDocument.getElementsByTagName(nodeName);
		} else
		{
			return null;
		}
	}

	/**
	 * Gets the node.
	 * 
	 * @param nodeName
	 *            the node name
	 * @return the node
	 */
	public Node GetNode(String nodeName)
	{
		if (XmlDocument == null)
		{
			return null;
		}

		if (XmlDocument.getElementsByTagName(nodeName).getLength() > 0)
		{
			return XmlDocument.getElementsByTagName(nodeName).item(0);
		} else
		{
			return null;
		}
	}

	/**
	 * Removes the node.
	 * 
	 * @param node
	 *            the node
	 */
	public void RemoveNode(Node node)
	{

		Node parentNode = node.getParentNode();

		parentNode.removeChild(node);
	}

	/**
	 * Sets the value for node.
	 * 
	 * @param nodeName
	 *            the node name
	 * @param nodevalue
	 *            the nodevalue
	 */
	public void SetValueForNode(String nodeName, String nodevalue)
	{
		Node NodeSelect = XmlDocument.getElementsByTagName(nodeName).item(0);

		if (NodeSelect != null)
		{

			NodeSelect.setTextContent(nodevalue);
		}

	}

	/**
	 * To xml string.
	 * 
	 * @return the string
	 */
	public String ToXmlString()
	{
		try
		{
			return XmlUtility.getXmlStringFromDoc(XmlDocument);
		} catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			return e.toString();
		}
	}

}
