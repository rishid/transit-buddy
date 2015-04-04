package com.common.commands;

import com.common.utilities.ByteArrayUtils;

public class GetTrips extends SerializableCommand
{
  private static final long serialVersionUID = -4591571072172858902L;
	private static final CommandCode COMMAND_CODE = CommandCode.GET_TRIPS;
	private String mAgencyID;
	private String mRouteID;

	public GetTrips(String agencyID, String routeID)
	{
		mAgencyID = agencyID;
		mRouteID = routeID;
	}
	
	public String getAgencyID()
  {
  	return mAgencyID;
  }

	public String getRouteID()
  {
  	return mRouteID;
  }

	@Override
	public byte[] getCommandBytes()
	{
		byte[] bytes = new byte[] { COMMAND_CODE.getValue() };
		bytes = ByteArrayUtils.addAll(bytes, mAgencyID.getBytes());
		bytes = ByteArrayUtils.addAll(bytes, mRouteID.getBytes());
		
		return bytes;
	}

	@Override
	public CommandCode getCommandCode()
	{
		return COMMAND_CODE;
	}
}