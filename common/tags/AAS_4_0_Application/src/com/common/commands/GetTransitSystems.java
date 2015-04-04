package com.common.commands;


public class GetTransitSystems extends SerializableCommand
{
	private static final long serialVersionUID = -2070047504423009671L;
	private static final CommandCode COMMAND_CODE = 
		CommandCode.GET_TRANSIT_SYSTEM;

	public GetTransitSystems()
	{
	}

	@Override
	public byte[] getCommandBytes()
	{
		return new byte[] { COMMAND_CODE.getValue() };
	}

	@Override
	public CommandCode getCommandCode()
	{
		return COMMAND_CODE;
	}
}