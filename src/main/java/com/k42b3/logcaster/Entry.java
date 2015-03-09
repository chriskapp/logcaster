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

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.mina.core.service.IoAcceptor;

import com.k42b3.logcaster.controller.Overview;

/**
 * Entry
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/logcaster
 */
public class Entry extends Application
{
	protected Logger logger = Logger.getLogger("com.k42b3.logcaster");

	private Scene scene;
	private IoAcceptor server;
	
	@Override
    public void start(Stage primaryStage) throws IOException
	{
		Logger.getLogger("com.k42b3.logcaster").setLevel(Level.INFO);
		Logger.getLogger("com.k42b3.logcaster").addAppender(new ConsoleAppender(new PatternLayout()));

		FXMLLoader loader = new FXMLLoader();
		Parent root = loader.load(getClass().getResource("/overview.fxml").openStream());
		final Overview controller = loader.getController();

        scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("/default.css");

        primaryStage.setTitle("LogCaster v" + LogCaster.VERSION);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent e)
			{
				if(server != null)
				{
					server.dispose();
				}
			}

		});
        primaryStage.show();

        // start server
        Task<Void> task = new Task<Void>(){

			@Override
			protected Void call()
			{
				try
				{
					HandlerImpl handler = new HandlerImpl(controller);
			        server = Server.start(61613, handler);

			        // welcome message
					handler.onReceive(new Message("LogCaster v" + LogCaster.VERSION + " started", "#060"));
				}
				catch(IOException e)
				{
					logger.error(e.getMessage(), e);
				}

		        return null;
			}

        };

        new Thread(task).start();
    }

	public static void main(String[] args) 
	{
		launch(args);
	}
}
