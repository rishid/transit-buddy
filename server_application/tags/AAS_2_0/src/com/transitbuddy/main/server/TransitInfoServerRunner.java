package com.transitbuddy.main.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

public class TransitInfoServerRunner implements Runnable
{
	private static final Logger LOGGER = 
		Logger.getLogger(TransitInfoServerRunner.class);
	private static final int DEFAULT_PORT = 1099;
	private static final String PROPERTIES_FILE = 
		"config/transitSystem_server.properties";
	/** The database TCP port */
	private static final String SERVER_PORT_STRING = "server.port";

	public static Properties sProperties;
	private int mPort;
	private ServerSocket mServerSocket;

	public TransitInfoServerRunner() throws FileNotFoundException, IOException
	{
		initLogging();

		Properties props = getProperties();
		if (props != null)
		{
			// Get the port
			String port = sProperties.get(SERVER_PORT_STRING).toString();
			try
			{
				mPort = Integer.parseInt(port);
			}
			catch (NumberFormatException e)
			{
				LOGGER.error("Could not parse server port from " + port +
						". Using default port: " + DEFAULT_PORT + ".");
				mPort = DEFAULT_PORT;
			}
		}
	}

	/**
	 * Loads the properties file for this class
	 * @return The properties file. 
	 * @throws FileNotFoundException If the properties file does not exist
	 * @throws IOException If there is an IO error while reading the file
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

	public void finalize()
	{
		if (mServerSocket != null)
		{
			try
			{
				mServerSocket.close();
			}
			catch (IOException e)
			{
				LOGGER.error("Could not close server socket error. IOExceptoin: " +
						e.getMessage());
			}
		}
	}

	public void initLogging()
	{
		// load custom XML configuration
		URL url = Loader.getResource("log4j-Server.xml");
		if (url == null)
		{
			System.err.println("Could not load file log4j-Server.xml");
		}
		DOMConfigurator.configure(url);
	}

	@Override
	public void run()
	{
		boolean listening = true;

		try 
		{
			mServerSocket = new ServerSocket(mPort);
			while (listening)
			{
				TransitInfoServer server = 
					new TransitInfoServer(mServerSocket.accept());
				new Thread(server).start();
			}
		} 
		catch (IOException e) 
		{
			LOGGER.error("Could not listen on port: " + mPort + ".");
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{		
		// Start the Transit Information Server thread
		try
		{
			TransitInfoServerRunner serverRunner = new TransitInfoServerRunner();
			Thread serverThread = new Thread(serverRunner);
			serverThread.start();
		}
		catch (Exception e) 
		{
			LOGGER.error("Got "+ e.getClass() + ":" + e.getMessage(), e);
		}
	}
}
