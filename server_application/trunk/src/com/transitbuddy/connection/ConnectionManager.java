package com.transitbuddy.connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.common.commands.ICommand;
import com.common.types.TransitSystem;
import com.transitbuddy.main.updator.Predictions.Direction.Stop;
import com.transitbuddy.query.AgencyLimitQuery;
import com.transitbuddy.query.InsertRealTimeUpdate;
import com.transitbuddy.query.InsertTransitSystemUpdate;
import com.transitbuddy.query.LowestAgencyIndexQuery;
import com.transitbuddy.query.Query;
import com.transitbuddy.query.RealTimeUpdate;
import com.transitbuddy.query.RetrieveNearbyStopsQuery;
import com.transitbuddy.query.RetrieveRealTimeStopsQuery;
import com.transitbuddy.query.RetrieveRoutesQuery;
import com.transitbuddy.query.RetrieveStopsQuery;
import com.transitbuddy.query.RetrieveTransitSystemQuery;
import com.transitbuddy.query.RetrieveTripsQuery;
import com.transitbuddy.query.TransitSystemExistsQuery;
import com.transitbuddy.query.TransitSystemUpdate;
import com.transitbuddy.query.Update;

/**
 * @author TBANNON
 * 
 */
public abstract class ConnectionManager implements QueryExecutor
{
	private static final Logger LOGGER = Logger
	    .getLogger(ConnectionManager.class);

	/** The database URL */
	private static final String DB_URL_STRING = "url";
	/** The database user name */
	private static final String DB_USER_NAME_STRING = "username";
	/** The database password */
	private static final String DB_PASSWORD_STRING = "password";
	/** The database host IP or alias */
	private static final String DB_HOST_STRING = "host";
	/** The database TCP port */
	private static final String DB_PORT_STRING = "port";
	/** The database TCP port */
	private static final String DB_NAME_STRING = "name";
	private int DEFAULT_PORT = 3306;

	/** The MySQL database connection */
	protected Connection mConnection;

	/** The string preceding all properties for this db connection */
	protected String mPropsPrefix = "";

	/** The driver for this connection */
	protected String mDriver = "";

	/** The database URL prefix for this connection */
	protected String mUrlPrefix = "";

	/** The connection URL */
	protected String mUrl = "";

	/** The database user name */
	protected String mUserName = "";

	/** The database password */
	protected String mPassword = "";

	/** The database host */
	protected String mHost = "";

	/** The database port */
	protected int mPort = DEFAULT_PORT;

	/** The database port */
	protected String mName = "";

	/**
	 * The lock object protecting executing queries/updates against the database
	 */
	protected Object mDBLock = new Object();

	// Queries
	protected AgencyLimitQuery mAgencyLimitQuery;
	protected LowestAgencyIndexQuery mLowestIndexQuery;
	protected RetrieveNearbyStopsQuery mRetrieveNearbyStops;
	protected TransitSystemExistsQuery mTransitSystemExistsQuery;
	protected RetrieveTransitSystemQuery mRetrieveTransitSystemQuery;
	protected RetrieveTripsQuery mRetrieveTripsQuery;
	protected RetrieveStopsQuery mRetrieveStopsQuery;
	protected RetrieveRealTimeStopsQuery mRetrieveRealTimeStopsQuery;
	protected RetrieveRoutesQuery mRetrieveRoutesQuery;

	// Updates
	protected InsertTransitSystemUpdate mInsertTransitSystemUpdate;
	protected InsertRealTimeUpdate mInsertRealTimeUpdate;
	protected RealTimeUpdate mRealTimeUpdate;
	protected TransitSystemUpdate mTransitSystemUpdate;

	/**
	 * Constructor. Opens a database connection.
	 * 
	 * @throws ClassNotFoundException
	 *           if the database driver class could not be found.
	 * @throws SQLException
	 *           if a database access error occurs
	 * @throws FileNotFoundException
	 *           If the properties file does not exist
	 * @throws IOException
	 *           If there is an IO error while reading the properties file
	 */
	public ConnectionManager(String propsPrefix, Properties props)
	    throws ClassNotFoundException, SQLException, FileNotFoundException,
	    IOException
	{
		mPropsPrefix = propsPrefix;
		mDriver = getDriver();
		mUrlPrefix = getUrlPrefix();
		readProperties(props);
		createConnection();
	}

