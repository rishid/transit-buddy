package com.transitbuddy.test;

import java.io.IOException;
import java.net.UnknownHostException;

import com.common.commands.GetNearbyStops;
import com.common.commands.GetRoutes;
import com.common.commands.GetStops;
import com.common.commands.GetStops.ScheduleType;
import com.common.commands.GetTransitSystems;
import com.common.commands.GetTrips;
import com.common.connection.CommandTransporter;
import com.common.types.Coordinate;
import com.common.utilities.ResponseResult;


public class ServerConnectionTest implements Runnable
{
	private static final int NUM_ARGS = 2;
	private static final int INVALID_PORT = -1;
	
	private String mHost = "";
	private int mPort = INVALID_PORT;
	
	/**
	 * Construstor.
	 * @param host The server host name or IP address
	 * @param port The server's port to listen on
	 * @throws NumberFormatException - if the port is not an integer
	 */
	public ServerConnectionTest(String host, String port) 
	throws NumberFormatException
	{
		System.out.println("Host = " + host);
		System.out.println("Port = " + port);
		mHost = host;
		mPort = Integer.valueOf(port);
	}

	/**
	 * Prints the usage of this test to the command line
	 */
	private static void printUsage()
	{
		System.out.println("Usage: java -jar ServerConnectionTest.jar HOST PORT");
		System.out.println("Where HOST = IP address or name of the server to " +
				"connect to.");
		System.out.println("And PORT = port of the server to connect to.");
	}

	@Override
  public void run()
  {
	  try
    {
	    // Make a connection to the server
	    CommandTransporter transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    
	    // Routes test
	    System.out.println("");
	    System.out.println("Test 1 - Routes Test");
	    System.out.println("Sending GetRoutes command.");
	    GetRoutes command = new GetRoutes("1"); // 1 - mbta
	    ResponseResult result = transporter.sendCommand(command);
	    processResult(result);
	    
	    // Trips test
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 2 - Trips Test");
	    System.out.println("Sending GetTrips command.");
	    GetTrips command1 = new GetTrips("1","1");
	    ResponseResult result1 = transporter.sendCommand(command1);
	    processResult(result1);
	    
	    // Stops test
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 3 - Stops Test");
	    System.out.println("Sending GetStops command.");
	    GetStops command2 = new GetStops("1","1","1",ScheduleType.ALL_TIMES);
	    ResponseResult result2 = transporter.sendCommand(command2);
	    processResult(result2);
	    
	    // Nearby Stops Test
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 4 - Nearby Stops Test");
	    System.out.println("Sending GetNearbyStops command.");
	    GetNearbyStops command3 = new GetNearbyStops(
	    		100, new Coordinate(0.0f, 0.0f), 5);
	    ResponseResult result3 = transporter.sendCommand(command3);
	    processResult(result3);
	    
	    // Stops test
	    transporter = new CommandTransporter(mHost, mPort, 
	    		1000000);
	    System.out.println("");
	    System.out.println("Test 5 - Transit Systems");
	    System.out.println("Sending GetTransitSystems command.");
	    GetTransitSystems command4 = new GetTransitSystems();
	    ResponseResult result4 = transporter.sendCommand(command4);
	    processResult(result4);
	    
    }
    catch (UnknownHostException e)
    {
	    System.err.println("Got UnknownHostException: " + e.getMessage());
    }
    catch (IOException e)
    {
	    System.err.println("Got IOException: " + e.getMessage());
    }

    System.out.println("******************************");
    System.out.println("End of Test");
    System.out.println("******************************");
  }
	
	private void processResult(ResponseResult result)
	{
	// Get the response status
    System.out.println("Getting Response");
    ResponseResult.ResponseStatus status = result.getResponseStatus();
    switch (status)
    {
    	case Completed:
    		System.out.println("Got Completed status from server. " + 
    				result.getMessage());
    		break;
    	case TimedOut:
    		System.err.println("Got TimedOut status from server. " + 
    				result.getMessage());
    		break;
    	case Failed:
    		System.err.println("Got Failed status from server. " + 
    				result.getMessage());
    		break;
    };
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Command Line validation
		if (args.length != NUM_ARGS)
		{
			printUsage();
		}
		else
		{
			try
			{
				// Create a ServerConnectionTest object and run it
				ServerConnectionTest test = new ServerConnectionTest(args[0], args[1]);
				Thread testThread = new Thread(test);
				testThread.start();
			}
			catch (Exception e) 
			{
				System.err.println("Got "+ e.getClass() + ":" + e.getMessage());
			}
		}
	}
}
