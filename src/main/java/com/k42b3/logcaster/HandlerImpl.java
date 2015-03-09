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

import java.time.LocalTime;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import com.google.gson.Gson;
import com.k42b3.logcaster.controller.Overview;

/**
 * HandlerImpl
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/logcaster
 */
public class HandlerImpl implements Handler
{
	protected Gson parser = new Gson();
	protected Overview controller;

	public HandlerImpl(Overview controller)
	{
		this.controller = controller;
	}

	@Override
	public void onStart()
	{
		this.addMessage("Server started", 0);
	}

	@Override
	public void onSend(Frame frame, int connectionId)
	{
		Message message = this.parser.fromJson(frame.getBody(), Message.class);

		if(message.getTitle() != null && !message.getTitle().isEmpty())
		{
			this.addMessage(message.getTitle(), connectionId, Color.BLACK, true);
		}

		if(message.getMessage() != null && !message.getMessage().isEmpty())
		{
			this.addMessage(message.getMessage(), connectionId, message.isSuccess() ? Color.BLUE : Color.RED, false);
		}

		if(message.getTrace() != null && !message.getTrace().isEmpty())
		{
			this.addMessage(message.getTrace(), connectionId);
		}

		if(message.getContext() != null && !message.getContext().isEmpty())
		{
			this.addMessage(message.getContext(), connectionId);
		}
	}

	@Override
	public void onDisconnect(Frame frame, int connectionId)
	{
		this.addMessage("Client disconnected", connectionId);
	}

	@Override
	public void onConnect(Frame frame, int connectionId, String clientIp)
	{
		this.addMessage("Client " + clientIp + " connected", connectionId);
	}

	protected void addMessage(String message, int connectionId)
	{
		addMessage(message, connectionId, Color.BLACK, false);
	}

	protected void addMessage(String message, int connectionId, Paint color, boolean bold)
	{
		LocalTime time = LocalTime.now();
		final String prefix = fillZero(connectionId) + "-[" + fillZero(time.getHour()) + ":" + fillZero(time.getMinute()) + ":" + fillZero(time.getSecond()) + "] ";

		Platform.runLater(new Runnable(){

			@Override
			public void run()
			{
				Text text = new Text(prefix + message);
				text.setFontSmoothingType(FontSmoothingType.LCD);
				text.setFill(color);
				if(bold)
				{
					text.setStyle("-fx-font-weight:bold;");
				}

				controller.addMessage(text);
			}

		});
	}
	
	protected String fillZero(int i)
	{
		return i < 10 ? "0" + i : "" + i;
	}
}
