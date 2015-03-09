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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Worker
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/logcaster
 */
public class Worker implements Runnable
{
	protected Logger logger = Logger.getLogger("com.k42b3.logcaster");

	protected Socket client;
	protected Handler handler;
	protected int connectionId;

	private boolean run = true;

	public Worker(Socket client, Handler handler, int connectionId)
	{
		this.client = client;
		this.handler = handler;
		this.connectionId = connectionId;

		logger.info("Accepted connection");
	}

	@Override
	public void run()
	{
		while(run)
		{
			try
			{
				if(client.getInputStream().available() == 0)
				{
					try
					{
						Thread.sleep(200);
					}
					catch(InterruptedException e)
					{
						logger.error(e.getMessage(), e);
					}
					continue;
				}

				logger.info("Data available");

				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

				Frame requestFrame = parseFrame(reader);

				logger.info("Received frame " + requestFrame.getCommand());

				if(requestFrame.getCommand().equals("CONNECT") || requestFrame.getCommand().equals("STOMP"))
				{
					handler.onConnect(requestFrame, connectionId, client.getInetAddress().getHostAddress());

					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("version", "1.2");
					headers.put("heart-beat", "0,0");
					headers.put("server", "logcaster/" + LogCaster.VERSION);

					Frame responseFrame = new Frame("CONNECTED", headers, "");
					
					this.sendFrame(writer, responseFrame);
				}
				else if(requestFrame.getCommand().equals("DISCONNECT"))
				{
					handler.onDisconnect(requestFrame, connectionId);

					run = false;
				}
				else if(requestFrame.getCommand().equals("SEND"))
				{
					handler.onSend(requestFrame, connectionId);
				}
			}
			catch(IOException e)
			{
				logger.error(e.getMessage(), e);
				break;
			}
		}

		try
		{
			client.close();
		}
		catch(IOException e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	protected Frame parseFrame(BufferedReader reader) throws IOException
	{
		String frame = readFrame(reader);
		String command = "";
		HashMap<String, String> headers = new HashMap<String, String>();
		StringBuilder body = new StringBuilder();
		
		String[] parts = frame.split("\n");
		boolean inHeader = true;

		for(int i = 0; i < parts.length; i++)
		{
			if(i == 0)
			{
				command = parts[i].toUpperCase();
			}
			else if(inHeader)
			{
				String header = parts[i].trim();
				if(!header.isEmpty())
				{
					String[] headerPart = header.split(":", 2);
					String key = null;
					String value = null;
					
					if(headerPart.length == 2)
					{
						key = headerPart[0];
						value = headerPart[1].trim();
					}
					else if(headerPart.length == 1)
					{
						key = headerPart[0];
					}
					
					if(key != null)
					{
						headers.put(key.toLowerCase(), value);
					}
				}
				else
				{
					inHeader = false;
				}
			}
			else
			{
				body.append(parts[i]);
			}
		}

		return new Frame(command, headers, body.toString());
	}

	protected String readFrame(BufferedReader reader) throws IOException
	{
		logger.info("Read frame");

		StringBuilder frame = new StringBuilder();
		
		int i;
		while((i = reader.read()) != -1)
		{
			char c = (char) i;
			if(c == '\0')
			{
				break;
			}

			frame.append(c);
		}

		return frame.toString();
	}

	protected void sendFrame(PrintWriter writer, Frame frame)
	{
		logger.info("Write frame");

		writer.write(frame.getCommand() + "\n");

		Iterator<Entry<String, String>> it = frame.getHeaders().entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, String> header = it.next();

			writer.write(header.getKey() + ":" + header.getValue() + "\n");
		}

		writer.write("\n");
		writer.write(frame.getBody() + "\0\n");
		writer.flush();
	}
}
