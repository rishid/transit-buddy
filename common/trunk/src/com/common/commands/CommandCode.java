package com.common.commands;

import java.io.Serializable;
import java.util.TreeMap;

public enum CommandCode implements Serializable
{
    INVALID_COMMAND((byte)0x00),
    GET_TRANSIT_SYSTEM((byte)0x01),
    GET_ROUTES((byte)0x02),
    GET_TRIPS((byte)0x03),
    GET_STOPS((byte)0x04),
    GET_NEARBY_STOPS((byte)0x05);
    
    private static TreeMap<Byte, CommandCode> _map;
    private byte _value;
    
    static
    {
        _map = new TreeMap<Byte, CommandCode>();
        for(CommandCode code : CommandCode.values())
        {
            _map.put(code.getValue(), code);
        }
    }
    
    CommandCode(byte value)
    {
        _value = value;
    }
    
    public byte getValue()
    {
        return _value;
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    
    public static CommandCode lookup(byte value)
    {
        return _map.get(new Byte(value));
    }
    
    public static int getSize()
    {
    	return _map.size();
    }
}
