/**
 * logcaster
 * A simple server which displays incoming messages for debugging.
 * 
 * Copyright (c) 2015 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of logcaster. logcaster is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * logcaster is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with logcaster. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.logcaster;

/**
 * Message
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/logcaster
 */
public class Message
{
	protected String color;
	protected boolean bold;
	protected String message;
	
	public Message(String message, String color, boolean bold)
	{
		this.message = message;
		this.color = color;
		this.bold = bold;
	}

	public Message(String message, String color)
	{
		this(message, color, false);
	}

	public Message(String message)
	{
		this(message, "#000");
	}

	public String getColor()
	{
		return color;
	}
	
	public void setColor(String color)
	{
		this.color = color;
	}
	
	public boolean isBold()
	{
		return bold;
	}
	
	public void setBold(boolean bold)
	{
		this.bold = bold;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
}
