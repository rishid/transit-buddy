package com.transitbuddy.test.stress.resmonitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class to monitor the resource (CPU, memory, downtime, etc) of a single
 * process.
 */
public class ResourceMonitor implements Runnable
{
	/*
	 * Private Variables
	 */

	/**
	 * The ps command to use.
	 */
	private final String psCommand;

	/**
	 * The monitoring interval (in seconds)
	 */
	private final int interval;

	/**
	 * The process ID to monitor.
	 */
	private final int processID;

	/**
	 * A count of the number of times this monitor was ran
	 */
	private int runCount;

	/**
	 * Vector of CPU percentages over time.
	 */
	private final Vector<Double> cpuUsage;

	/**
	 * Vector of virtual memory usage (in kilobytes) over time.
	 */
	private final Vector<Integer> virtualMemory;

	/**
	 * Vector of resident set memory usage (in kilobytes) over time.
	 */
	private final Vector<Integer> residentSetMemory;

	/**
	 * Vector of memory percentage usage over time.
	 */
	private final Vector<Double> memoryUsage;

	/**
	 * Counter for downtime, increments of interval
	 */
	private int downtime;

	/**
	 * The maximum downtime;
	 */
	private int maxDowntime;

	/*
	 * Constructors
	 */

	/**
	 * Constructor
	 * 
	 * @param processID
	 *          The process ID to monitor.
	 * @param interval
	 *          The interval (in seconds) to monitor
	 * @param duration
	 *          The duration (in seconds) to monitor.
	 */
	public ResourceMonitor(final int processID, final int interval,
	    final int duration)
	{
		this.runCount = 0;
		this.processID = processID;
		this.interval = interval;
		this.cpuUsage = new Vector<Double>(duration);
		this.virtualMemory = new Vector<Integer>(duration);
		this.memoryUsage = new Vector<Double>(duration);
		this.residentSetMemory = new Vector<Integer>(duration);
		this.downtime = 0;
		this.maxDowntime = 0;
		this.psCommand = "ps p " + this.processID + " u";
	}

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
	 * Get the run count.
	 * 
	 * @return The run count.
	 */
	public int getRunCount()
	{
		return this.runCount;
	}

	/**
	 * Get the CPU usage.
	 * 
	 * @return The CPU usage.
	 */
	public Vector<Double> getCPUUsage()
	{
		return this.cpuUsage;
	}

	/**
	 * Get the virtual memory.
	 * 
	 * @return The virtual memory.
	 */
	public Vector<Integer> getVirtualMemory()
	{
		return this.virtualMemory;
	}

	/**
	 * Get the resident set memory.
	 * 
	 * @return The resident set memory.
	 */
	public Vector<Integer> getResidentSetMemory()
	{
		return this.residentSetMemory;
	}

	/**
	 * Get the memory usage.
	 * 
	 * @return The memory usage.
	 */
	public Vector<Double> getMemoryUsage()
	{
		return this.memoryUsage;
	}

	/**
	 * Get the maximum CPU usage.
	 * 
	 * @return The maximum CPU usage.
	 */
	public double getMaxCPUUsage()
	{
		if (this.cpuUsage.isEmpty())
		{
			return -1;
		}
		else
		{
			return Collections.max(this.cpuUsage);
		}
	}

	/**
	 * Get the maximum virtual memory.
	 * 
	 * @return The maximum virtual memory.
	 */
	public int getMaxVirtualMemory()
	{
		if (this.virtualMemory.isEmpty())
		{
			return -1;
		}
		else
		{
			return Collections.max(this.virtualMemory);
		}
	}

	/**
	 * Get the maximum resident set memory.
	 * 
	 * @return The maximum resident set memory.
	 */
	public int getMaxResidentSetMemory()
	{
		if (this.residentSetMemory.isEmpty())
		{
			return -1;
		}
		else
		{
			return Collections.max(this.residentSetMemory);
		}
	}

	/**
	 * Get the maximum memory usage percentage.
	 * 
	 * @return The maximum memory usage percentage.
	 */
	public double getMaxMemoryUsage()
	{
		if (this.memoryUsage.isEmpty())
		{
			return -1;
		}
		else
		{
			return Collections.max(this.memoryUsage);
		}
	}

	/**
	 * Get the current downtim,e.
	 * 
	 * @return The current downtime.
	 */
	public int getDowntime()
	{
		return this.downtime * this.interval;
	}

