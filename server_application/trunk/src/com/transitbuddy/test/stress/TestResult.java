package com.transitbuddy.test.stress;

import java.util.HashMap;

import com.common.utilities.ResponseResult.ResponseStatus;

public class TestResult
{
	/** A map of ResponseStatus to the number of tests that returned that 
	 * ResponseStatus code */
	private HashMap<ResponseStatus,Integer> mResultsMap = 
		new HashMap<ResponseStatus,Integer>();
	
	/**
	 * Constructor
	 */
	public TestResult()
	{
		// Initialize the results map
		for (ResponseStatus status : ResponseStatus.values())
		{
			mResultsMap.put(status, 0);
		}
	}
	
	/**
	 * Increments the number of tests that returned the given ResponsStatus
	 * for the given status
	 * @param status The ResponseStatus whose test count will be incremented
	 */
	public void addResult(ResponseStatus status)
	{
		if (!mResultsMap.containsKey(status))
		{
			throw new IllegalArgumentException("");
		}
		
		// Get the current number of tests for the given status, increment it,
		// and then overwrite it in the map
		int num = mResultsMap.get(status);
		num++;
		mResultsMap.put(status, num);
	}
	
	/**
	 * @param status The response status type to be checked
	 * @return The number of tests that returned with a response status of the
	 *  given type; 0 is returned if the response status type is not in the
	 *  results map
	 */
	public int getNumResultsOfType(ResponseStatus status)
	{
		int ret = 0;
		Integer count = mResultsMap.get(status);
		if (count != null)
		{
			ret = count.intValue();
		}
		
		return ret;
	}
}
