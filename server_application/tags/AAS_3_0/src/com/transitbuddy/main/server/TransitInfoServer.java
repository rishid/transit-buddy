package com.transitbuddy.main.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.common.commands.ICommand;

public class TransitInfoServer implements Runnable
{
	private static final Logger LOGGER = Logger
	    .getLogger(TransitInfoServer.class);

	private Socket mSocket = null;

	public TransitInfoServer(Socket socket)
	{
		mSocket = socket;
	}

	@Override
	public void run()
	{

		ObjectOutputStream oos = null;
		ObjectInputStream in = null;

		try
		{
			OutputStream outstream = mSocket.getOutputStream();
			InputStream instream = mSocket.getInputStream();
			oos = new ObjectOutputStream(outstream);
			in = new ObjectInputStream(instream);

			Object input = null;
			Serializable outputObj = null;
			TransitInfoRequestProtocol protocol = new TransitInfoRequestProtocol();
			//TransitInfoRequestProtocol protocol = new TestDataRequestProtocol();

			// Get all of the input from the client
			input = in.readObject();
			if (input != null)
			{
				ICommand command = (ICommand) input;
				if (command != null)
				{
					// Process the input from the client and write the output to the
					// client's output stream
					outputObj = protocol.processCommand(command);
					oos.writeObject(outputObj);
				}
			}
			else
			{
				LOGGER.error("Got null input.");
			}
		}
		catch (IOException e)
		{
			LOGGER.error(e);
		}
		catch (ClassNotFoundException e)
		{
			LOGGER.error(e);
		}
		catch (SQLException e)
		{
			LOGGER.error(e);
		}
		finally
		{
			if (oos != null)
			{
				try
				{
					oos.close();
				}
				catch (IOException e)
				{
					LOGGER.error("Could not close object output stream. Got "
					    + "IOException: " + e.getMessage() + ".");
				}
			}
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					LOGGER.error("Could not close input stream. Got IOException: "
					    + e.getMessage() + ".");
				}
			}
			if (mSocket != null)
			{
				try
				{
					mSocket.close();
				}
				catch (IOException e)
				{
					LOGGER.error("Could not close socket. Got IOException: "
					    + e.getMessage() + ".");
				}
			}
		}
	}
}
