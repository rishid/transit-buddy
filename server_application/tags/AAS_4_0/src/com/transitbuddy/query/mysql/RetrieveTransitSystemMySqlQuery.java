package com.transitbuddy.query.mysql;

import com.transitbuddy.query.RetrieveTransitSystemQuery;

public class RetrieveTransitSystemMySqlQuery extends RetrieveTransitSystemQuery
{
	/**
	 * Query Statement accessor
	 * @return The statement for this query
	 */
	public String getStatementString()
	{
		return "select agency." + AGENCY_ID_COL + ", agency." + AGENCY_NAME_COL + 
		" from agency;";
	}
}
