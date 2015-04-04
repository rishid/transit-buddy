package com.transitbuddy.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.common.commands.ICommand;

public abstract class CommandableQuery extends Query
{

	private ICommand mCommand;
	
	public CommandableQuery()
	{
		
	}
	
	public CommandableQuery(String stmt)
	{
		super(stmt);
	}

	public void setCommand(ICommand command)
	{
		mCommand = command;
	}
	
	@Override
	public Serializable getAnswer(ResultSet rs) throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
