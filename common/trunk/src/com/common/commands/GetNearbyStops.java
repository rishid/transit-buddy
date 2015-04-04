package com.common.commands;

import com.common.types.Coordinate;

public class GetNearbyStops extends SerializableCommand
{
  private static final long serialVersionUID = -1786683810904232548L;
	private static final CommandCode COMMAND_CODE = CommandCode.GET_NEARBY_STOPS;
	private int mVicinityMeters;
  private int mMaxStops;
	private Coordinate mCoordinate;

	public GetNearbyStops(int vicinityMeters, Coordinate c, int maxStops)
	{
		mVicinityMeters = vicinityMeters;
		mCoordinate = c;
		mMaxStops = maxStops;
	}
	
	public int getVicinityMeters()
  {
  	return mVicinityMeters;
  }
	
	public int getMaxStops()
  {
  	return mMaxStops;
  }

	public Coordinate getCoordinate()
  {
  	return mCoordinate;
  }

	@Override
	public byte[] getCommandBytes()
	{
		return new byte[] {COMMAND_CODE.getValue(),
				Integer.valueOf(mVicinityMeters).byteValue(),
				Float.valueOf(mCoordinate.getLatitude()).byteValue(),
				Float.valueOf(mCoordinate.getLongitude()).byteValue()  };
	}

	@Override
	public CommandCode getCommandCode()
	{
		return CommandCode.GET_NEARBY_STOPS;
	}
}