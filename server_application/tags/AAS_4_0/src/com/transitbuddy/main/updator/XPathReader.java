package com.transitbuddy.main.updator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathReader
{
	private static final Logger LOGGER = Logger.getLogger(XPathReader.class);
	private URL mUrl;
	private Document mXMLDocument;
	private XPath mXPath;

	public XPathReader(String url)
	{
		try
		{
			mUrl = new URL(url);
			InputStream in = mUrl.openStream();

			mXMLDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			    .parse(in);
			mXPath = XPathFactory.newInstance().newXPath();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (SAXException ex)
		{
			ex.printStackTrace();
		}
		catch (ParserConfigurationException ex)
		{
			ex.printStackTrace();
		}
	}

	public NodeList readNodeList(String expression)
	{
		try
		{
			XPathExpression xPathExpression = mXPath.compile(expression);
			return (NodeList) xPathExpression.evaluate(mXMLDocument,
			    XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String readAttribute(String expression, String attributeName)
	{
		NodeList nodes = readNodeList(expression);

		if (nodes.getLength() > 0)
		{

			NamedNodeMap nnm = nodes.item(0).getAttributes();
			if (nnm != null)
			{
				Node n = nnm.getNamedItem(attributeName);
				if (n != null)
				{
					return n.getNodeValue();
				}
				else
				{
					LOGGER.error("Using expression: " + expression + " -- Cannot find "
					    + attributeName + " attribute\nURL: " + mUrl);
				}
			}
			else
			{
				LOGGER.error("Using expression: " + expression
				    + " -- Cannot find attributes\nURL: " + mUrl);
			}
		}
		else
		{
			LOGGER.error("Using expression: " + expression
			    + " -- No nodes returned\nURL: " + mUrl);
		}
		return null;
	}

	public String readAttribute(Node node, String attributeName)
	{
		if (node != null)
		{
			NamedNodeMap nnm = node.getAttributes();
			if (nnm != null)
			{
				Node n = nnm.getNamedItem(attributeName);
				if (n != null)
				{
					return n.getNodeValue();
				}
				else
				{
					LOGGER.error("Node: " + node + " -- Cannot find " + attributeName
					    + " attribute\nURL: " + mUrl);
				}
			}
			else
			{
				LOGGER.error("Node: " + node + " -- Cannot find attributes\nURL: "
				    + mUrl);
			}
		}
		else
		{
			LOGGER.error("Null node passed in as parameter");
		}
		return null;
	}

	public ArrayList<String> readAttributes(String expression,
	    String attributeName)
	{
		ArrayList<String> rc = new ArrayList<String>();
		NodeList nodes = readNodeList(expression);

		for (int i = 0; i < nodes.getLength(); i++)
		{
			NamedNodeMap nnm = nodes.item(i).getAttributes();
			if (nnm != null)
			{
				Node n = nnm.getNamedItem(attributeName);
				if (n != null)
				{
					rc.add(n.getNodeValue());
				}
				else
				{
					LOGGER.error("Using expression: " + expression + " -- Cannot find "
					    + attributeName + " attribute\nURL:" + mUrl);
				}
			}
			else
			{
				LOGGER.error("Using expression: " + expression
				    + " -- Cannot find attributes\nURL:" + mUrl);
			}
		}
		return rc;
	}
}
