package com.transitbuddy.main;

import com.transitbuddy.main.server.TransitInfoServerRunner;
import com.transitbuddy.main.updator.TransitInfoUpdatorRunner;

public class TransitInfoServerMain
{	
	private TransitInfoServerMain()
	{
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// Start the Transit Information Database Updator thread
		try
		{
			TransitInfoUpdatorRunner updatorRunner = new TransitInfoUpdatorRunner();
			Thread updatorThread = new Thread(updatorRunner);
			updatorThread.start();
		}
		catch (Exception e)
		{
		}
		
		try
		{
			TransitInfoServerRunner serverRunner = new TransitInfoServerRunner();
			Thread serverThread = new Thread(serverRunner);
			serverThread.start();
		}
		catch (Exception e)
		{
		}
	}

}
