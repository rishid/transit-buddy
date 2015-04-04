package com.transitbuddy.main.updator;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PredictionsGatherer implements
    Callable<ArrayList<PredictionTimes>>
{
	private static final Logger LOGGER = Logger
	    .getLogger(PredictionsGatherer.class);
	private String mAgency;
	private String mRoute;

	public PredictionsGatherer(String agency, String route)
	    throws IllegalArgumentException
	{
		if (agency == null || agency.equals("") || route == null
		    || route.equals(""))
		{
			LOGGER
			    .error("Agency or route was null or blank. Cannot create PredictionsGatherer");
			throw new IllegalArgumentException(
			    "Agency or route was null or blank. Cannot create PredictionsGatherer");
		}
		mAgency = agency;
		mRoute = route;
	}

	private void getListOfStops(String agency, String route,
	    ArrayList<String> stopTags, ArrayList<String> stopIds)
	{
		String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a="
		    + agency + "&r=" + route;
		try
		{
			URL xmlUrl = new URL(url);
			InputStream in = xmlUrl.openStream();

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(in);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("/body/route/stop");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++)
			{
				NamedNodeMap nnm = nodes.item(i).getAttributes();
				if (nnm != null)
				{
					Node n = nodes.item(i).getAttributes().getNamedItem("tag");
					if (n != null)
					{
						stopTags.add(n.getNodeValue());
					}
					else
					{
						LOGGER.debug("Cannot find tag attribute");
					}
					n = nodes.item(i).getAttributes().getNamedItem("stopId");
					if (n != null)
					{
						stopIds.add(n.getNodeValue());
					}
					else
					{
						LOGGER.debug("Cannot find stopId attribute");
					}
				}
				else
				{
					LOGGER.error("Cannot get attributes");
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error(e.toString());
		}
	}

	private ArrayList<PredictionTimes> getPredictions(String agency,
	    String route, ArrayList<String> stopTags, ArrayList<String> stopIds)
	{
		ArrayList<PredictionTimes> rc = new ArrayList<PredictionTimes>();
		// Example url:
		// predictionsForMultiStops&a=mbta&stops=747|null|11803&stops=747|null|6571
		String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictionsForMultiStops&a="
		    + agency;
		StringBuilder sb = new StringBuilder(url);
		for (String stopTag : stopTags)
		{
			sb.append("&stops=" + route + "|null|" + stopTag);
		}

		try
		{
			URL xmlUrl = new URL(sb.toString());
			InputStream in = xmlUrl.openStream();

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(in);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("/body/predictions");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;

			// iterate over Predictions nodes
			for (int i = 0; i < nodes.getLength(); i++)
			{
				PredictionTimes pt = new PredictionTimes();
				pt.stopTag = nodes.item(i).getAttributes().getNamedItem("stopTag")
				    .getNodeValue();

				// HACK: this page doens't give us stopIds which is how we would relate
				// static data to real-time data
				// it only gives stopTags which seem to be internal identifiers to
				// NextBus. They are similar but some
				// tags have '_ar' appended to them, stripping them off now
				// If this does not work, may need to do a relationship between RT and
				// Static data using:
				// agency/route_tag/direction_head_sign/stop_title
				if (pt.stopTag.endsWith("_ar"))
					pt.stopId = pt.stopTag.substring(0, pt.stopTag.length() - 3);
				else
					pt.stopId = pt.stopTag;

				XPath xpathpred = factory.newXPath();
				XPathExpression exprpred = xpathpred
				    .compile("/body/predictions[@stopTag=\"" + pt.stopTag
				        + "\"]/direction/prediction");

				Object resultpred = exprpred.evaluate(doc, XPathConstants.NODESET);
				NodeList nodespred = (NodeList) resultpred;

				// iterate over the prediction nodes
				for (int j = 0; j < nodespred.getLength(); j++)
				{
					Integer tripId;
					Long time;
					Node n;
					n = nodespred.item(j).getAttributes().getNamedItem("epochTime");
					if (n != null)
					{
						time = Long.parseLong(n.getNodeValue());
					}
					else
					{
						LOGGER.error("Null node when getting named item epochTime");
						continue;
					}

					n = nodespred.item(j).getAttributes().getNamedItem("tripTag");
					if (n != null)
					{
						tripId = Integer.parseInt(n.getNodeValue());
					}
					else
					{
						LOGGER.error("Null node when getting named item tripTag");
						continue;
					}

					pt.tripTimes.put(tripId, time);
				}
				pt.agency = agency;
				pt.route = route;
				rc.add(pt);

				// System.out.println(pt);
			}
		}
		catch (Exception e)
		{
			LOGGER.error(e.toString());
		}

		return rc;
	}

	public ArrayList<PredictionTimes> call() throws Exception
	{
		ArrayList<String> stopTags = new ArrayList<String>();
		ArrayList<String> stopIds = new ArrayList<String>();

		LOGGER
		    .debug("Starting gathering on agency/route " + mAgency + "/" + mRoute);

		getListOfStops(mAgency, mRoute, stopTags, stopIds);
		ArrayList<PredictionTimes> times = getPredictions(mAgency, mRoute,
		    stopTags, stopIds);

		return times;
	}

}
