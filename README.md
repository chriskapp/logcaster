LogCaster
=========

## About

A simple server which displays incoming messages for debugging. The server uses
the STOMP protocol to receive messages. Every incoming message gets displayed in 
an list. The message body must contain an JSON payload with the following 
structure:

	{
		"success": false
		"title": "Error title i.e. the exception class",
		"message": "Message of the error",
		"trace": "Stacktrace of the error",
		"context": "Context of the file where the error occured"
	}

An application can write all log messages directly to the LogCaster server 
instead of an file so you can see every message directly. The server listens on 
the default STOMP port 61613. The tool requires Java 8.

To send a message you can use an STOMP client or write the message on the socket
by your self. More informations about the STOMP protocol at
https://stomp.github.io/stomp-specification-1.2.html
