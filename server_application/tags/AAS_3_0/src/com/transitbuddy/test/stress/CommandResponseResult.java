package com.transitbuddy.test.stress;

import com.common.commands.CommandCode;
import com.common.utilities.ResponseResult;

public class CommandResponseResult
{
	private ResponseResult mResult;
	private CommandCode mCommandCode;
	
	public CommandResponseResult(CommandCode commandCode, ResponseResult result)
	{
		mCommandCode = commandCode;
		mResult = result;
	}

	public ResponseResult getResponseResult()
  {
  	return mResult;
  }
	

	public CommandCode getCommandCode()
  {
  	return mCommandCode;
  }
}
