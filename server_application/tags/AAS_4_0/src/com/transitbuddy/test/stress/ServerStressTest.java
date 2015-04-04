package com.transitbuddy.test.stress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

import com.transitbuddy.test.stress.resmonitor.ResourceMonitor;
import com.transitbuddy.util.TimeUtils;

/**
 * This is a stress test for the TransitBuddy server application. The test runs
 * for an hour. During the test, simultaneous remote connections are made from
 * the test application to the TransitInformationServer application to mimic
 * connections made from multiple TransitBuddy apps.
 * 
 * For each connection, the test application sends one of the five Commands to
 * the TransitInformationServer ( i.e. GetTransitSystems, GetRoutes, GetTrips,
 * GetStops, or GetNearbyStops). This tests that all Commands are supported by
 * the server.
 * 
 * The simultaneous connections push the server application to its fullest
 * potential and elevate the server application’s CPU/disk consumption to peak
 * levels. Statistics are gathered by the test application which are processed
 * and recorded to a log file at the end of the test. These statistics include
 * the following: - TransitBuddy request response time -
 * TransitInformationServer CPU consumption - TransitInformationServer downtime
 * - TransitInformationServer disk consumption
 * 
 * To test REQ-114, the test application logs all downtime that occurs within an
 * hour and then multiplies that value by 24 to make a guess as how much
 * downtime will occur during a day. 
 * - REQ-102: The TransitInformationServer SHALL accept incoming network connection requests from the TransitBuddy application. 
 * - REQ-106: The TransitInformationServer SHALL provide an interface to find the closest stop to the user’s current position. 
 * - REQ-111: The TransitInformationServer SHALL respond to TransitBuddy application requests within 5 seconds. 
 * - REQ-112: The TransitInformationServer SHALL not consume 100% of CPU time for more than 1 minute. 
 * - REQ-113: The TransitInformationServer SHALL not consume all available disk space. 
 * - REQ-114: The TransitInformationServer SHALL have less than 30 seconds downtime per day. 
 * - REQ-115: The TransitInformationServer SHALL support simultaneous connections.
 */
public class ServerStressTest
{
	private static final Logger LOGGER = Logger.getLogger(ServerStressTest.class);
	private static final String PROPERTIES_FILE = "config/stress_test.properties";

	/** The properties for the test */
	public static Properties sProperties;

	/** The property file key for the number of milliseconds to run for */
	private static final String RUN_TIME_MILLIS_PROPERTY = "run.time.millis";

	/** The number of seconds in a day */
	private static final int MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;
	
	/**
	 * The property file key for the number of seconds to wait since the last
	 * check before checking the PID's stats again
	 */
	private static final String MONITOR_INTERVAL_SECONDS_PROPERTY = 
		"monitor.interval.seconds";

	/** The number of milliseconds to run the test for */
	private int mRunTimeMillis = 0;

	/** The process ID to be monitored */
	private static int sProcessId;

	/**
	 * The number of seconds to wait since the last check before checking the
	 * PID's stats again
	 */
	private int mMonitorIntervalSeconds;

	ScheduledExecutorService mExecutor;
	ResourceMonitor mResourceMonitor;

	/**
	 * Constructor
	 * 
	 * @throws FileNotFoundException
	 *           - if the properties file does not exist
	 * @throws IOException
	 *           - if there is an I/O error while reading the properties file
	 * @throws NumberFormatException
	 *           - if there is an error trying to convert the run time or monitor
	 *           interval time from a string to an Integer
	 */
	public ServerStressTest() throws FileNotFoundException, IOException,
	    NumberFormatException
	{
		initLogging();
		sProperties = getProperties();
		String runTimeStr = sProperties.getProperty(RUN_TIME_MILLIS_PROPERTY);
		mRunTimeMillis = Integer.parseInt(runTimeStr);
		String intervalStr = sProperties
		    .getProperty(MONITOR_INTERVAL_SECONDS_PROPERTY);
		mMonitorIntervalSeconds = Integer.parseInt(intervalStr);
	}

	/**
	 * @return the number of milliseconds to run the test for
	 */
	public int getRunTimeMillis()
	{
		return mRunTimeMillis;
	}

