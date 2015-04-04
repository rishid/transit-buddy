package com.common.utilities;

public class ResponseResult
{
	public enum ResponseStatus
	{
		Completed,
		TimedOut,
		Failed
	};

	private ResponseStatus mStatus;
	private String mMessage;
	private Object mResultData;

	public ResponseResult(ResponseStatus status)
	{
		mStatus = status;
	}

	public ResponseResult(ResponseStatus status, String message,
			Object resultData)
	{
		mStatus = status;
		mMessage = message;
		mResultData = resultData;
	}

	public ResponseResult(ResponseStatus status, String message)
	{
		mStatus = status;
		mMessage = message;
	}

	public Object getResultData()
	{
		return mResultData;
	}

	public String getMessage()
	{
		return mMessage;
	}

	public ResponseStatus getResponseStatus()
	{
		return mStatus;
	}
}