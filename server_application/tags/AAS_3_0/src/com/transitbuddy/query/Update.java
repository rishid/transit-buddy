package com.transitbuddy.query;

public class Update 
{
	/** The Update statement for the database */
	private String mStmt;
	
	/**
	 * Default Constructor
	 */
	protected Update()
	{
		
	}
	
	/**
	 * Constructor
	 */
	protected Update(String stmt)
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
}
