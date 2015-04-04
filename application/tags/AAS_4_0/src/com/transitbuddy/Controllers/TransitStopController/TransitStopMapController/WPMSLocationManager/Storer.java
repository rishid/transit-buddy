package com.transitbuddy.Controllers.TransitStopController.TransitStopMapController.WPMSLocationManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Storer extends SQLiteOpenHelper
{	
	private static final String NAME = "wpms";
	private static final int VERSION = 5;

	private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
	private static final String DROP_TRIGGER = "DROP TRIGGER IF EXISTS ";
	private static final String DROP_VIEW = "DROP VIEW IF EXISTS ";
	
	protected static final String WIFI_GRID_MAP_TABLE = "wifi_grid_map";
	protected static final String SIGNAL_MEASUREMENT_TABLE = "signal_measurement";
	protected static final String WIRELESS_ACCESS_POINT_TABLE = "wireless_access_point";
	
	private static final String SIGNAL_MEASUREMENT_WIFI_GRID_MAP_ID_TRIGGER = "fk_signal_measurement_wifi_grid_map_id"; 
	private static final String SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_ID_TRIGGER = "fk_signal_measurement_wireless_access_point_id";
	
	protected static final String MAP_RESULTS_VIEW = "view_map_data";
	
	private static final String CREATE_WIFI_GRID_MAP_TABLE =
		"CREATE TABLE " + WIFI_GRID_MAP_TABLE +
		"(   _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"    nw_corner_latitude INTEGER NOT NULL, " +
		"    nw_corner_longitude INTEGER NOT NULL, " +
		"    se_corner_latitude INTEGER NOT NULL, " +
		"    se_corner_longitude INTEGER NOT NULL" +
		");";
	
	private static final String CREATE_SIGNAL_MEASUREMENT_TABLE =
		"CREATE TABLE " + SIGNAL_MEASUREMENT_TABLE +
		"(   _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"    wifi_grid_map_id INTEGER NOT NULL, " +
		"    wireless_access_point_id INTEGER NOT NULL, " +
		"    signal_strength REAL NOT NULL, " +
		"    FOREIGN KEY (wifi_grid_map_id) REFERENCES " + WIFI_GRID_MAP_TABLE + "(id), " +
		"    FOREIGN KEY (wireless_access_point_id) REFERENCES " + WIRELESS_ACCESS_POINT_TABLE + "(id)" +
		");";
	
	private static final String CREATE_WIRELESS_ACCESS_POINT_TABLE = 
		"CREATE TABLE " + WIRELESS_ACCESS_POINT_TABLE +
		"(   _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"    bssid TEXT NOT NULL" +
		");";

	private static final String CREATE_SIGNAL_MEASUREMENT_WIFI_GRID_MAP_FK_TRIGGER =
		"CREATE TRIGGER " + SIGNAL_MEASUREMENT_WIFI_GRID_MAP_ID_TRIGGER + 
		"BEFORE INSERT ON " + SIGNAL_MEASUREMENT_TABLE + " " +
		"FOR EACH ROW BEGIN " +
		"  SELECT CASE WHEN ((SELECT 1 FROM " + WIFI_GRID_MAP_TABLE + " WHERE _id = new.wifi_grid_map_id) IS NULL) " +
		"  THEN RAISE (ABORT, 'Foreign key violation on " + SIGNAL_MEASUREMENT_WIFI_GRID_MAP_ID_TRIGGER + "')" +
		"  END; " +
		"END;";
	
	private static final String CREATE_SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_FK_TRIGGER =
		"CREATE TRIGGER " + SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_ID_TRIGGER +
		"BEFORE INSERT ON " + SIGNAL_MEASUREMENT_TABLE + " " +
		"FOR EACH ROW BEGIN " +
		"  SELECT CASE WHEN ((SELECT 1 FROM " + WIRELESS_ACCESS_POINT_TABLE + " WHERE _id = new.wireless_access_point_id) IS NULL) " +
		"  THEN RAISE (ABORT, 'Foreign key violation on " + SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_ID_TRIGGER + "')" +
		"  END; " +
		"END;";
	
//	private static final String CREATE_MAP_DATA_VIEW =
//		"CREATE VIEW " + MAP_RESULTS_VIEW + " AS " +
//		"SELECT " + WIRELESS_ACCESS_POINT_TABLE + ".bssid AS bssid, " +
//		"(("      + WIFI_GRID_MAP_TABLE + ".nw_corner_latitude + "
//                  + WIFI_GRID_MAP_TABLE + ".se_corner_latitude) / 2" +
//  		") AS latitude, " +
//  		"(("      + WIFI_GRID_MAP_TABLE + ".nw_corner_longitude + "
//  		          + WIFI_GRID_MAP_TABLE + ".se_corner_longitude) / 2" +
//        ") AS longitude, "
//                  + SIGNAL_MEASUREMENT_TABLE + ".strength AS strength " +
//        "FROM "   + WIRELESS_ACCESS_POINT_TABLE + ", "
//                  + WIFI_GRID_MAP_TABLE + ", "
//                  + SIGNAL_MEASUREMENT_TABLE + " " +
//        "WHERE "  + WIRELESS_ACCESS_POINT_TABLE + "._id = " + SIGNAL_MEASUREMENT_TABLE + ".wireless_access_point_id " +
//        "  AND "  + WIFI_GRID_MAP_TABLE + "._id = " + SIGNAL_MEASUREMENT_TABLE + ".wifi_grid_map_id;";

	private static final String CREATE_MAP_DATA_VIEW =
		"CREATE VIEW " + MAP_RESULTS_VIEW + " AS " +
		"SELECT " + WIRELESS_ACCESS_POINT_TABLE + ".bssid AS bssid, "
                  + SIGNAL_MEASUREMENT_TABLE + ".signal_strength AS signal_strength, "
                  + WIFI_GRID_MAP_TABLE + ".nw_corner_latitude AS latitude, "
                  + WIFI_GRID_MAP_TABLE + ".nw_corner_longitude AS longitude " +                  
        "FROM "   + WIRELESS_ACCESS_POINT_TABLE  
                  + " JOIN " + SIGNAL_MEASUREMENT_TABLE  
                  + " ON " + WIRELESS_ACCESS_POINT_TABLE + "._id = "
                           + SIGNAL_MEASUREMENT_TABLE + ".wireless_access_point_id "
                  + " JOIN " + WIFI_GRID_MAP_TABLE
                  + " ON " + WIFI_GRID_MAP_TABLE + "._id = "
                           + SIGNAL_MEASUREMENT_TABLE + ".wifi_grid_map_id;";
	
	public Storer(Context context)
	{
		super(context, NAME, null, VERSION);
	}

	
	
	public void store(String sql)
	{
		SQLiteDatabase database = getWritableDatabase();
		
		database.execSQL(sql);
		
		database.close();
	}
	
	
	// TODO: Should probably use bind variables
	/**
	 * Return a cursor to the results of a passed select statement
	 */
	public Cursor fetch(String sql)
	{
		SQLiteDatabase database;

		database = getReadableDatabase();
		return database.rawQuery(sql, null);
	}
	

	@Override
	public void onCreate(SQLiteDatabase database) 
	{			
		
		// Create tables
		
		// Log.d("Test", CREATE_WIFI_GRID_MAP_TABLE);
		database.execSQL(CREATE_WIFI_GRID_MAP_TABLE);
		
		// Log.d("Test", CREATE_SIGNAL_MEASUREMENT_TABLE);
		database.execSQL(CREATE_SIGNAL_MEASUREMENT_TABLE);
		
		
		// Log.d("Test", CREATE_WIRELESS_ACCESS_POINT_TABLE);
		database.execSQL(CREATE_WIRELESS_ACCESS_POINT_TABLE);
		
		// Create foreign key constraints
		// Log.d("Test", CREATE_SIGNAL_MEASUREMENT_WIFI_GRID_MAP_FK_TRIGGER);
		database.execSQL(CREATE_SIGNAL_MEASUREMENT_WIFI_GRID_MAP_FK_TRIGGER);
		
		// Log.d("Test", CREATE_SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_FK_TRIGGER);
		database.execSQL(CREATE_SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_FK_TRIGGER);

		// Create convenience views
		// Log.d("Test", CREATE_MAP_DATA_VIEW);
		database.execSQL(CREATE_MAP_DATA_VIEW);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) 
	{
//		Log.d("Test", DROP_TABLE + WIFI_GRID_MAP_TABLE);
//		Log.d("Test", DROP_TABLE + WIRELESS_ACCESS_POINT_TABLE);
//		Log.d("Test", DROP_TABLE + SIGNAL_MEASUREMENT_TABLE);
//		Log.d("Test", DROP_TRIGGER + SIGNAL_MEASUREMENT_WIFI_GRID_MAP_ID_TRIGGER);
//		Log.d("Test", DROP_TRIGGER + SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_ID_TRIGGER);
//		Log.d("Test", DROP_VIEW + MAP_RESULTS_VIEW);
				
		// Clear existing tables
		database.execSQL(DROP_TABLE + WIFI_GRID_MAP_TABLE);
		database.execSQL(DROP_TABLE + WIRELESS_ACCESS_POINT_TABLE);
		database.execSQL(DROP_TABLE + SIGNAL_MEASUREMENT_TABLE);
		
		// Clear existing triggers
		database.execSQL(DROP_TRIGGER + SIGNAL_MEASUREMENT_WIFI_GRID_MAP_ID_TRIGGER);
		database.execSQL(DROP_TRIGGER + SIGNAL_MEASUREMENT_WIRELESS_ACCESS_POINT_ID_TRIGGER);
		
		// Clear existing views
		database.execSQL(DROP_VIEW + MAP_RESULTS_VIEW);
	
		onCreate(database);
	}
}
