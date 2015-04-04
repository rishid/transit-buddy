package com.transitbuddy.main.updator;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.transitbuddy.main.updator.RouteConfig.Direction;

public class PredictionsGatherer implements Callable<Predictions>
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

	private RouteConfig getRouteConfig(String agency, String route)
	{
		RouteConfig rc = new RouteConfig();

		String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a="
		    + agency + "&r=" + route;

		XPathReader reader = new XPathReader(url);

		String tag = reader.readAttribute("/body/route", "tag");
		if (tag != null)
		{
			rc.mAgency = agency;
			rc.mRouteTag = tag;
		}
		else
		{
			LOGGER.error("Failing getting route config for agency/route: " + agency
			    + "/" + route);
			return null;
		}

		ArrayList<String> stopTags;
		stopTags = reader.readAttributes("/body/route/stop", "tag");
		ArrayList<String> stopTitles;
		stopTitles = reader.readAttributes("/body/route/stop", "title");

		if (stopTags.size() == stopTitles.size())
		{
			for (int i = 0; i < stopTags.size(); i++)
			{
				rc.mStops.put(stopTags.get(i), stopTitles.get(i));
			}
		}
		else
		{
			LOGGER
			    .fatal("Stop tags and Stop titles ArrayLists are not the same size");
		}

		NodeList nodes = reader.readNodeList("/body/route/direction");
		for (int i = 0; i < nodes.getLength(); i++)
		{
			RouteConfig.Direction direction = rc.new Direction();
			direction.mHeadsign = reader.readAttribute(nodes.item(i), "title");
			direction.mDirectionName = reader.readAttribute(nodes.item(i), "name");

			stopTags = reader.readAttributes("/body/route/direction/stop", "tag");

			for (int j = 0; j < stopTags.size(); j++)
			{
				direction.mStops.put(stopTags.get(j), rc.mStops.get(stopTags.get(j)));
			}

			rc.mDirections.add(direction);
		}

		return rc;
	}

	private Predictions getPredictions(RouteConfig routeConfig)
	{
		Predictions rc = new Predictions();
		// Example url:
		// predictionsForMultiStops&a=mbta&stops=747|null|11803&stops=747|null|6571
		String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=predictionsForMultiStops&a="
		    + routeConfig.mAgency;
		StringBuilder sb = new StringBuilder(url);
		// nextbus only allow 150 per request, artificially limit here for now and
		// if time persists come up with a solution to combine later
		int counter = 0;
		for (String stopTag : routeConfig.mStops.keySet())
		{
			sb.append("&stops=" + routeConfig.mRouteTag + "|null|" + stopTag);
			if (++counter > 150)
				break;
		}
		XPathReader reader = new XPathReader(sb.toString());

		rc.mAgency = routeConfig.mAgency;
		rc.mRouteTag = routeConfig.mRouteTag;

		for (Direction direction : routeConfig.mDirections)
		{
			Predictions.Direction d = rc.new Direction();
			d.mHeadsign = direction.mHeadsign;
			d.mDirectionName = direction.mDirectionName;

			for (Entry<String, String> entry : direction.mStops.entrySet())
			{
				Predictions.Direction.Stop s = d.new Stop();
				String stopTag = entry.getKey();
				String stopTitle = entry.getValue();
				s.mStopTag = stopTag;
				s.mStopTitle = stopTitle;
				NodeList nodes = reader.readNodeList("/body/predictions[@stopTitle=\""
				    + stopTitle + "\"]/direction");
				// LOGGER.error("tag/title/headsign " + stopTag + "/" + stopTitle + "/"
				// + d.mHeadsign);
				for (int i = 0; i < nodes.getLength(); i++)
				{
					Node curNode = nodes.item(i);
					if (reader.readAttribute(curNode, "title")
					    .equals(direction.mHeadsign))
					{
						NodeList predNodes = curNode.getChildNodes();

						int predNodeCounter = 0;

						for (int j = 0; j < predNodes.getLength(); j++)
						{
							if (predNodes.item(j).getNodeName().equals("prediction"))
							{
								if (++predNodeCounter > 5)
								{
									LOGGER.error("More than 5 predictions nodes! Stop title: "
									    + stopTitle + " URL: " + sb.toString());
									continue;
								}
								Predictions.Direction.Stop.Prediction p = s.new Prediction();
								String epochTime = reader.readAttribute(predNodes.item(j),
								    "epochTime");
								String vehicle = reader.readAttribute(predNodes.item(j),
								    "vehicle");
								p.mArrivalTime = epochTime;
								p.mVehicle = vehicle;
								s.mPredictions.add(p);
							}
						}
						break;
					}
				}
				d.mStops.add(s);
			}
			rc.mDirections.add(d);
		}
		// LOGGER.debug(rc);
		return rc;
	}

	public Predictions call() throws Exception
	{
		LOGGER.debug("Starting gathering prediction times for agency/route "
		    + mAgency + "/" + mRoute);

		RouteConfig rc = getRouteConfig(mAgency, mRoute);

		if (rc != null)
		{
			Predictions p = getPredictions(rc);
			LOGGER.debug("Finished gathering prediction times for agency/route "
			    + mAgency + "/" + mRoute);
			return p;
		}
		else
		{
			return null;
		}
	}

}
