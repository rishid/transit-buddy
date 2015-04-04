package com.transitbuddy.query;

import com.transitbuddy.main.updator.PredictionTimes;

public class RealTimeUpdate extends Update
{
	protected PredictionTimes mPredictionTime;

	public RealTimeUpdate()
	{
	}

	public RealTimeUpdate(String stmt)
	{
		super(stmt);
	}

	public PredictionTimes getmPredictionTimes()
	{
		return mPredictionTime;
	}

	public void setmPredictionTimes(PredictionTimes mPredictionTime)
	{
		this.mPredictionTime = mPredictionTime;
	}
}
