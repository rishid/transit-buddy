package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RetrieveTripsQuery extends CommandableQuery
{

	public RetrieveTripsQuery()
	{
		// TODO Auto-generated constructor stub
	}

	public RetrieveTripsQuery(String stmt)
	{
		super(stmt);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
