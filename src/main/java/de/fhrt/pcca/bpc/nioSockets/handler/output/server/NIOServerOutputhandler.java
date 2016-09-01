package de.fhrt.pcca.bpc.nioSockets.handler.output.server;

import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.nioSockets.handler.output.NIOOutputhandler;
import de.fhrt.pcca.bpc.sockets.handler.output.server.ServerOutputhandler;

public abstract class NIOServerOutputhandler<T> extends NIOOutputhandler<T> implements ServerOutputhandler{
	public NIOServerOutputhandler(NIOConnection connection) {
		super(connection);
	}
}
