package com.common.utilities;

import java.nio.ByteBuffer;

public class ByteArrayUtils
{

	public static byte[] intToBytes(final int i)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(i);
		return bb.array();
	}
	
	public static byte[] addAll(byte[] first, byte[] second)
	{
		byte[] ret = new byte[first.length + second.length];
		System.arraycopy(first, 0, ret, 0, first.length);
		System.arraycopy(second, 0, ret, first.length, ret.length); 
		
		return ret;
	}

}