package de.fhrt.pcca.bpc.basicSockets.handler.input.server;

import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.basicSockets.handler.input.BasicSocketsInputhandler;
import de.fhrt.pcca.bpc.sockets.handler.input.server.ServerInputhandler;
import de.fhrt.pcca.bpc.sockets.server.SocketSearchServer;

public abstract class BasicSocketsServerInputhandler extends BasicSocketsInputhandler implements ServerInputhandler{
	protected SocketSearchServer worker;

	public BasicSocketsServerInputhandler(BasicSocketsConnection connection) {
		super(connection);
		this.worker = (SocketSearchServer) connection.getConnectionManager().getInstance().getWorker();
	}
}
