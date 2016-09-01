package de.fhrt.pcca.bpc.basicSockets.handler.output.server;

import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.basicSockets.handler.output.BasicSocketsOutputhandler;
import de.fhrt.pcca.bpc.sockets.handler.output.server.ServerOutputhandler;

public abstract class BasicSocketsServerOutputhandler<T> extends BasicSocketsOutputhandler<T> implements ServerOutputhandler{
	public BasicSocketsServerOutputhandler(BasicSocketsConnection connection) {
		super(connection);
	}
}
