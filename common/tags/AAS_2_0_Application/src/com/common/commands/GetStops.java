package com.common.commands;

import com.common.utilities.ByteArrayUtils;

public class GetStops extends SerializableCommand
{
  private static final long serialVersionUID = 1293872803856920505L;
	private static final CommandCode COMMAND_CODE = CommandCode.GET_STOPS;

	public enum ScheduleType
	{		
		ALL_TIMES((byte) 0x00),
		NEXT_FIVE((byte) 0x01);
		

		private byte mValue;
		
		ScheduleType(byte value)
		{
			mValue = value;
		}

		public byte getValue()
		{
			return mValue;
		}
	}

	private String mAgencyID;
	private String mRouteID;
	private String mTripID;
	private ScheduleType mType;

	public GetStops(String agencyID, String routeID, String tripID,
	    ScheduleType type)
	{
		mAgencyID = agencyID;
		mRouteID = routeID;
		mTripID = tripID;
		mType = type;
	}

	public String getAgencyID()
  {
  	return mAgencyID;
  }

	public String getRouteID()
  {
  	return mRouteID;
  }

	public String getTripID()
  {
  	return mTripID;
  }

	public ScheduleType getType()
  {
  	return mType;
  }

	@Override
	public byte[] getCommandBytes()
	{
		byte[] bytes = new byte[] { COMMAND_CODE.getValue() };
		bytes = ByteArrayUtils.addAll(bytes, mAgencyID.getBytes());
		bytes = ByteArrayUtils.addAll(bytes, mRouteID.getBytes());
		bytes = ByteArrayUtils.addAll(bytes, mTripID.getBytes());
		byte[] tmp = new byte[] { mType.getValue() };
		bytes = ByteArrayUtils.addAll(bytes, tmp);

		return bytes;
	}

	@Override
	public CommandCode getCommandCode()
	{
		return COMMAND_CODE;
	}
}