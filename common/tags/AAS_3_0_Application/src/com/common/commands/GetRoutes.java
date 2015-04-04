package com.common.commands;

import com.common.utilities.ByteArrayUtils;

public class GetRoutes extends SerializableCommand
{
	private static final long serialVersionUID = 1268087841121778725L;
	private static final CommandCode COMMAND_CODE = CommandCode.GET_ROUTES;
	private String mAgencyID;

	public GetRoutes(String agencyID)
	{
		mAgencyID = agencyID;
	}

	public String getAgencyID()
	{
		return mAgencyID;
	}

	@Override
	public byte[] getCommandBytes()
	{
		byte[] bytes = new byte[] { COMMAND_CODE.getValue() };
		bytes = ByteArrayUtils.addAll(bytes, mAgencyID.getBytes());
		
		return bytes;
	}

	@Override
	public CommandCode getCommandCode()
	{
		return COMMAND_CODE;
	}
}