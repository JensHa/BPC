package de.fhrt.pcca.bpc.sockets;

import de.fhrt.pcca.bpc.sockets.handler.input.Inputhandler;
import de.fhrt.pcca.bpc.sockets.handler.output.Outputhandler;

public interface ConnectionManager<A extends Connection, B extends Inputhandler, C extends Outputhandler<?>>{
	
	public void changeHandlersForConnections(Class<B> inputHandler, Class<C> outputHandler);
	public void shutdown();
	public A createConnection(String ip, int port);
	public A createConnection(String ip);
	public void startListening();
	public void setServer(A connection);
	public A getServer();
	public boolean isActive();
	public A getConnection(String string);
}
