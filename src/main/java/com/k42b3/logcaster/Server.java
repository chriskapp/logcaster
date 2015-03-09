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

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * Server
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/logcaster
 */
public class Server
{
	protected Logger logger = Logger.getLogger("com.k42b3.logcaster");
	
	protected boolean run = true;
	protected int port;
	protected Handler handler;
	protected ExecutorService pool;
	protected ServerSocket socket;

	protected static int connectionId = 0;

	public Server(int port, Handler handler)
	{
		this.port = port;
		this.handler = handler;
		this.pool = Executors.newFixedThreadPool(8);
	}

	public void close()
	{
		this.run = false;

		this.pool.shutdown();

		try
		{
			if(socket != null)
			{
				socket.close();
			}
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	public void run()
	{
		try
		{
			socket = new ServerSocket(port);
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
			return;
		}

		handler.onStart();

		while(this.run)
		{
			try
			{
				this.pool.execute(new Worker(socket.accept(), handler, ++connectionId));
			}
			catch(IOException e)
			{
				logger.error(e.getMessage(), e);
			}

			try
			{
				Thread.sleep(200);
			}
			catch(InterruptedException e)
			{
				logger.error(e.getMessage(), e);
			}
		}
	}
}

