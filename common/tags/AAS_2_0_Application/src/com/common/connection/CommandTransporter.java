package com.common.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.common.commands.SerializableCommand;
import com.common.utilities.ResponseResult;

public class CommandTransporter
{
	public static final int DEFAULT_TIMEOUT_MILLISECONDS = 15000;
	private Socket mSock;
	private ObjectOutputStream mOut;
	private ObjectInputStream mIn;
	private int mTimeout = DEFAULT_TIMEOUT_MILLISECONDS;

	/**
	 * Instantiates a command transporter object which is responsible for issuing
	 * commands from the TransitBuddy application to the TransitBuddy server or 
	 * vice versa
	 * @param host The host address
	 * @param port The port to open the connection on
	 * @param timeout The timeout in milliseconds
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public CommandTransporter(String host, int port, int timeout) 
	throws UnknownHostException,
	    IOException
	{
		setTimeout(timeout);

		// Get socket with given timeout
		mSock = TimedSocket.getSocket(host, port, timeout);
		
		// Setting the timeout will cause a read() call on the InputStream
		// associated with this Socket will block for only this amount of time
		// If the timeout expires, a java.net.SocketTimeoutException is raised,
		// though the Socket is still valid. 
		mSock.setSoTimeout(mTimeout);

		mIn = new ObjectInputStream(mSock.getInputStream());
		mOut = new ObjectOutputStream(mSock.getOutputStream());
	}

	public void finalize()
	{
		if (mSock != null)
		{
			try
			{
				mSock.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ResponseResult sendCommand(SerializableCommand command)
	{
		ResponseResult result = null;

		// Write the command to the socket
		try
		{
			mOut.writeObject(command);
			mOut.flush();

			// Read the object from the socket
			try
			{
				Object obj = mIn.readObject();
				if (obj != null)
				{
					// COMPLETED
					result = new ResponseResult(ResponseResult.ResponseStatus.Completed,
					    "Success", obj);
				}
				else
				{
					// FAILED result
					result = new ResponseResult(ResponseResult.ResponseStatus.Failed,
					    "Returned object was null.", null);
				}
			}
			catch (SocketTimeoutException e)
			{
				// TIMEDOUT result
				result = new ResponseResult(ResponseResult.ResponseStatus.TimedOut, 
						"Got SocketTimeoutException trying to read an object from the " +
						"socket: " + e.getMessage(), null);
			}
			catch (ClassNotFoundException e)
			{
				// FAILED result
				result = new ResponseResult(ResponseResult.ResponseStatus.Failed, 
						"Got ClassNotFoundException trying to read an object from the " +
						"socket: " + e.getMessage(), null);
			}
		}
		catch (IOException e)
		{
			// FAILED result
			result = new ResponseResult(ResponseResult.ResponseStatus.Failed,
					"Got IOException trying to write an object to the socket: " +
					e.getMessage(), null);
		}

		return result;
	}

	public int getTimeout()
  {
  	return mTimeout;
  }

	public void setTimeout(int timeout)
  {
  	mTimeout = timeout;
  }
}