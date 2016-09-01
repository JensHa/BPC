package de.fhrt.pcca.bpc.nioSockets.handler.input.server;

import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.nioSockets.handler.input.NIOInputhandler;
import de.fhrt.pcca.bpc.sockets.handler.input.server.ServerInputhandler;
import de.fhrt.pcca.bpc.sockets.server.SocketSearchServer;

public abstract class NIOServerInputhandler extends NIOInputhandler implements ServerInputhandler{
	protected SocketSearchServer worker;

	public NIOServerInputhandler(NIOConnection connection) {
		super(connection);
		this.worker = (SocketSearchServer) connection.getConnectionManager().getInstance().getWorker();
	}
}
