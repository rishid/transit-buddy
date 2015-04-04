package com.transitbuddy.connection;

import java.io.Serializable;
import java.sql.SQLException;

import com.common.commands.ICommand;
import com.common.types.TransitSystem;
import com.transitbuddy.main.updator.Predictions.Direction.Stop;

public interface QueryExecutor
{
	public String getAgencyLimit(int artistIndex, int maxArtists)
	    throws SQLException;

	public String getLowestAgencyIndex() throws SQLException;

	public Serializable getNearbyStops(ICommand command) throws SQLException;

	public Serializable getRoutes(ICommand command) throws SQLException;
	
	public Serializable getRealTimeStops(ICommand command) throws SQLException;

	public Serializable getStops(ICommand command) throws SQLException;

	public Serializable getTransitSystem(ICommand command) throws SQLException;

	public String getTransitSystemExists(TransitSystem transitSystem)
	    throws SQLException;

	public Serializable getTrips(ICommand command) throws SQLException;

	public int insertRealTimeData(String agency, String routetag,
	    String directionname, String headsign, Stop s) throws SQLException;

	public int insertTransitSystem(TransitSystem transitSystem)
	    throws SQLException;

	public int updateRealTimeData(String agency, String routetag,
	    String directionname, String headsign, Stop s) throws SQLException;

	public int updateTransitSystem(TransitSystem transitSystem)
	    throws SQLException;
}
