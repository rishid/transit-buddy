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

import com.transitbuddy.main.updator.TransitInfoUpdatorRunner;

public class TransitInfoServerRunner implements Runnable
{
	/** The logger */
	private static final Logger LOGGER = 
		Logger.getLogger(TransitInfoServerRunner.class);
	
	/** The default port number the server listens on/write to */
	private static final int DEFAULT_PORT = 1099;
	/** The name and path of the properties file for this server */
	private static final String PROPERTIES_FILE = 
		"config/transitSystem_server.properties";
	/** The database TCP port */
	private static final String SERVER_PORT_STRING = "server.port";
	private static TransitInfoUpdatorRunner sUpdator = null;

	/** The Server properties */
	public static Properties sProperties;
	/** The port number the server listens on/writes to */
	private int mPort;
	/** The server socket the server listens to/writes to */
	private ServerSocket mServerSocket;

	/**
	 * Constructor
	 * @throws FileNotFoundException If the properties file does not exist
	 * @throws IOException If there is an IO error while reading the file
	 */
	public TransitInfoServerRunner() throws FileNotFoundException, IOException
	{
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

	/**
	 * Initializes the logger for this thread using log4j
	 */
	public static void initLogging()
	{
		// load custom XML configuration
		URL url = Loader.getResource("log4j-Server.xml");
		if (url == null)
		{
			System.err.println("Could not load file log4j-Server.xml");
		}
		DOMConfigurator.configure(url);
	}

	/**
   * @return the sUpdator
   */
  public static TransitInfoUpdatorRunner getsUpdator()
  {
  	return sUpdator;
  }

	/**
	 * @param mUpdator the mUpdator to set
	 */
	public static void setUpdator(TransitInfoUpdatorRunner updator)
	{
		sUpdator = updator;
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
	 * Main entry point for the server execution
	 * @param args None
	 */
	public static void main(String[] args)
	{		
		// Start the Transit Information Server thread
		try
		{
			initLogging();
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
