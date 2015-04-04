package com.transitbuddy.main.updator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.transitbuddy.TimeUtils;
import com.transitbuddy.connection.ConnectionManager;
import com.transitbuddy.connection.ConnectionManagerFactory;

public class TransitInfoUpdatorRunner implements Runnable
{
	private static final Logger LOGGER = Logger
	    .getLogger(TransitInfoUpdatorRunner.class);
	private static final String PROPERTIES_FILE = "config/transitinfo_updator.properties";
	private static final String DB_TYPE_PROPERTY = "type";
	private static final String TRANSIT_INFO_DB_PREFIX = "db.transitBuddy.";

	/** The maximum number of threads to run transit info parsers with */
	private static final String PARSER_MAX_THREADS_PROPERTY = "parser.max.threads";
	public static Properties sProperties;

	private ConnectionManager mTransitSystemDbManager;

	/** Executes the parser threads */
	private ExecutorService mPool;
	private CompletionService<ArrayList<PredictionTimes>> mECS;	

	public TransitInfoUpdatorRunner() throws FileNotFoundException,
	    ClassNotFoundException, SQLException, IOException
	{
		initLogging();

		Properties props = getProperties();
		if (props != null)
		{
			// Create the TransitSystem DB Manager
			String db_type = props.getProperty(TRANSIT_INFO_DB_PREFIX
			    + DB_TYPE_PROPERTY);
			mTransitSystemDbManager = ConnectionManagerFactory.getConnectionManager(
			    db_type, TRANSIT_INFO_DB_PREFIX, props);

			String maxThreads = props.getProperty(PARSER_MAX_THREADS_PROPERTY);
			int threadsNum = 1;
			try
			{
				threadsNum = Integer.parseInt(maxThreads);
			}
			catch (Exception e)
			{
				LOGGER.error("Got error while parsing property "
				    + PARSER_MAX_THREADS_PROPERTY + " into an integer. "
				    + PARSER_MAX_THREADS_PROPERTY + " = " + maxThreads);
			}
			try
			{
				mPool = Executors.newFixedThreadPool(threadsNum);
				mECS = new ExecutorCompletionService<ArrayList<PredictionTimes>>(mPool);
			}
			catch (IllegalArgumentException e)
			{
				LOGGER.error("Got IllegalArgumentException trying to set the max "
				    + "number of executer threads to " + maxThreads + ". "
				    + e.getMessage());
			}
		}
	}

	/**
	 * Loads the properties file for this class
	 * 
	 * @return The properties file.
	 * @throws FileNotFoundException
	 *           If the properties file does not exist
	 * @throws IOException
	 *           If there is an IO error while reading the file
	 */
	public static Properties getProperties() throws FileNotFoundException,
	    IOException
	{
		if (sProperties == null)
		{
			sProperties = new Properties();
			sProperties.load(new FileInputStream(PROPERTIES_FILE));
		}
		return sProperties;
	}

	public void initLogging()
	{
		// load custom XML configuration
		URL url = Loader.getResource("log4j-Updator.xml");
		DOMConfigurator.configure(url);
	}

	/**
	 * @return how many tasks were submitted to the executor service
	 */	
  public int processTransitSystems()
	{
		int taskCount = 0;
		try
		{
			String agency = "mbta";
			// FIXME cache this routeList at some point
			ArrayList<String> routeList = getListOfRoutes(agency);

			for (String route : routeList)
			{
				PredictionsGatherer pg = new PredictionsGatherer(agency, route);
				
				mECS.submit(pg);
				taskCount++;
				
				// Save the Future to the array
				//Future<ArrayList<PredictionTimes>> future = mPool.submit(pg);
				//mParserFutures.add(future);
			}

			//LOGGER.info("Completed getting real-time information for agency: \'" + agency + "\' Number of routes: " + routeList.size());
			// FIXME add sleep for x number of seconds
			
			return taskCount;
		}
		catch (Exception e)
		{
			LOGGER.error(e);
		}
		return taskCount;
	}

	private ArrayList<String> getListOfRoutes(String agency)
	{
		ArrayList<String> rc = new ArrayList<String>();
		String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a="
		    + agency;
		try
		{
			URL xmlUrl = new URL(url);
			InputStream in = xmlUrl.openStream();

			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(in);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("/body/route");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;

			for (int i = 0; i < nodes.getLength(); i++)
			{
				rc.add(nodes.item(i).getAttributes().getNamedItem("tag").getNodeValue());
			}
		}
		catch (Exception e)
		{
			LOGGER.error(e.toString());
		}

		return rc;
	}

