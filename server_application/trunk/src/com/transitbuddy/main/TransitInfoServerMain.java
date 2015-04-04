package com.transitbuddy.main;

import java.net.URL;
import java.util.Timer;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

import com.transitbuddy.main.server.TransitInfoServerRunner;
import com.transitbuddy.main.updator.TransitInfoUpdatorRunner;

public class TransitInfoServerMain
{	
	private static final Logger LOGGER = Logger
  .getLogger(TransitInfoServerMain.class);
	
	private TransitInfoServerMain()
	{
	}
	
	/**
	 * Initializes the logger for this thread using log4j
	 */
	public static void initLogging()
	{
		// load custom XML configuration
		URL url = Loader.getResource("log4j-Main.xml");
		if (url == null)
		{
			System.err.println("Could not load file log4j-Main.xml");
		}
		DOMConfigurator.configure(url);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		initLogging();
		
		// Start the Transit Information Database Updator thread
		TransitInfoUpdatorRunner updatorRunner;
		TransitInfoServerRunner serverRunner;
		
		try
		{
			// Start the updator
			updatorRunner = new TransitInfoUpdatorRunner();
			Timer timer = new Timer();
			timer.schedule(updatorRunner, 0, TransitInfoUpdatorRunner.RUN_INTERVAL * 1000);

			// Start the server that handles client connections
			serverRunner = new TransitInfoServerRunner();
			TransitInfoServerRunner.setUpdator(updatorRunner);
			Thread serverThread = new Thread(serverRunner);
			serverThread.start();
		}
		catch (Exception e)
		{
			LOGGER.error("Got exception in main loop: " + e.getMessage());
		}
	}

}
