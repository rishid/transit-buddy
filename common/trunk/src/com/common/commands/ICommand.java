package com.common.commands;

public interface ICommand
{
	public byte[] getCommandBytes();
	public CommandCode getCommandCode();
}