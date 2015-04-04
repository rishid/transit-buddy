package com.transitbuddy.test.stress.resmonitor;

import java.util.Vector;

/**
 * A container class to handle a single PS result
 */
public class PSResult
{
	/*
	 * Private Variables
	 */

	/**
	 * The process ID.
	 */
	private final int processID;

	/**
	 * The CPU usage percentage.
	 */
	private final double cpuUsage;

	/**
	 * The memory usage percentage.
	 */
	private final double memoryUsage;

	/**
	 * The virtual memory size (VSZ) in bytes.
	 */
	private final int virtualMemory;

	/**
	 * The resident set size (RSS) in bytes.
	 */
	private final int residentSetSize;

	/*
	 * Constructors
	 */

	/**
	 * Constructor
	 * 
	 * @param psString
	 *          A single line from the ps command to build results from.
	 * 
	 */
	public PSResult(final String psString)
	{
		// parse the string
		final Vector<String> fields = this.parsePsString(psString);

		// NOTE: this assumes the command "ps -u" was used
		// TODO: make more robust
		this.processID = Integer.valueOf(fields.get(1));
		this.cpuUsage = Double.valueOf(fields.get(2));
		this.memoryUsage = Double.valueOf(fields.get(3));
		this.virtualMemory = Integer.valueOf(fields.get(4));
		this.residentSetSize = Integer.valueOf(fields.get(5));
	} // end constructor

	/*
	 * Public Methods
	 */

	/**
	 * Get the process ID.
	 * 
	 * @return The process ID.
	 */
	public int getProcessID()
	{
		return this.processID;
	}

	/**
	 * Get the CPU usage.
	 * 
	 * @return The CPU usage.
	 */
	public double getCpuUsage()
	{
		return this.cpuUsage;
	}

	/**
	 * Get the memory usage.
	 * 
	 * @return The memory usage.
	 */
	public double getMemoryUsage()
	{
		return this.memoryUsage;
	}

	/**
	 * Get the virtual memory.
	 * 
	 * @return The virtual memory.
	 */
	public int getVirtualMemory()
	{
		return this.virtualMemory;
	}

	/**
	 * Get the resident set size.
	 * 
	 * @return The resident set size.
	 */
	public int getResidentSetSize()
	{
		return this.residentSetSize;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "PSResult [processID=" + this.processID + ", cpuUsage="
		    + this.cpuUsage + ", memoryUsage=" + this.memoryUsage
		    + ", virtualMemory=" + this.virtualMemory + ", residentSetSize="
		    + this.residentSetSize + "]";
	}

	/*
	 * Private Methods
	 */

	/**
	 * Parse the Ps string into its parts
	 * 
	 * @param psString
	 *          String to parse
	 * @return Vector of field values.
	 */
	private Vector<String> parsePsString(final String psString)
	{
		// split the ps string
		final String[] parts = psString.split("\\s");

		// loop through and pull out all non-empty strings
		final Vector<String> fields = new Vector<String>();
		for (final String part2 : parts)
		{
			final String part = part2.trim();
			if (!part.isEmpty())
			{
				fields.add(part);
			}
		}

		return fields;
	} // end method parsePsString
} // end class PSResult
