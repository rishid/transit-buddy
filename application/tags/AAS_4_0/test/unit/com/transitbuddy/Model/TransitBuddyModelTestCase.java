package com.transitbuddy.Model;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.common.commands.GetStops.ScheduleType;
import com.common.enumerations.RouteType;
import com.common.types.Coordinate;
import com.common.types.TransitData;
import com.common.types.TransitRoute;
import com.common.types.TransitStop;
import com.common.types.TransitSystem;
import com.common.utilities.ResponseResult.ResponseStatus;
import com.transitbuddy.enumerations.MapType;

public class TransitBuddyModelTestCase extends AndroidTestCase
{
	final int PORT_NUM = 1099;
	final int COMMAND_TIMEOUT = 5000; // milliseconds
	final String HOST_ADDR = "129.10.128.235";

	final int WRONG_PORT_NUM = 1098;
	final int ZERO_COMMAND_TIMEOUT = 0;

	/**
	 * @param name
	 */
	public TransitBuddyModelTestCase()
	{
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#TransitBuddyModel()}
	 * 
	 * @author Rob
	 */
	public final void testTransitBuddyModel()
	{
		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR, 
														PORT_NUM,
														COMMAND_TIMEOUT);

		Assert.assertNotNull(model);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getSystemID()}
	 * 
	 * @author Rob
	 */
	public final void testGetSystemID()
	{
		final String systemIDTest1 = "1";
		final String systemIDTest2 = "aaaaaa";

		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);

		// Test 1, set system id to a plausible value.
		model.setSystemID(systemIDTest1);
		Assert.assertEquals(systemIDTest1, model.getSystemID());

