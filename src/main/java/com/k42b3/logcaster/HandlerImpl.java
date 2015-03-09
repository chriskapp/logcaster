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

import org.apache.log4j.Logger;

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
	protected Logger logger = Logger.getLogger("com.k42b3.logcaster");

	protected Overview controller;

	public HandlerImpl(Overview controller)
	{
		this.controller = controller;
	}

	@Override
	public void onReceive(Message message)
	{
		Color color;
		if(message.getColor() != null && !message.getColor().isEmpty())
		{
			color = Color.web(message.getColor());
		}
		else
		{
			color = Color.BLACK;
		}

		this.addMessage(message.getMessage(), color, message.isBold());
	}

	protected void addMessage(String message, Paint color, boolean bold)
	{
		LocalTime time = LocalTime.now();
		final String prefix = "[" + 
			fillZero(time.getHour(), 2) + ":" + 
			fillZero(time.getMinute(), 2) + ":" + 
			fillZero(time.getSecond(), 2) + "] ";

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

	protected String fillZero(int i, int len)
	{
		return String.format("%0" + len + "d", i);
	}
}
