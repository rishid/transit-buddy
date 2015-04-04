package com.transitbuddy.connection;

import java.io.Serializable;
import java.sql.SQLException;

import com.common.commands.ICommand;
import com.common.types.TransitSystem;

public interface QueryExecutor
{
	public String getAgencyLimit(int artistIndex, int maxArtists) 
	throws SQLException;
	public String getLowestAgencyIndex() throws SQLException;
	public String getNearbyStops() throws SQLException;
	
	public Serializable getRoutes(ICommand command)throws SQLException;
	public Serializable getStops(ICommand command)throws SQLException;
	public Serializable getTransitSystem(ICommand command) throws SQLException;
	public String getTransitSystemExists(TransitSystem transitSystem) 
	throws SQLException;
	public Serializable getTrips(ICommand command) throws SQLException;
	
	public int insertTransitSystem(TransitSystem transitSystem) 
	throws SQLException;
	public int updateTransitSystem(TransitSystem transitSystem) 
	throws SQLException;
}
