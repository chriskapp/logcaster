LogCaster
=========

## About

A simple server which displays incoming messages for debugging. The server is
build with MINA (https://mina.apache.org/). To send an message simply connect to 
127.0.0.1:61613 and write an JSON string to the socket. The JSON payload must 
have the following structure:

	{
		"color": "#000",
		"bold": false,
		"message": "Message of the error"
	}

The tool requires Java 8.
