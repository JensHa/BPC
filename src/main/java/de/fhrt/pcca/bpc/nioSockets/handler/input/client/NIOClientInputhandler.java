package de.fhrt.pcca.bpc.nioSockets.handler.input.client;

import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.nioSockets.handler.input.NIOInputhandler;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.handler.input.client.ClientInputhandler;

public abstract class NIOClientInputhandler extends NIOInputhandler implements ClientInputhandler{
	protected ClientWorker worker;

	public NIOClientInputhandler(NIOConnection connection) {
		super(connection);
	}
	
	@Override
	public void receiveStealrequest(){
		while(!worker.isRunning()){}
	}
}