		// Test 2, set system id to a random value
		model.setSystemID(systemIDTest2);
		Assert.assertEquals(systemIDTest2, model.getSystemID());
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getSystemID(int)}
	 * 
	 * @author Rob
	 */
	public final void testGetSystemIDInt()
	{
		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);

		/* TEST 1 */

		// Test to make sure the model returns null if the index is not valid.
		Assert.assertNull(model.getSystemID(0));

		/* TEST 2 */

		// Now load the system data structure with data
		ArrayList<String> systems = new ArrayList<String>();

		try
		{
			model.getTransitSystems(systems);
		}
		catch (IOException ioe)
		{
			fail("Exception caught");
		}

		// Assert if the systems is null or empty.
		Assert.assertNotNull(systems);
		Assert.assertFalse(systems.size() == 0);

		// Check that the return value is correct for valid indexes.
		int maxIndex = systems.size() - 1;
		Assert.assertEquals(systems.get(0), model.getSystemID(0));
		Assert.assertEquals(systems.get(maxIndex), model.getSystemID(maxIndex));
		// Check invalid indexes
		Assert.assertNull(model.getSystemID(-1));
		Assert.assertNull(model.getSystemID(systems.size()));
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#setSystemID(java.lang.String)}
	 * 
	 * @author Rob
	 */
	public final void testSetSystemID()
	{
		final String systemIDTest1 = "1";
		final String systemIDTest2 = "aaaaaa";

		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);

		// Test 1, set system id to a plausible value.
		model.setSystemID(systemIDTest1);
		Assert.assertEquals(systemIDTest1, model.getSystemID());

		// Test 2, set system id to a random value
		model.setSystemID(systemIDTest2);
		Assert.assertEquals(systemIDTest2, model.getSystemID());
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getRouteType()}
	 * 
	 * @author Fabian
	 */
	public final void testGetRouteType()
	{
	    final RouteType ROUTE_TYPE = RouteType.Gondola;

        // Create TransitBuddyModel
        TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
            COMMAND_TIMEOUT);

        // Set/get the route type
        tbm.setRouteType(ROUTE_TYPE);
        Assert.assertEquals(tbm.getRouteType(), ROUTE_TYPE);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getRouteTypeID(int)}
	 * 
	 * @author Fabian
	 */
	public final void testGetRouteTypeID()
	{
	    final RouteType ROUTE_TYPE_1 = RouteType.Gondola;
        final String SYSTEM_NAME = "System";
        final String SYSTEM_ID = "1";

        TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
            COMMAND_TIMEOUT);

        TransitRoute route1 = new TransitRoute(null, null, null);

        TransitSystem system = new TransitSystem(SYSTEM_NAME, SYSTEM_ID);
        system.addRoute(ROUTE_TYPE_1, route1);

        TransitData data = new TransitData();
        data.addTransitSystem(system);

        tbm.setSystemID(SYSTEM_ID);
        tbm.setRouteType(ROUTE_TYPE_1);

        tbm.setTransitBuddyData(null);
        assertNull(tbm.getRouteTypeID(0));

        tbm.setTransitBuddyData(data);
        assertEquals(tbm.getRouteTypeID(0).getName(), ROUTE_TYPE_1.getName());

        assertNull(tbm.getRouteTypeID(1));
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#setRouteType(com.common.enumerations.RouteType)}
	 * 
	 * @author Fabian
	 */
	public final void testSetRouteType()
	{
	    final RouteType ROUTE_TYPE = RouteType.Funicular;
	    
	    // Create the TransitBuddyModel
        TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
            COMMAND_TIMEOUT);

        // Null test
        tbm.setRouteType(null);
        assertNull(tbm.getRouteType());

        // Set test
        tbm.setRouteType(ROUTE_TYPE);
        assertEquals(tbm.getRouteType(), ROUTE_TYPE);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getRouteID()}
	 * 
	 * @author Tara
	 */
	public final void testGetRouteID()
	{
		final String ROUTE_ID = "Test Route";

		// Create TransitBuddyModel
		TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);

		// Set the route id
		tbm.setRouteID(ROUTE_ID);
		assertEquals(tbm.getRouteID(), ROUTE_ID);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getRouteID(int)}
	 * 
	 * @author Tara
	 */
	public final void testGetRouteIDInt()
	{
		final RouteType ROUTE_1_2_TYPE = RouteType.Bus;
		final RouteType ROUTE_3_4_TYPE = RouteType.Rail;
		final String ROUTE_1_NAME = "Route 1";
		final String ROUTE_1_ID = "1";
		final String ROUTE_2_NAME = "Route 2";
		final String ROUTE_2_ID = "2";
		final String ROUTE_3_NAME = "Route 3";
		final String ROUTE_3_ID = "3";
		final String ROUTE_4_NAME = "Route 4";
		final String ROUTE_4_ID = "4";
		final String SYSTEM_NAME = "TestName";
		final String SYSTEM_ID = "1";

		// Create TransitBuddyModel
		TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);

		// Create 4 routes with empty trips:
		// first 2 have the same RouteType, second 2 have the same RouteType
		TransitRoute route1 = new TransitRoute(ROUTE_1_NAME, ROUTE_1_ID, null);
		TransitRoute route2 = new TransitRoute(ROUTE_2_NAME, ROUTE_2_ID, null);
		TransitRoute route3 = new TransitRoute(ROUTE_3_NAME, ROUTE_3_ID, null);
		TransitRoute route4 = new TransitRoute(ROUTE_4_NAME, ROUTE_4_ID, null);

		// Create a Transit System with the 4 routes
		TransitSystem system = new TransitSystem(SYSTEM_NAME, SYSTEM_ID);
		system.addRoute(ROUTE_1_2_TYPE, route1);
		system.addRoute(ROUTE_1_2_TYPE, route2);
		system.addRoute(ROUTE_3_4_TYPE, route3);
		system.addRoute(ROUTE_3_4_TYPE, route4);

		// Create transit data with the 1 system
		TransitData data = new TransitData();
		data.addTransitSystem(system);

		// Set the TransitBuddyModel member variables
		tbm.setSystemID(SYSTEM_ID);
		tbm.setRouteType(ROUTE_1_2_TYPE);
		tbm.setRouteID(ROUTE_1_ID);

		// Test: Ensure that having a null transit data value returns null route ID
		tbm.setTransitBuddyData(null);
		assertNull(tbm.getRouteID(0));
		// Test: Try an index that is at the upper bounds
		tbm.setTransitBuddyData(data);
		assertEquals(tbm.getRouteID(1), ROUTE_2_ID);
		// Test: Try and index that is too high
		assertNull(tbm.getRouteID(2));
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#setRouteID(java.lang.String)}
	 * 
	 * @author Tara
	 */
	public final void testSetRouteID()
	{
		// Create the TransitBuddyModel
		TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);

		// Test setting the route ID to null and ensure it is null when we get it
		tbm.setRouteID(null);
		assertNull(tbm.getRouteID());

		// Test setting the route ID to a non-null string and ensure it is not null
		final String ROUTE_ID = "Test Route";
		tbm.setRouteID(ROUTE_ID);
		assertEquals(tbm.getRouteID(), ROUTE_ID);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getTripID()}
	 * 
	 * @author Rishi
	 */
	public final void testGetTripID()
	{
		testSetTripID();
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getTripID(int)}
	 * 
	 * @author Rishi
	 */
	public final void testGetTripIDInt()
	{
		// Null check
		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);
		model.setTransitBuddyData(null);
		Assert.assertNull(model.getTripID(0));
		
		ArrayList<String> trips = new ArrayList<String>();
		model = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
			    COMMAND_TIMEOUT);		

		model.setSystemID("mbta");
		model.setRouteType(RouteType.Subway);
		model.setRouteID("Red Line");
		model.setTripID("Alewife");

		ResponseStatus status = ResponseStatus.Failed;

		try
		{
			status = model.getTransitTrips("Red Line", trips);
		}
		catch (IOException ioe)
		{
			fail("Exception caught on getTransitTrips");
		}
		
		Assert.assertFalse(trips.size() == 0);
		Assert.assertTrue(status == ResponseStatus.Completed);
		
		for (int i = 0; i < trips.size(); i++)
			Assert.assertNotNull(model.getTripID(i));
		
		Assert.assertNull(model.getTripID(trips.size()));
		
		// Test timeout
		status = ResponseStatus.Completed;
		
		model = new TransitBuddyModel(HOST_ADDR, PORT_NUM, 1);
		model.setSystemID("mbta");
		model.setRouteType(RouteType.Subway);
		model.setRouteID("Red Line");
		model.setTripID("Alewife");
		
		try
		{
			status = model.getTransitTrips("Red Line", trips);
		}
		catch (IOException ioe)
		{
			fail("Exception caught on getTransitTrips");
		}
		
		Assert.assertTrue(status == ResponseStatus.TimedOut);		
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#setTripID(java.lang.String)}
	 * 
	 * @author Rishi
	 */
	public final void testSetTripID()
	{
		TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    COMMAND_TIMEOUT);
		String actual = null;
		tbm.setTripID(actual);
		assertEquals(tbm.getTripID(), actual);
		actual = new String();
		tbm.setTripID(actual);
		assertEquals(tbm.getTripID(), actual);
		actual = "mycoolstring";
		tbm.setTripID(actual);
		assertEquals(tbm.getTripID(), actual);
		actual = "reallllllllllllllllylongstring";
		tbm.setTripID(actual);
		assertEquals(tbm.getTripID(), actual);
	}

	/**
	 * Test method for {@link com.transitbuddy.Model.TransitBuddyModel#getMapID()}
	 * 
	 * @author Mike
	 */
	public final void testGetMapID()
	{
        TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR,
														 PORT_NUM,
														 COMMAND_TIMEOUT);	
        
        final MapType mapIDTest1 = MapType.NearbyStops; 
		final MapType mapIDTest2 = null;
		
         // Test 1, set map id to a plausible value.
		model.setMapID(mapIDTest1);
		Assert.assertEquals(mapIDTest1, model.getMapID());
	
		// Test 2, set map id to null
		model.setMapID(mapIDTest2);
		Assert.assertEquals(mapIDTest2, model.getMapID());
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#setMapID(com.transitbuddy.enumerations.MapType)}
	 * 
	 * @author Mike
	 */
	public final void testSetMapID()
	{
		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR,
														PORT_NUM,
														COMMAND_TIMEOUT);
		
		final MapType mapIDTest1 = MapType.NearbyStops; 
		final MapType mapIDTest2 = MapType.TransitTrip;
		final MapType mapIDTest3 = null;
			
		// Test 1, set map id to a plausible value.
		model.setMapID(mapIDTest1);
		Assert.assertEquals(mapIDTest1, model.getMapID());
		
		// Test 2, set map id to the other plausible value.
		model.setMapID(mapIDTest2);
		Assert.assertEquals(mapIDTest2, model.getMapID());
		
		// Test 3, set map id to null
		model.setMapID(mapIDTest3);
		Assert.assertEquals(mapIDTest3, model.getMapID());
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getTransitSystems(java.util.ArrayList)}
	 * 
	 * @author Tara
	 */
	public final void testGetTransitSystems()
	{
		final ResponseStatus FAILED = ResponseStatus.Failed;
		final ResponseStatus COMPLETED = ResponseStatus.Completed;
		final ResponseStatus TIMED_OUT = ResponseStatus.TimedOut;

		// Test IOException returns null by talking to the wrong server port
		TransitBuddyModel tbmWrongPort = new TransitBuddyModel(HOST_ADDR,
		    WRONG_PORT_NUM, COMMAND_TIMEOUT);
		ArrayList<String> systems = new ArrayList<String>();

		// Assert that the response is failed and the number of systems returned
		// is 0
		try
		{
			assertEquals(tbmWrongPort.getTransitSystems(systems), FAILED);
			fail("Should have thrown IOException while using the wrong"
			    + " server port: " + WRONG_PORT_NUM);
		}
		catch (IOException e)
		{

		}
		assertEquals(systems.size(), 0);

		// Create a model with the correct port, but have it timeout after zero
		// seconds
		// Ensure we get a TimedOut status and that no systems were returned
		TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM,
		    ZERO_COMMAND_TIMEOUT);
		try
		{
			assertEquals(tbm.getTransitSystems(systems), TIMED_OUT);
		}
		catch (IOException e)
		{
			fail("Should not have gotten IOException when using server with"
			    + " correct port: " + PORT_NUM);
		}
		assertEquals(systems.size(), 0);

		// Create a model with the correct port
		tbm = new TransitBuddyModel(HOST_ADDR, PORT_NUM, COMMAND_TIMEOUT);

		// Test that connecting to the server on the correct port gets a completed
		// status and that the systems are populated with at least 1 system
		try
		{
			assertEquals(tbm.getTransitSystems(systems), COMPLETED);
		}
		catch (IOException e)
		{
			fail("Should not have gotten IOException when using server with"
			    + " correct port: " + PORT_NUM);
		}
		assertTrue(systems.size() > 0);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getTransitRouteTypes(java.lang.String, java.util.ArrayList)}
	 * 
	 * @author Rishi
	 */
	public final void testGetTransitRouteTypes()
	{
		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR,
														WRONG_PORT_NUM,
														COMMAND_TIMEOUT);
    
		ArrayList<String> routeTypes = new ArrayList<String>();
		
		try
		{
			assertEquals(model.getTransitRouteTypes("mbta", routeTypes), ResponseStatus.Failed);
			fail("Should have thrown IOException while using the wrong"
			    + " server port: " + WRONG_PORT_NUM);
		}
		catch (IOException e)
		{

		}
		assertEquals(routeTypes.size(), 0);
		
		model = new TransitBuddyModel(HOST_ADDR, PORT_NUM, ZERO_COMMAND_TIMEOUT);
		try
		{
			assertEquals(model.getTransitRouteTypes("mbta", routeTypes), ResponseStatus.TimedOut);
		}
		catch (IOException e)
		{
			fail("Should not have gotten IOException when using server with"
			    + " correct port: " + PORT_NUM);
		}
		assertEquals(routeTypes.size(), 0);
		
		model = new TransitBuddyModel(HOST_ADDR, PORT_NUM, COMMAND_TIMEOUT);

		try
		{
			assertEquals(model.getTransitRouteTypes("mbta", routeTypes), ResponseStatus.Completed);
		}
		catch (IOException e)
		{
			fail("Should not have gotten IOException when using server with"
			    + " correct port: " + PORT_NUM);
		}
		Assert.assertFalse(routeTypes.size() == 0);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getTransitRoutes(com.common.enumerations.RouteType, java.util.ArrayList)}
	 * 
	 * @author Mike
	 */
	public final void testGetTransitRoutes()
	{
		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR,
														PORT_NUM,
														COMMAND_TIMEOUT);
				
		ArrayList<String> routeList    = new ArrayList<String>();
		
		ResponseStatus status = ResponseStatus.Failed;
		
		// Test 1, set route type to a plausible value, normal timeout
		model.setSystemID("mbta");
			
		status = model.getTransitRoutes(RouteType.Subway, routeList);
		
		// Checks that the response is as expected
		Assert.assertEquals(ResponseStatus.Completed, status);
		
		// Checks that the route list is not null
		Assert.assertNull(routeList);
		
		// Checks that the route list is not 0
		Assert.assertFalse(routeList.size() == 0);
		
		// Checks that the response is as expected
		Assert.assertFalse(status != ResponseStatus.Completed);
		
		// Test 2, Test timeout 
		routeList.clear();
		
		status = ResponseStatus.Completed;
		
		TransitBuddyModel timeoutModel = new TransitBuddyModel(HOST_ADDR,
															   PORT_NUM,
															   ZERO_COMMAND_TIMEOUT);
	
		timeoutModel.setSystemID("mbta");
		
		status = timeoutModel.getTransitRoutes(RouteType.Subway, routeList);

		// Checks that the response is as expected
		Assert.assertFalse(status != ResponseStatus.TimedOut);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getTransitTrips(java.lang.String, java.util.ArrayList)}
	 * 
	 * @author Fabian
	 */
	public final void testGetTransitTrips()
	{
	    ArrayList<String> trips = new ArrayList<String>();
	    
	    // Timeout
	    
	    TransitBuddyModel tbmTimeout = new TransitBuddyModel(HOST_ADDR, 
                PORT_NUM,
                ZERO_COMMAND_TIMEOUT);
	    
	    tbmTimeout.setSystemID("mbta");
        tbmTimeout.setRouteType(RouteType.Subway);
        tbmTimeout.setRouteID("Red Line");
        
        ResponseStatus rsTimeout = ResponseStatus.Completed;

        try {
            rsTimeout = tbmTimeout.getTransitTrips(tbmTimeout.getRouteID(),
                    trips);
        } catch (IOException ioe) {
            fail("Exception CAUGHT in testGetTransitTrips TIMEOUT path");
        }

        Assert.assertFalse(rsTimeout != ResponseStatus.TimedOut);
        
        // Exception
        
        TransitBuddyModel tbmException = new TransitBuddyModel(HOST_ADDR, 
                WRONG_PORT_NUM,
                COMMAND_TIMEOUT);
        
        tbmException.setSystemID("mbta");
        tbmException.setRouteType(RouteType.Subway);
        tbmException.setRouteID("Red Line");
        
        ResponseStatus rsException = ResponseStatus.Completed;

        try {
            rsException = tbmException.getTransitTrips(
                    tbmException.getRouteID(), trips);
        } catch (IOException ioe) {
            fail("Exception NOT CAUGHT in testGetTransitTrips EXCEPTION path");
        }

        Assert.assertFalse(rsException != ResponseStatus.Failed);
        
        // Normal
        
        TransitBuddyModel tbm = new TransitBuddyModel(HOST_ADDR, 
                PORT_NUM,
                COMMAND_TIMEOUT);
        
        tbm.setSystemID("mbta");
        tbm.setRouteType(RouteType.Subway);
        tbm.setRouteID("Red Line");
        
        ResponseStatus rs = ResponseStatus.Failed;

        Assert.assertNull(trips);
        
        try {
            rs = tbm.getTransitTrips(tbm.getRouteID(), trips);
        } catch (IOException ioe) {
            fail("Exception CAUGHT in testGetTransitTrips NORMAL path");
        }

        Assert.assertFalse(trips.size() == 0);
        Assert.assertFalse(rs != ResponseStatus.Completed);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getTransitStops(java.lang.String, java.util.ArrayList, com.common.commands.GetStops.ScheduleType)}
	 * 
	 * @author Rob
	 */
	public final void testGetTransitStops()
	{
		/* TEST 1- Normal path */

		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR,
														PORT_NUM,
														COMMAND_TIMEOUT);
		
		ResponseStatus status = ResponseStatus.Failed;

		ArrayList<String> systems = new ArrayList<String>();
		ArrayList<String> routeTypes = new ArrayList<String>();
		ArrayList<String> routeList = new ArrayList<String>();
		ArrayList<String> trips = new ArrayList<String>();
		ArrayList<TransitStop> stops = new ArrayList<TransitStop>();
		
		//Need to load up the data structures.
		try
		{
			status = model.getTransitSystems(systems);
			model.setSystemID("mbta");
			status = model.getTransitRouteTypes(model.getSystemID(), routeTypes);
			model.setRouteType(RouteType.Subway);
			status = model.getTransitRoutes(model.getRouteType(), routeList);
			model.setRouteID(model.getRouteID(0));
			status = model.getTransitTrips(model.getRouteID(), trips);
			model.setTripID("1");
		}
		catch(IOException ioe)
		{
			fail("Exception caught");
		}
		
		status = ResponseStatus.Failed;

		try
		{
			status = model.getTransitStops(model.getTripID(), 
										   stops,
										   ScheduleType.ALL_TIMES);
		}
		catch (IOException ioe)
		{
			fail("Exception caught");
		}

		Assert.assertNull(stops);
		Assert.assertFalse(stops.size() == 0);
		Assert.assertFalse(status != ResponseStatus.Completed);

		/* TEST 2- Timeout path */

		// Set the timeout to 1 millisecond to guarantee a timeout
		TransitBuddyModel modelTimeout = new TransitBuddyModel(HOST_ADDR, 
															   PORT_NUM,
															   ZERO_COMMAND_TIMEOUT);

		systems.clear();
		routeList.clear();
		trips.clear();
		stops.clear();
		
		//Need to load up the data structures.
		try
		{
			modelTimeout.getTransitSystems(systems);
			modelTimeout.setSystemID("mbta");
			modelTimeout.getTransitRouteTypes(modelTimeout.getSystemID(), routeTypes);
			modelTimeout.setRouteType(RouteType.Subway);
			modelTimeout.getTransitRoutes(modelTimeout.getRouteType(), routeList);
			modelTimeout.setRouteID(modelTimeout.getRouteID(0));
			modelTimeout.getTransitTrips(modelTimeout.getRouteID(), trips);
			modelTimeout.setTripID("1");
		}
		catch(IOException ioe)
		{
			fail("Exception caught");
		}

		ResponseStatus statusTimeout = ResponseStatus.Completed;

		try
		{
			statusTimeout = modelTimeout.getTransitStops(modelTimeout.getTripID(),
														 stops, 
														 ScheduleType.ALL_TIMES);
		}
		catch (IOException ioe)
		{
			fail("Exception caught");
		}

		Assert.assertFalse(statusTimeout != ResponseStatus.TimedOut);

		/* TEST 3- Exception path */

		TransitBuddyModel modelException = new TransitBuddyModel(HOST_ADDR,
																 WRONG_PORT_NUM, 
																 COMMAND_TIMEOUT);
		systems.clear();
		routeList.clear();
		trips.clear();
		stops.clear();
		
		//Need to load up the data structures.
		try
		{
			modelException.getTransitSystems(systems);
			modelException.setSystemID("mbta");
			modelException.getTransitRouteTypes(modelException.getSystemID(), routeTypes);
			modelException.setRouteType(RouteType.Subway);
			modelException.getTransitRoutes(modelException.getRouteType(), routeList);
			modelException.setRouteID(modelException.getRouteID(0));
			modelException.getTransitTrips(modelException.getRouteID(), trips);
			modelException.setTripID("1");
		}
		catch(IOException ioe)
		{
			fail("Exception caught");
		}

		ResponseStatus statusException = ResponseStatus.Failed;

		try
		{
			statusException = modelTimeout.getTransitStops(modelException.getTripID(), 
														   stops, 
														   ScheduleType.ALL_TIMES);
			fail("Exception not thrown");
		}
		catch (IOException ioe)
		{

		}

		Assert.assertFalse(statusException != ResponseStatus.Failed);
	}

	/**
	 * Test method for
	 * {@link com.transitbuddy.Model.TransitBuddyModel#getNearbyStops(int, com.common.types.Coordinate, int, java.util.ArrayList)}
	 * 
	 * @author Mike
	 */
	public final void testGetNearbyStops()
	{
		TransitBuddyModel model = new TransitBuddyModel(HOST_ADDR,
														PORT_NUM,
														COMMAND_TIMEOUT);

		final int vicinityTest = 10000; 
		final int maxStopsTest = 25;
		
		final Coordinate coordinateTest1 = new Coordinate((float)42.282204, (float)-71.01837);
		final Coordinate coordinateTest2 = new Coordinate((float) 0.0, (float) 0.0);
		
		final boolean        expectedEmpty1      = false;
		final boolean        expectedEmpty2      = true;
		
		ResponseStatus expectedResult            = ResponseStatus.Completed;
		ArrayList<TransitStop> nearbyStops       = new ArrayList<TransitStop>();
		
		ResponseStatus status = ResponseStatus.Failed;
			
		
		// Test 1, set vicinity, coordinates, max stops to a plausible values.
		model.setSystemID("mbta");
		
		// Checks that the response is as expected
		try 
		{
			status = model.getNearbyStops(vicinityTest, coordinateTest1, maxStopsTest, nearbyStops);	
		}
		catch(IOException ioe)
		{
			fail("Exception caught");
		}
		
		Assert.assertEquals(expectedResult, status);
		
		// Checks that the nearbyStops list contains stops
		Assert.assertEquals(expectedEmpty1, nearbyStops.isEmpty());	
		
		// Test 2, set vicinity and max stops to a plausible values, 
		// but the coordinate are outside of the area that is supported by transit buddy
		// Checks that the response is as expected
		   
		
		try 
		{
			status = model.getNearbyStops(vicinityTest, coordinateTest2, maxStopsTest, nearbyStops);	
		}
		catch(IOException ioe)
		{
			fail("Exception caught");
		}
		
		Assert.assertEquals(expectedResult,  status);

		// Checks that the nearbyStops list contains no stops
		Assert.assertEquals(expectedEmpty2, nearbyStops.isEmpty());	
		
		// Test 3, Timeout Path
		TransitBuddyModel timeoutModel = new TransitBuddyModel(HOST_ADDR,
																PORT_NUM,
																ZERO_COMMAND_TIMEOUT);
		
		status = ResponseStatus.Completed;
		
		timeoutModel.setSystemID("mbta");
		
		try 
		{
			status = timeoutModel.getNearbyStops(vicinityTest, coordinateTest1, maxStopsTest, nearbyStops);	
		}
		catch(IOException ioe)
		{
			fail("Exception caught");
		}
		
		Assert.assertFalse(status != ResponseStatus.TimedOut);
		
		// Test 4, Exception Path
		TransitBuddyModel exceptionModel = new TransitBuddyModel(HOST_ADDR,
				                                                WRONG_PORT_NUM,
																COMMAND_TIMEOUT);
		
		status = ResponseStatus.Failed;
		
		exceptionModel.setSystemID("mbta");
		
		try 
		{
			status = exceptionModel.getNearbyStops(vicinityTest, coordinateTest1, maxStopsTest, nearbyStops);
			
			fail("Exception not thrown");
		}
		catch(IOException ioe)
		{
			fail("Exception caught");
		}
		
		Assert.assertFalse(status != ResponseStatus.Failed);
	}

}
