package com.transitbuddy.query;

import com.transitbuddy.main.updator.PredictionTimes;

public class InsertRealTimeUpdate extends Update
{
	protected PredictionTimes mPredictionTime;

	/**
	 * Default Constructor
	 */
	public InsertRealTimeUpdate()
	{
		mPredictionTime = null;
	}

	public PredictionTimes getmPredictionTimes()
	{
		return mPredictionTime;
	}

	public void setPredictionTimes(PredictionTimes mPredictionTime)
	{
		this.mPredictionTime = mPredictionTime;
	}
}
