package com.transitbuddy.query.mysql;

import com.transitbuddy.query.AgencyLimitQuery;


public class AgencyLimitMySqlQuery extends AgencyLimitQuery
{
	private static final String SELECTION_COLUMN = "agency_id";

	public AgencyLimitMySqlQuery(int index, int limit)
	{
		super(index, limit);
	}
	
	@Override
	public String getStatementString()
	{
		String sql = "";
		return sql;
	}

	@Override
	protected String getAgencyIdColumn()
	{
		return SELECTION_COLUMN;
	}
}