	private void processDoneFutures(int numberOfTasks)
	{
		for (int i = 0; i < numberOfTasks; i++) {
			try
      {
	      ArrayList<PredictionTimes> predictionTimes = mECS.take().get();
	      if (predictionTimes != null)
	      {
					// Store the values in the database
					if (predictionTimes.size() > 0)
					{
						LOGGER.debug("Received " + predictionTimes.size()
						    + " times for agency/route: " + predictionTimes.get(0).agency
						    + "/" + predictionTimes.get(0).route);

						// Store the transit information in the database
						storePredictionTimes(predictionTimes);
					}
	      }
      }
      catch (Exception e)
      {
      	LOGGER.error(e.toString());
      }		
		}		
	}

	private void storePredictionTimes(ArrayList<PredictionTimes> predictionTimes)
	{
		if (mTransitSystemDbManager == null)
		{
			LOGGER.error("Transit info database Connection manager was null. "
			    + "Cannot store the prediction information.");
			return;
		}

		for (int pi = 0; pi < predictionTimes.size(); pi++)
		{
			PredictionTimes record = predictionTimes.get(pi);
			if (record == null)
			{
				LOGGER.error("Transit Info was null. Skipping to "
				    + "the next transit info to store.");
				continue;
			}

			// // Check if the transit info exists in the database
			// try
			// {
			// String numRecordsStr = mTransitSystemDbManager
			// .getTransitSystemExists(record);
			// int numRecords = Integer.parseInt(numRecordsStr);
			// if (numRecords > 0)
			// {
			// // TODO: Update the record in the database
			// try
			// {
			// // Insert the record into the database
			// int insertResult = mTransitSystemDbManager
			// .updateTransitSystem(record);
			// if (insertResult != 1)
			// {
			// LOGGER.error("Could not insert transit info into the database. "
			// + record.toString());
			// }
			// }
			// catch (SQLException e)
			// {
			// LOGGER.error("Got " + e.getClass() + " while trying to insert "
			// + "transit info in the database for artist. " + e.getMessage()
			// + ". " + record.toString());
			// }
			// }
			// else
			// {
			// try
			// {
			// // Insert the record into the database
			// int insertResult = mTransitSystemDbManager
			// .insertTransitSystem(record);
			// if (insertResult != 1)
			// {
			// LOGGER.error("Could not insert transit info into the database. "
			// + record.toString());
			// }
			// }
			// catch (SQLException e)
			// {
			// LOGGER.error("Got " + e.getClass() + " while trying to insert "
			// + "transit info in the database for artist. " + e.getMessage()
			// + ". " + record.toString());
			// }
			// }
			//
			// }
			// catch (SQLException e)
			// {
			// LOGGER.error("Got " + e.getClass() + " while trying to check if "
			// + "the transit info exists in the database. " + e.getMessage()
			// + ". " + record.toString());
			// }
		}
	}

	protected void finalize() throws Throwable
	{
		try
		{
			if (mTransitSystemDbManager != null)
			{
				mTransitSystemDbManager.closeConnection();
			}
		}
		catch (SQLException e)
		{
			LOGGER.error("Got exception trying to close database " + "connection.");
		}
	}

	@Override
	public void run()
	{
		try
		{
			long startTime = System.nanoTime();
			TimeUtils.LogTime(LOGGER, "START TRANSIT INFO UPDATE ");

			// Process the pages
			int numberOfTasks = processTransitSystems();
			processDoneFutures(numberOfTasks);
			
			TimeUtils.LogTime(LOGGER, "END TRANSIT INTO UPDATE");

			long endTime = System.nanoTime();
			long totalTime = endTime - startTime;
			LOGGER.info("Total time = " + (totalTime / 1E6) + " ms");
		}
		catch (Exception e)
		{
			LOGGER.error("Got " + e.getClass() + ":" + e.getMessage(), e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			TransitInfoUpdatorRunner main = new TransitInfoUpdatorRunner();
			main.run();
		}
		catch (Exception e)
		{
			LOGGER.error("Got " + e.getClass() + ":" + e.getMessage(), e);
		}
	}

}
