package com.transitbuddy.query;

import com.common.commands.ICommand;

public abstract class CommandableQuery extends Query
{

	protected ICommand mCommand;
	
	public CommandableQuery()
	{	
	}
	
	public CommandableQuery(String stmt)
	{
		super(stmt);
	}

	public void setCommand(ICommand command)throws IllegalArgumentException
	{
		// Reset all values of the query
		reset();
		
		// Validate the command
		validateCommandAndSetQueryValues(command);
		
		// Set the command in the query
		mCommand = command;
	}
	
	/**
	 * Validates the command and its values. If there are no errors, then this
	 * method also sets the values of the query.
	 * @param command The command containing a query request
	 * @throws IllegalArgumentException - if any of the values in the command are
	 * invalid
	 */
	public abstract void validateCommandAndSetQueryValues(ICommand command) 
	throws IllegalArgumentException;
}
