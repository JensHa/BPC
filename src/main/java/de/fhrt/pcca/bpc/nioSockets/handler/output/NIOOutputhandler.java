package de.fhrt.pcca.bpc.nioSockets.handler.output;

import java.nio.ByteBuffer;

import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.sockets.handler.output.Outputhandler;

public abstract class NIOOutputhandler<T> implements Outputhandler<T> {
	protected NIOConnection connection;
	protected ByteBuffer buffer;

	public NIOOutputhandler(NIOConnection connection) {
		this.connection = connection;
		this.buffer=ByteBuffer.allocate(2048);
	}
}
