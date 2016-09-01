package de.fhrt.pcca.bpc.nioSockets.handler.input;

import java.io.InputStream;
import java.nio.ByteBuffer;

import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.nioSockets.handler.input.messageReader.MessageReader;
import de.fhrt.pcca.bpc.sockets.handler.input.Inputhandler;

public abstract class NIOInputhandler implements Inputhandler {
	protected NIOConnection connection;
	protected MessageReader<?> messageReader;
	protected String commandReceived; 
	protected String messageType;



	public NIOInputhandler(NIOConnection connection) {
		this.connection = connection;
	}

	public abstract void read(ByteBuffer buffer);
}
