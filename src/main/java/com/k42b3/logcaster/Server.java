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
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.google.gson.Gson;

/**
 * Server
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/logcaster
 */
public class Server extends IoHandlerAdapter
{
	protected Logger logger = Logger.getLogger("com.k42b3.logcaster");
	protected Gson parser = new Gson();
	
	protected int port;
	protected Handler handler;

	public Server(int port, Handler handler)
	{
		this.port = port;
		this.handler = handler;
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception
	{
		logger.error(cause.getMessage(), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object request) throws Exception
	{
		Message message = this.parser.fromJson(request.toString(), Message.class);
		if(message != null)
		{
			handler.onReceive(message);
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception
	{
		logger.info("Idle " + session.getIdleCount(status));
	}

	public static IoAcceptor start(int port, Handler handler) throws IOException
	{
		TextLineCodecFactory tlcf = new TextLineCodecFactory(Charset.forName("UTF-8"));
		tlcf.setDecoderMaxLineLength(8192);

        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(tlcf));
        acceptor.setHandler(new Server(port, handler));
        acceptor.getSessionConfig().setReadBufferSize(8192);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        acceptor.bind(new InetSocketAddress(port));

        return acceptor;
	}
}

