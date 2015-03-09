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

import java.util.HashMap;

/**
 * Frame
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/logcaster
 */
public class Frame
{
	protected String command;
	protected HashMap<String, String> headers = new HashMap<String, String>();
	protected String body;
	
	public Frame(String command, HashMap<String, String> headers, String body)
	{
		this.command = command;
		this.headers = headers;
		this.body = body;
	}

	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public HashMap<String, String> getHeaders()
	{
		return headers;
	}

	public void setHeaders(HashMap<String, String> headers)
	{
		this.headers = headers;
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}
}