	/**
	 * Get the max downtime.
	 * 
	 * @return The max downtime (in seconds).
	 */
	public int getMaxDowntime()
	{
		return this.maxDowntime * this.interval;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		// execute the PS command
		this.runCount++;
		System.out.println("Preparing to execute `" + this.psCommand
		    + "` command. Run count = " + this.runCount);
		final PSResult ps_result = this.executePSCommand();

		// check if the process was found
		if (ps_result == null)
		{
			// process was not found, increment downtime count
			this.downtime++;
			System.out.println("Process " + this.processID
			    + " was not found. Downtime = " + this.getDowntime() + " seconds.");
		}
		else
		{
			// if needed reset the downtime counter
			if (this.downtime > 0)
			{
				this.downtime = 0;
			}

			// store results
			System.out.println("Built " + ps_result);
			this.cpuUsage.add(ps_result.getCpuUsage());
			this.virtualMemory.add(ps_result.getVirtualMemory());
			this.memoryUsage.add(ps_result.getMemoryUsage());
			this.residentSetMemory.add(ps_result.getResidentSetSize());
		}

		// store the max downtime
		if (this.downtime > this.maxDowntime)
		{
			this.maxDowntime = this.downtime;
		}
	} // end method run

	/**
	 * Main entry point for application. Meant for testing.
	 * 
	 * @param args
	 *          String array of arguments: [0] - The process ID to monitor [1] -
	 *          The monitoring interval (in seconds) [2] - The amount of time to
	 *          run for (in seconds)
	 */
	public static void main(final String[] args)
	{
		// the first argument is the duration
		if (args.length < 3)
		{
			System.out.println("Invalid arguments");
			System.exit(-1);
		}
		// TODO: add input validation
		final int process_id = Integer.parseInt(args[0]);
		final int interval = Integer.parseInt(args[1]);
		final int duration = Integer.parseInt(args[2]);
		System.out.println("Preparing to monitor process " + process_id + " every "
		    + interval + " seconds for " + duration + " seconds...");

		// schedule a resource monitor at a fixed rate
		final ScheduledExecutorService executor = Executors
		    .newSingleThreadScheduledExecutor();
		final ResourceMonitor resource_monitor = new ResourceMonitor(process_id,
		    interval, duration);
		executor.scheduleAtFixedRate(resource_monitor, 0, interval,
		    TimeUnit.SECONDS);

		// loop until duration has been met then shutdown
		// TODO there is probably a better way to do this, but this is quick and
		// dirty
		while (true)
		{
			// sleep for one second then check run count
			try
			{
				Thread.currentThread();
				Thread.sleep(1000);
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
			if (resource_monitor.getRunCount() > duration)
			{
				System.out.println("Terminating resource monitor");
				executor.shutdown();
				break;
			}
		} // end while

		// print results and exit
		System.out.println(" ");
		System.out.println("Maximum CPU usage: "
		    + resource_monitor.getMaxCPUUsage() + "%");
		System.out.println("Maximum memory usage: "
		    + resource_monitor.getMaxMemoryUsage() + "%");
		System.out.println("Maximum virtual memory size: "
		    + resource_monitor.getMaxVirtualMemory() + " bytes");
		System.out.println("Maximum resident set size: "
		    + resource_monitor.getMaxResidentSetMemory() + " bytes");
		System.out.println("Maximum downtime: " + resource_monitor.getMaxDowntime()
		    + " seconds");
		//System.out.println("Dan is awesome!");
		System.exit(0);
	} // end method main

	/*
	 * Private Methods
	 */

	/**
	 * Execute the PS command.
	 * 
	 * @return A PSResult object for the process of interest, null if not found or
	 *         error occurs.
	 */
	private PSResult executePSCommand()
	{
		// initialize output
		PSResult ps_result = null;

		try
		{
			// execute the linux cp,,amd
			final Process process = Runtime.getRuntime().exec(this.psCommand);

			// read any errors from the command
			String str;
			final BufferedReader error_stream = new BufferedReader(
			    new InputStreamReader(process.getErrorStream()));
			while ((str = error_stream.readLine()) != null)
			{
				System.out.println("Error Occurred: " + str);
			}

			// read the output from the command
			final BufferedReader input_stream = new BufferedReader(
			    new InputStreamReader(process.getInputStream()));
			str = input_stream.readLine(); // skip first line (header)
			str = input_stream.readLine();

			// build PSResult object, and if this is the right process ID then restore
			// it
			if (str != null)
			{
				final PSResult tmp_result = new PSResult(str);
				if (tmp_result.getProcessID() == this.processID)
				{
					ps_result = tmp_result;
				}
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}

		return ps_result;
	} // end method executePSCommand
} // end class ResourceMonitor