	/**
	 * Closes the database connection
	 * 
	 * @throws SQLException
	 *           if a database access error occurs
	 */
	public void closeConnection() throws SQLException
	{
		if (mConnection != null)
		{
			mConnection.close();
		}
	}

	/**
	 * Creates the database connection
	 * 
	 * @throws ClassNotFoundException
	 *           if the database driver class could not be found.
	 * @throws SQLException
	 *           if a database access error occurs
	 */
	public void createConnection() throws ClassNotFoundException, SQLException
	{
		// Register the JDBC driver for the database.
		Class.forName(mDriver);

		// Get a connection to the database for a
		// user named guest with the password.
		mConnection = DriverManager.getConnection(mUrl, mUserName, mPassword);
	}

	/**
	 * Executes the given query against the database and returns the result set
	 * 
	 * @param query
	 *          The query statement to be executed against the database
	 * @return The a string representing the result of executing the given query;
	 *         An empty string is returned if there is an issue while executing
	 *         the error.
	 * @throws SQLException
	 *           - if a database access error occurs, this method is called on a
	 *           closed connection or the given parameters are not ResultSet
	 *           constants indicating type and concurrency
	 */
	public Serializable executeQuery(Query query) throws SQLException
	{
		Serializable answer = null;

		// Create the statement
		Statement stmt = mConnection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// Query the database
		LOGGER.info("SQL: " + query.getStatementString());

		ResultSet rs = null;
		synchronized (mDBLock)
		{
			rs = stmt.executeQuery(query.getStatementString());
		}
		if (rs != null)
		{
			// Format the answer
			answer = query.getAnswer(rs);
		}
		else
		{
			LOGGER.error("The reuslt set from the query was null.");
		}
		return answer;
	}

