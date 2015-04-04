package com.transitbuddy.connection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import com.transitbuddy.query.mysql.AgencyLimitMySqlQuery;
import com.transitbuddy.query.mysql.InsertRealTimeMySqlUpdate;
import com.transitbuddy.query.mysql.InsertTransitSystemMySqlUpdate;
import com.transitbuddy.query.mysql.LowestAgencyIndexMySqlQuery;
import com.transitbuddy.query.mysql.RealTimeMySqlUpdate;
import com.transitbuddy.query.mysql.RetrieveNearbyStopsMySqlQuery;
import com.transitbuddy.query.mysql.RetrieveRealTimeStopsMySqlQuery;
import com.transitbuddy.query.mysql.RetrieveRoutesMySqlQuery;
import com.transitbuddy.query.mysql.RetrieveStopsMySqlQuery;
import com.transitbuddy.query.mysql.RetrieveTransitSystemMySqlQuery;
import com.transitbuddy.query.mysql.RetrieveTripsMySqlQuery;
import com.transitbuddy.query.mysql.TransitSystemExistsMySqlQuery;
import com.transitbuddy.query.mysql.TransitSystemMySqlUpdate;


public class MySqlConnectionManager extends ConnectionManager
{

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL_PREFIX ="jdbc:mysql://";


	/**
	 * Constructor.
	 * Opens a MySQL connection.
	 * @throws ClassNotFoundException if the database driver class could not
	 *  be found.
	 * @throws SQLException if a database access error occurs
	 * @throws FileNotFoundException If the properties file does not exist
	 * @throws IOException If there is an IO error while reading the file
	 */
	public MySqlConnectionManager(String propsPrefix, Properties props) 
	throws ClassNotFoundException, SQLException, 
	FileNotFoundException, IOException
	{ 
		super(propsPrefix, props);
		mLowestIndexQuery = new LowestAgencyIndexMySqlQuery();
		mAgencyLimitQuery = new AgencyLimitMySqlQuery(0, 0);
		mTransitSystemExistsQuery = new TransitSystemExistsMySqlQuery();
		mInsertTransitSystemUpdate = new InsertTransitSystemMySqlUpdate();
		mTransitSystemUpdate = new TransitSystemMySqlUpdate();
		mRetrieveNearbyStops = new RetrieveNearbyStopsMySqlQuery();
		mRetrieveTransitSystemQuery = new RetrieveTransitSystemMySqlQuery();
		mRetrieveTripsQuery = new RetrieveTripsMySqlQuery();
		mRetrieveStopsQuery = new RetrieveStopsMySqlQuery();
		mRetrieveRoutesQuery = new RetrieveRoutesMySqlQuery();		
		mInsertRealTimeUpdate = new InsertRealTimeMySqlUpdate();
		mRealTimeUpdate = new RealTimeMySqlUpdate();
		mRetrieveRealTimeStopsQuery = new RetrieveRealTimeStopsMySqlQuery();
	}

	protected String getDriver()
	{
		return DRIVER;
	}

	protected String getUrlPrefix()
	{
		return URL_PREFIX;
	}
}
