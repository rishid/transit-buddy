package com.transitbuddy.query.mysql;

import com.transitbuddy.query.LowestAgencyIndexQuery;

public class LowestAgencyIndexMySqlQuery extends LowestAgencyIndexQuery
{

	private static final String INDEX_COLUMN = "index";

	/**
	 * Query Statement accessor
	 * @return The statement for this query
	 */
	@Override
	public String getStatementString()
	{
		return "";
	}
	
	@Override
	protected String getIndexColumnName()
	{
		return INDEX_COLUMN;
	}

}