	/**
	 * Creates the out directory for the logging files and initializes all logging
	 * for the entire application using log4j file log4j-StressTest.xml
	 * 
	 * @throws FileNotFoundException
	 *           - if it cannot create the out folder
	 */
	public void initLogging() throws FileNotFoundException
	{
		// Create the "out" directory if it doesn't exist
		File file = new File("out");
		if (!file.exists())
		{
			file.mkdirs();
		}
		if (file.exists())
		{
			// load custom XML configuration
			URL url = Loader.getResource("log4j-StressTest.xml");
			DOMConfigurator.configure(url);
		}
		else
		{
			throw new FileNotFoundException("Could not initiate the logging. "
			    + "Ensure the out directory exists.");
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

	private void logTestResults(ClientRunner clientTest)
	{
		LOGGER.info("");
		LOGGER.info("**********************");
		LOGGER.info("**     RESULTS     **");
		LOGGER.info("**********************");
		LOGGER.info("");

		clientTest.logTestResults();

		// REQ-112
		String str = "Result: ";
		LOGGER.info("");
		LOGGER.info("Requirement:  REQ-112: The TransitInformationServer SHALL"
		    + " not consume 100% of CPU time for more than 1 minute.");
		double maxCpuUsage = mResourceMonitor.getMaxCPUUsage();
		if (maxCpuUsage < 100.0)
		{
			LOGGER.info(str + "PASS");
		}
		else
		{
			LOGGER.info(str + "FAIL");
		}

		// REQ-114
		LOGGER.info("");
		LOGGER.info("Requirement:  REQ-114: The TransitInformationServer SHALL"
		    + " have less than 30 seconds downtime per day.");
		double maxDownTime = mResourceMonitor.getMaxDowntime();
		double num = MILLISECONDS_IN_A_DAY / mRunTimeMillis;
		double downTimeInADay = num * maxDownTime;
		
		if (maxDownTime < 30000)
		{
			LOGGER.info(str + "PASS");
		}
		else
		{
			LOGGER.info(str + "FAIL");
		}
		LOGGER.info("Down time in a day: " + downTimeInADay
				+ ", Down time during test: " + maxDownTime);
		
		// REQ-113
		LOGGER.info("");
		LOGGER.info("Requirement:  REQ-113: The TransitInformationServer SHALL" +
				" not consume all available disk space..");
		double maxMemUsage = mResourceMonitor.getMaxMemoryUsage();
		
		if (maxMemUsage < 100)
		{
			LOGGER.info(str + "PASS");
		}
		else
		{
			LOGGER.info(str + "FAIL");
		}
		LOGGER.info("Max mem usage: " + maxMemUsage);

		System.out.println(" ");
		System.out.println("Maximum CPU usage: "
		    + mResourceMonitor.getMaxCPUUsage() + "%");
		System.out.println("Maximum memory usage: "
		    + mResourceMonitor.getMaxMemoryUsage() + "%");
		System.out.println("Maximum virtual memory size: "
		    + mResourceMonitor.getMaxVirtualMemory() + " bytes");
		System.out.println("Maximum resident set size: "
		    + mResourceMonitor.getMaxResidentSetMemory() + " bytes");
		System.out.println("Maximum downtime: " + mResourceMonitor.getMaxDowntime()
		    + " seconds");
	}

	public void createAndRunMontior()
	{
		int duration = (mRunTimeMillis / 1000);
		System.out.println("Preparing to monitor process " + sProcessId + " every "
		    + mMonitorIntervalSeconds + " seconds for " + duration + " seconds...");

		// schedule a resource monitor at a fixed rate
		mExecutor = Executors.newSingleThreadScheduledExecutor();
		mResourceMonitor = new ResourceMonitor(sProcessId, mMonitorIntervalSeconds,
		    duration);
		mExecutor.scheduleAtFixedRate(mResourceMonitor, 0, mMonitorIntervalSeconds,
		    TimeUnit.SECONDS);

	}

	public void stopMonitor()
	{
		if (mExecutor != null)
		{
			mExecutor.shutdown();
		}
	}

	/**
	 * Main method. Creates and runs the stress test.
	 * 
	 * @param args
	 *          None
	 */
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Invalid arguments");
			System.out.println("Usage: java -jar ServerStressTest.java PID");
			System.out.println("where the PID is the process ID to be monitored,");
			System.exit(-1);
		}

		sProcessId = Integer.parseInt(args[0]);

		// Start the main Stress test thread
		try
		{
			// Initialize the main class
			ServerStressTest main = new ServerStressTest();

			// Create and run the client runner to submit commands to the server
			ClientRunner clientTest = new ClientRunner();
			Thread clientThread = new Thread(clientTest);
			clientThread.start();

			main.createAndRunMontior();

			// Sleep for the duration of the test
			int sleepMillis = main.getRunTimeMillis();

			long startTime = System.nanoTime();
			TimeUtils.LogTime(LOGGER, "START SERVER STRESS TEST ");

			LOGGER.info("Sleeping for " + sleepMillis + " milliseconds.");
			Thread.sleep(sleepMillis);

			// Stop the test threads
			if (clientThread.isAlive())
			{
				clientThread.interrupt();
			}

			// Stop the monitor
			main.stopMonitor();

			TimeUtils.LogTime(LOGGER, "END SERVER STRESS TEST");

			long endTime = System.nanoTime();
			long totalTime = endTime - startTime;
			LOGGER.info("Total time = " + (totalTime / 1E6) + " ms");

			// Write the test results to the log file
			main.logTestResults(clientTest);
		}
		catch (Exception e)
		{
			LOGGER.error("Got " + e.getClass() + ":" + e.getMessage(), e);
		}

		// End the test
		System.exit(0); // success
	}
}
