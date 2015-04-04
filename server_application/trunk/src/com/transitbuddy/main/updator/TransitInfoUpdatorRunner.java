package com.transitbuddy.main.updator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

import com.transitbuddy.connection.ConnectionManager;
import com.transitbuddy.connection.ConnectionManagerFactory;
import com.transitbuddy.main.updator.Predictions.Direction;
import com.transitbuddy.main.updator.Predictions.Direction.Stop;
import com.transitbuddy.util.TimeUtils;

public class TransitInfoUpdatorRunner extends TimerTask
{
	public static final int RUN_INTERVAL = 15; // seconds
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
	private CompletionService<Predictions> mECS;

	private ArrayList<String> mRouteList = new ArrayList<String>();
	// KV is RouteName and last updated time
	private HashMap<String, Long> mUpdateRouteMap = new HashMap<String, Long>();

	public TransitInfoUpdatorRunner() throws FileNotFoundException,
	    ClassNotFoundException, SQLException, IOException
	{
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
				mECS = new ExecutorCompletionService<Predictions>(mPool);
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
	private static Properties getProperties() throws FileNotFoundException,
	    IOException
	{
		if (sProperties == null)
		{
			sProperties = new Properties();
			sProperties.load(new FileInputStream(PROPERTIES_FILE));
		}
		return sProperties;
	}

	private static void initLogging()
	{
		// load custom XML configuration
		URL url = Loader.getResource("log4j-Updator.xml");
		DOMConfigurator.configure(url);
	}

	/**
	 * @param agency
	 * @return how many tasks were submitted to the executor service
	 */
	private synchronized int processRoutes(String agency)
	{
		int taskCount = 0;
		try
		{
			for (Entry<String, Long> entry : mUpdateRouteMap.entrySet())
			{
				String routeTag = entry.getKey();
				Long timestamp = entry.getValue();

				// Check update timestamp first, if too old remove from map
				if (System.currentTimeMillis() - (60 * 1000) > timestamp)
				{
					mUpdateRouteMap.remove(routeTag);
				}
				else
				{
					// Create PredictionsGatherer on route and submit it to the thread
					// pool
					PredictionsGatherer pg = new PredictionsGatherer(agency, routeTag);

					// Save the Future to the array
					mECS.submit(pg);
					taskCount++;
				}
			}
			return taskCount;
		}
		catch (Exception e)
		{
			LOGGER.error(e);
		}
		return taskCount;
	}

	// route_tags = %W{ 1 4 5 7 8 9 10 11 14 15 16 17 18 19 21 22 23 24 26 27 28
	// 29 30 31 32 33 34 34E 35 36 37 38 39 40 41 42 43 44 45 47 48 50 51 52 55 57
	// 59 60 62 64 65 66 67 68 69 70 70A 71 72 73 74 75 76 77 78 79 80 83 84 85 86
	// 87 88 89 90 91 92 93 94 95 96 97 99 100 101 104 105 106 108 109 110 111
	// 111C 112 114 116 117 119 120 121 131 132 134 136 137 170 171 191 192 193
	// 194 201 202 210 211 212 214 215 216 217 220 221 222 225 225C 230 236 238
	// 240 245 274 275 276 277 325 326 350 351 352 354 355 411 424 424W 426 426W
	// 428 429 430 430G 431 434 435 436 439 441 441W 442 442W 448 449 450 450W 451
	// 455 455W 456 459 465 468 500 501 502 503 504 505 553 554 555 556 558 701
	// 708 741 742 746 747 748 749 751 9109 9111 9501 9507 9701 9702 9703 }
	private ArrayList<String> getListOfRoutes(String agency)
	{
		String url = "http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a="
		    + agency;

		XPathReader reader = new XPathReader(url);
		return reader.readAttributes("/body/route", "tag");
	}

	public synchronized boolean isRealtimeAvailable(String agency,
	    String route_short_name, String headsign)
	{
		if (agency.equals("1"))
			agency = "mbta";
			
		String routeTag;
		if (route_short_name.equals("CT1"))
		{
			routeTag = "701";
		}
		else if (route_short_name.equals("CT2"))
		{
			routeTag = "747";
		}
		else if (route_short_name.equals("CT3"))
		{
			routeTag = "708";
		}
		else
		{
			routeTag = route_short_name;
		}

		if (mRouteList.contains(routeTag))
		{
			Long oldVal = mUpdateRouteMap.put(routeTag, System.currentTimeMillis());

			if (oldVal == null)
			{
				// first time we added it to list, go get fresh data now!
				PredictionsGatherer pg = new PredictionsGatherer(agency, routeTag);
				try
				{
					Predictions p = pg.call();
					if (p != null)
					{
						storePredictionTimes(p);
						// check to see if any times were actually received
						boolean timesAvailable = false;
						for (Direction d : p.mDirections)
						{
							for (Stop s : d.mStops)
							{
								if (s.mPredictions.size() > 0)
									timesAvailable = true;
							}
						}
						
						return timesAvailable;
					}
					else
					{
						return false;
					}
				}
				catch (Exception e)
				{
					LOGGER.error(e.getMessage());
				}
			}
			else
			{
				// we should have some relatively recent data already for this route
				return true;
			}
		}
		else
		{
			return false;
		}
		return false;
	}

	private synchronized void processDoneFutures(int numberOfTasks)
	{
		for (int i = 0; i < numberOfTasks; i++)
		{
			try
			{
				Predictions predictionTimes = mECS.take().get();
				if (predictionTimes != null)
				{
					// Store the values in the database
					storePredictionTimes(predictionTimes);
				}
			}
			catch (Exception e)
			{
				LOGGER.error(e.toString());
			}
		}
	}

	private synchronized void storePredictionTimes(Predictions p)
	{
		if (mTransitSystemDbManager == null)
		{
			LOGGER.error("Transit info database Connection manager was null. "
			    + "Cannot store the prediction information.");
			return;
		}

		// Update the data if it exists else run an insert command
		try
		{
			for (Direction d : p.mDirections)
			{
				for (Stop s : d.mStops)
				{
					int updateResult = mTransitSystemDbManager.updateRealTimeData(
					    p.mAgency, p.mRouteTag, d.mDirectionName, d.mHeadsign, s);
					// Not in database yet, do an insert
					if (updateResult == 0)
					{
						int insertResult = mTransitSystemDbManager.insertRealTimeData(
						    p.mAgency, p.mRouteTag, d.mDirectionName, d.mHeadsign, s);
						if (insertResult == 0)
						{
							LOGGER.error("SQL Insert failed");
						}
					}
				}
			}
		}
		catch (SQLException e)
		{
			LOGGER.error(e.toString());
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

			// Get route lists for each agency, only need to do this once
			String agency = "mbta";
			if (mRouteList.isEmpty())
				mRouteList = getListOfRoutes(agency);

			// Process the pages
			int numberOfTasks = processRoutes(agency);
			processDoneFutures(numberOfTasks);

			TimeUtils.LogTime(LOGGER, "END TRANSIT INFO UPDATE");

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
			initLogging();

			Timer timer = new Timer();
			timer.schedule(new TransitInfoUpdatorRunner(), 0, RUN_INTERVAL * 1000); // X
																																							// seconds
		}
		catch (Exception e)
		{
			LOGGER.error("Got " + e.getClass() + ":" + e.getMessage(), e);
		}
	}

}
