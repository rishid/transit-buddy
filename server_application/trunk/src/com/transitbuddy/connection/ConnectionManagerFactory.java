package com.transitbuddy.connection;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManagerFactory
{
	public static ConnectionManager getConnectionManager(String type, 
			String propsPrefix, Properties props) throws IllegalArgumentException, 
			FileNotFoundException, ClassNotFoundException, SQLException, IOException
	{
		ConnectionManager cm = null;
		
		if (type == null || type.equals(""))
		{
			throw new IllegalArgumentException(
					"Connection manager type was null or blank.");
		}
		
		if (type.equals("mysql"))
		{
			cm = new MySqlConnectionManager(propsPrefix, props);
		}
		
		return cm;
	}
}
