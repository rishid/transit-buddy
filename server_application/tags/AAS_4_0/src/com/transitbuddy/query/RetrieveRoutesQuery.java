package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.common.commands.GetRoutes;
import com.common.commands.ICommand;
import com.common.enumerations.RouteType;
import com.common.types.TransitRoute;
import com.common.types.TransitSystem;

public class RetrieveRoutesQuery extends CommandableQuery
{
	private static final Logger LOGGER = Logger
	    .getLogger(RetrieveRoutesQuery.class);

	protected static final String AGENCY_NAME_COL = "agency_name";
	protected static final String ROUTE_ID_COL = "route_id";
	protected static final String ROUTE_TYPE_COL = "pseudo_route_type";
	protected static final String ROUTE_SHORT_NAME_COL = "route_short_name";
	protected static final String ROUTE_LONG_NAME_COL = "route_long_name";
	protected static final String ROUTE_NAME_COL = "route_name";

	/** The agency identifier in the database */
	String mAgencyId;

	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		TransitSystem system = null;
		String agencyName = null;

		// Get the agency ID
		String agencyId = getAgencyId();
		if (agencyId == null)
		{
			LOGGER.error("Agency ID was null. Cannot get routes result.");
		}

		while (rs.next())
		{
			// Check if the system has been initialized
			if (system == null)
			{
				// Get the agency name
				agencyName = rs.getString(AGENCY_NAME_COL);
				if (agencyId != null && agencyName != null)
				{
					system = new TransitSystem(agencyName, agencyId);
				}
			}
			if (system != null)
			{
				// Get the route type
				int routeTypeInt = rs.getInt(ROUTE_TYPE_COL);
				RouteType routeType = RouteType.lookup(routeTypeInt);
				if (routeType != null)
				{
					// Get the route id
					String routeId = rs.getString(ROUTE_ID_COL);
					// Get the route long name
					String routeName = rs.getString(ROUTE_NAME_COL);

					if (routeName != null && routeId != null)
						system.addRoute(routeType, new TransitRoute(routeName, routeId));
				}
				else
				{
					LOGGER.error("Could not lookup RouteType for int " + routeTypeInt);
				}
			}
		}
		try
		{
			rs.close();
		}
		catch (Exception e)
		{
			LOGGER.error("Got exception while closing result set. Exception: "
			    + e.getMessage());
		}

		LOGGER.info("Built a TransitSystem Object: " + system.getName() + " "
		    + system.getTransitRouteHashMap().size());
		return system;
	}

	/**
	 * @return the agencyId
	 */
	public String getAgencyId()
	{
		return mAgencyId;
	}

	/**
	 * @param agencyId
	 *          the agencyId to set
	 */
	protected void setAgencyId(String agencyId)
	{
		System.out.println("received agency id: " + agencyId);
		mAgencyId = agencyId;
	}

	@Override
	public void reset()
	{
		mAgencyId = null;
	}

	@Override
	public void validateCommandAndSetQueryValues(ICommand command)
	    throws IllegalArgumentException
	{
		// Ensure the command is not null
		if (command == null)
		{
			throw new IllegalArgumentException("Command is null. Cannot create get"
			    + " routes SQL statement.");
		}
		// Cast the command
		GetRoutes routesCommand = (GetRoutes) command;
		// Set the command's member values
		String agencyId = routesCommand.getAgencyID();
		if (agencyId == null)
		{
			throw new IllegalArgumentException("Command agency ID is null. Cannot"
			    + " create get routes SQL statement.");
		}
		// Set the values in the query
		setAgencyId(agencyId);
	}
}