	/**
	 * Executes the given update against the database and returns the result
	 * 
	 * @param query
	 *          The update statement to be executed against the database
	 * @return The Result integer created from executing the given update
	 * @throws SQLException
	 *           - if a database access error occurs, this method is called on a
	 *           closed connection or the given parameters are not ResultSet
	 *           constants indicating type and concurrency
	 */
	public int executeUpdate(Update update) throws SQLException
	{
		int result = 0;

		// Create the statement
		Statement stmt = mConnection.createStatement(
		    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		// Query the database
		// LOGGER.info("SQL: " + update.getStatementString());

		synchronized (mDBLock)
		{
			result = stmt.executeUpdate(update.getStatementString());
		}

		return result;
	}

	/**
	 * Sets the database connection information from the properties file
	 * 
	 * @throws FileNotFoundException
	 *           If the properties file does not exist
	 * @throws IOException
	 *           If there is an IO error while reading the file
	 */
	public void readProperties(Properties props) throws FileNotFoundException,
	    IOException
	{
		// Get the url
		mUrl = props.get(mPropsPrefix + DB_URL_STRING).toString();
		// Get the username
		mUserName = props.get(mPropsPrefix + DB_USER_NAME_STRING).toString();
		// Get the password
		mPassword = props.get(mPropsPrefix + DB_PASSWORD_STRING).toString();
		// Get the host
		mHost = props.get(mPropsPrefix + DB_HOST_STRING).toString();
		// Get the db name
		mName = props.get(mPropsPrefix + DB_NAME_STRING).toString();
		// Get the port
		String port = props.get(mPropsPrefix + DB_PORT_STRING).toString();
		try
		{
			mPort = Integer.parseInt(port);
		}
		catch (NumberFormatException e)
		{
			LOGGER.error("Could not parse port from " + port + ". Using default"
			    + " port: " + DEFAULT_PORT + ".");
			mPort = DEFAULT_PORT;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getAgencyLimit()
	 */
	@Override
	public String getAgencyLimit(int agencyIndex, int maxAgencies)
	    throws SQLException
	{
		mAgencyLimitQuery.setIndex(agencyIndex);
		mAgencyLimitQuery.setLimit(maxAgencies);
		return String.valueOf(executeQuery(mAgencyLimitQuery));
	}

	abstract protected String getDriver();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getLowestAgencyIndex()
	 */
	@Override
	public String getLowestAgencyIndex() throws SQLException
	{
		return String.valueOf(executeQuery(mLowestIndexQuery));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getNearbyStops()
	 */
	@Override
	public Serializable getNearbyStops(ICommand command) throws SQLException
	{
		try
    {
	    mRetrieveNearbyStops.setCommand(command);
	    return executeQuery(mRetrieveNearbyStops);
    }
    catch (IllegalArgumentException e)
    {
    	LOGGER.error(e);
	    return null;
    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getRoutes()
	 */
	@Override
	public Serializable getRoutes(ICommand command) throws SQLException
	{
		try
    {
	    mRetrieveRoutesQuery.setCommand(command);
	    return executeQuery(mRetrieveRoutesQuery);
    }
    catch (IllegalArgumentException e)
    {
	    LOGGER.error(e);
	    return null;
    }
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getRealTimeStops()
	 */
	@Override
	public Serializable getRealTimeStops(ICommand command) throws SQLException
	{
		// Retrieve real time info
		try
		{
			mRetrieveRealTimeStopsQuery.setCommand(command);
			return executeQuery(mRetrieveRealTimeStopsQuery);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.error(e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getStops()
	 */
	@Override
	public Serializable getStops(ICommand command) throws SQLException
	{

		// Retrieve static info
		try
		{
			mRetrieveStopsQuery.setCommand(command);
			return executeQuery(mRetrieveStopsQuery);
		}
		catch (IllegalArgumentException e)
		{
			LOGGER.error(e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getTransitSystem()
	 */
	@Override
	public Serializable getTransitSystem(ICommand command) throws SQLException
	{
		try
    {
	    mRetrieveTransitSystemQuery.setCommand(command);
	    return executeQuery(mRetrieveTransitSystemQuery);
    }
    catch (IllegalArgumentException e)
    {
    	LOGGER.error(e);
	    return null;
    }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getTransitSystemExists()
	 */
	@Override
	public String getTransitSystemExists(TransitSystem transitSystem)
	    throws SQLException
	{
	    mTransitSystemExistsQuery.setTransitSystem(transitSystem);
	    return String.valueOf(executeQuery(mTransitSystemExistsQuery));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#getTrips()
	 */
	@Override
	public Serializable getTrips(ICommand command) throws SQLException
	{
		try
    {
	    mRetrieveTripsQuery.setCommand(command);
	    return executeQuery(mRetrieveTripsQuery);
    }
    catch (IllegalArgumentException e)
    {
    	LOGGER.error(e);
	    return null;
    }
	}

	abstract protected String getUrlPrefix();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#insertTransitSystem()
	 */
	@Override
	public int insertTransitSystem(TransitSystem transitSystem)
	    throws SQLException
	{
		mInsertTransitSystemUpdate.setTransitSystem(transitSystem);
		return executeUpdate(mInsertTransitSystemUpdate);
	}

	@Override
	public int insertRealTimeData(String agency, String routetag,
	    String directionname, String headsign, Stop s) throws SQLException
	{
		mInsertRealTimeUpdate.setAgency(agency);
		mInsertRealTimeUpdate.setRouteTag(routetag);
		mInsertRealTimeUpdate.setDirectionName(directionname);
		mInsertRealTimeUpdate.setHeadsign(headsign);
		mInsertRealTimeUpdate.setStop(s);
		return executeUpdate(mInsertRealTimeUpdate);
	}

	@Override
	public int updateRealTimeData(String agency, String routetag,
	    String directionname, String headsign, Stop s) throws SQLException
	{
		mRealTimeUpdate.setAgency(agency);
		mRealTimeUpdate.setRouteTag(routetag);
		mRealTimeUpdate.setDirectionName(directionname);
		mRealTimeUpdate.setHeadsign(headsign);
		mRealTimeUpdate.setStop(s);
		return executeUpdate(mRealTimeUpdate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.transitbuddy.connection.QueryExecutor#updateTransitSystem()
	 */
	@Override
	public int updateTransitSystem(TransitSystem transitSystem)
	    throws SQLException
	{
		mTransitSystemUpdate.setTransitSystem(transitSystem);
		return executeUpdate(mTransitSystemUpdate);
	}
}
