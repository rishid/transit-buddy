package com.transitbuddy.query;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract public class Query 
{
	/** The Query statement for the database */
	private String mStmt;
	
	/**
	 * Default Constructor
	 */
	protected Query()
	{
		
	}
	
	/**
	 * Constructor
	 */
	protected Query(String stmt)
	{
		mStmt = stmt;
	}
	
	/**
	 * Query Statement accessor
	 * @return The statement for this query
	 */
	public String getStatementString()
	{
		return mStmt;
	}
	
	public void setStatement(String stmt)
	{
		mStmt = stmt;
	}
	
	/**
	 * Retrieves the answer from the database ResultSet
	 * @return The answer to the query
	 * @throws SQLException
	 */
	abstract public Serializable getAnswer(ResultSet rs) throws SQLException;
}
