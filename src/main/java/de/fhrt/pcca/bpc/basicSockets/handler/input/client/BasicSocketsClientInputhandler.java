package de.fhrt.pcca.bpc.basicSockets.handler.input.client;


import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.basicSockets.handler.input.BasicSocketsInputhandler;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.handler.input.client.ClientInputhandler;

public abstract class BasicSocketsClientInputhandler extends BasicSocketsInputhandler implements ClientInputhandler{
	protected ClientWorker worker;

	public BasicSocketsClientInputhandler(BasicSocketsConnection connection) {
		super(connection);
		worker =  (ClientWorker)connection.getConnectionManager().getInstance().getWorker();
	}
	
	@Override
	public void receiveStealrequest(){
		while(!worker.isRunning()){}
	}
}
