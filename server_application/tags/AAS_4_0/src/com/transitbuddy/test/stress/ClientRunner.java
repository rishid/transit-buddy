package com.transitbuddy.test.stress;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.common.commands.CommandCode;
import com.common.utilities.ResponseResult.ResponseStatus;

public class ClientRunner implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(ClientRunner.class);
	
	/** This is the number of valid command codes */
	private static final int NUM_COMMAND_CODES = CommandCode.getSize();
	
	/** The property file key for the maximum number of threads to run tests 
	 * with */
	private static final String MAX_THREADS_PROPERTY = 
		"max.simultaneous.threads";
	/** The property file key for the server host machine name */
	private static final String SERVER_HOST_PROPERTY = "server.host";
	/** The property file key for the server host machine name */
	private static final String SERVER_PORT_PROPERTY = "server.port";
	
	/** The server's host name */
	private String mHost;
	/** The server's port */
	private int mPort;
	
	/** The max number of threads to run tests with*/
	private int mThreadsNum = 1;
	
	/** Executes the parser threads */
	private ExecutorService mPool;

	/** The pool of ExecutorCompletionService to run the tests with */
	private ExecutorCompletionService<CommandResponseResult> mECS;
	
	/** A map of the test results for each command code */
	private HashMap<CommandCode, TestResult> mCommandTestResults = 
		new HashMap<CommandCode, TestResult>();
	
	private boolean mSimultaneousConnectionsTestResult = false;
	private ResponseStatus mLastTestStatus = ResponseStatus.Failed;
	
	private boolean mOneConnectionTestResult = false;
	private boolean mNearbyStopsTestResult = false;
	
	public ClientRunner() throws FileNotFoundException, IOException
	{
		Properties props = ServerStressTest.getProperties();
		if (props != null)
		{
			// Get the host and port of the server machine
			mHost = props.getProperty(SERVER_HOST_PROPERTY);

			String portStr = props.getProperty(SERVER_PORT_PROPERTY);
			mPort = Integer.parseInt(portStr);
	
			// Get the max stress test threads
			String maxThreads = props.getProperty(MAX_THREADS_PROPERTY);
			try
			{
				mThreadsNum = Integer.parseInt(maxThreads);
			}
			catch (Exception e)
			{
				LOGGER.error("Got error while parsing property "
				    + MAX_THREADS_PROPERTY + " into an integer. "
				    + MAX_THREADS_PROPERTY + " = " + maxThreads);
			}
			
		}
		try
		{
			mPool = Executors.newFixedThreadPool(mThreadsNum);
			mECS = new ExecutorCompletionService<CommandResponseResult>(mPool);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.error("Got IllegalArgumentException trying to set the max "
			    + "number of executer threads to " + mThreadsNum + ". "
			    + e.getMessage());
		}
	}
	
	public void logTestResults()
	{
		String str = "Result: ";
		
		// REQ-102
		LOGGER.info("Requirement:  REQ-102: The TransitInformationServer SHALL" +
				" accept incoming network connection requests from the TransitBuddy" +
				" application.");
		if (mOneConnectionTestResult)
		{
			LOGGER.info( str + "PASS");
		}
		else
		{
			LOGGER.info(str + "FAIL");
		}
		
		// REQ-106
		LOGGER.info("");
		LOGGER.info("Requirement:  REQ-106: The TransitInformationServer SHALL" +
				" provide an interface to find the closest stop to the user’s " +
				"current position.");
		if (mNearbyStopsTestResult)
		{
			LOGGER.info( str + "PASS");
		}
		else
		{
			LOGGER.info(str + "FAIL");
		}
		
		// REQ-115
		LOGGER.info("");
		LOGGER.info("Requirement:  REQ-115: The TransitInformationServer SHALL" +
				" support simultaneous connections.");
		if (mSimultaneousConnectionsTestResult)
		{
			LOGGER.info(str + "PASS");
		}
		else
		{
			LOGGER.info(str + "FAIL");
		}

		// REQ-111
		LOGGER.info("");
		LOGGER.info("Requirement: REQ-111: The TransitInformationServer SHALL" +
				" respond to TransitBuddy application requests within 5 seconds.");
		
		// Loop through the results and tally the ones that timed out
		int totalTests = 0;
		int totalTimedOut = 0;
		for (CommandCode code : mCommandTestResults.keySet())
		{
			TestResult testResult = mCommandTestResults.get(code);
			int completed = testResult.getNumResultsOfType(ResponseStatus.Completed);
			int failed = testResult.getNumResultsOfType(ResponseStatus.Failed);
			int timedOut = testResult.getNumResultsOfType(ResponseStatus.TimedOut);
			totalTests += completed + failed + timedOut;
			totalTimedOut += timedOut;
			LOGGER.debug("Command Code: " + code.name());
			LOGGER.debug("Completed: " + completed + ", Failed: " + failed
					+ ", Timed out: " + timedOut);
		}
		if (totalTimedOut == 0)
		{
			LOGGER.info(str + "PASS");
			LOGGER.info("Total Tests = " + totalTests);
		}
		else
		{
			LOGGER.info(str + "FAIL");
			LOGGER.info("Timed Out Tests = " + totalTimedOut);
			LOGGER.info("Total Tests = " + totalTests);
		}
	}

	/**
	 * Submits the given number of tests to the ECS to be run
	 * @param numTests The number of tests to be submitted to be run
	 */
	public void submitTests(int numTests)
	{
		try
		{
			for (int i = 0; i < numTests; i++)
			{
				TestRunner pg = new TestRunner(mHost, mPort, getRandomCommandCode());

				// Save the Future to the array
				mECS.submit(pg);
			}
		}
		catch (Exception e)
		{
			LOGGER.error(e);
		}
	}
	
	/**
	 * Checks the status of the given number of tests. If the test are done, it 
	 * takes the done tests off the ECS queue and processes the results
	 * @param numTests The total number of tests in the ECS queue
	 * @return The number of completed tests
	 */
	private int processDoneTests(int numTests)
	{
		int numOfDoneTasks = 0;
		for (int i = 0; i < numTests; i++)
		{
			try
			{
				// Try to get a result from a test that is done running
				Future<CommandResponseResult> future = mECS.take();
				if (future.isDone())
				{
					CommandResponseResult result = future.get();
					if (result != null)
					{
						// Store the result status in the list
						storeResponseResult(result);
					}

					// If a nearby stops command returned Completed, mark this test as true
					if (!mNearbyStopsTestResult &&
							result.getCommandCode().equals(CommandCode.GET_NEARBY_STOPS) &&
							result.getResponseResult().getResponseStatus().
							equals(ResponseStatus.Completed))
					{
						mNearbyStopsTestResult = true;
					}

					// If not tested already, test if the server accepted a connection
					if (!mOneConnectionTestResult && 
							result.getResponseResult().getResponseStatus().
							equals(ResponseStatus.Completed))
					{
						mOneConnectionTestResult = true;
					}
					
					// If not tested already, check if there are simultaneous connections
					// to the server
					if (!mSimultaneousConnectionsTestResult &&
							mLastTestStatus.equals(ResponseStatus.Completed) &&
							result.getResponseResult().getResponseStatus().
							equals(ResponseStatus.Completed))
					{
						mSimultaneousConnectionsTestResult = true;
					}
					
					// Increment the number of completed tasks
					numOfDoneTasks++;
					
					mLastTestStatus = result.getResponseResult().getResponseStatus();
				}
			}
			catch (Exception e)
			{
				LOGGER.error(e.toString());
			}
		}
		
		return numOfDoneTasks;
	}
	
	/**
	 * Stores the given test result in the map of results per command code
	 * @param result The test result to be stored
	 */
	private void storeResponseResult(CommandResponseResult result)
	{
		if (result != null)
		{
			// Get the current results for the new result's command code
			CommandCode newResultCommandCode = result.getCommandCode();

			if (mCommandTestResults.containsKey(newResultCommandCode))
			{
					TestResult testResult = mCommandTestResults.get(newResultCommandCode);
					testResult.addResult(result.getResponseResult().getResponseStatus());
					mCommandTestResults.put(newResultCommandCode, testResult);
			}
			else
			{
				// Add an entry to the command test results map if the command code
				// isn't found
				TestResult testResult = new TestResult();
				testResult.addResult(result.getResponseResult().getResponseStatus());
				mCommandTestResults.put(newResultCommandCode, testResult);
			}
		}
		else
		{
			LOGGER.debug("Got NULL command response result.");
		}
	}
	
	/**
	 * @return a random valid CommandCode
	 */
	public static CommandCode getRandomCommandCode()
	{
		CommandCode ret = CommandCode.INVALID_COMMAND;
		// Get a random number,
		// multiply by 100 to get an integer between 0 and 99
		// get the remainder between 0 and NUM_COMMAND_CODES - 1
		// and add one so we never get 0 which is invalid
		int randNum = (int)((Math.random() * 100.0) % NUM_COMMAND_CODES) + 1;
		for (CommandCode code : CommandCode.values())
		{
			if (code.ordinal() == randNum)
			{
				ret = code;
				break;
			}
		}
		
		return ret;
	}

	@Override
	public void run()
	{
		try
		{
			int numTasks = mThreadsNum;
			
			while (true)
			{
				// Submit the number of tasks
				submitTests(numTasks);
				
				// Process the results of the returned tests
				numTasks = processDoneTests(mThreadsNum);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Got " + e.getClass() + ":" + e.getMessage(), e);
		}
	}

}
