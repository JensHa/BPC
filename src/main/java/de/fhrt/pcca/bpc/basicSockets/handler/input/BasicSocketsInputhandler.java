package de.fhrt.pcca.bpc.basicSockets.handler.input;

import java.io.IOException;
import java.io.InputStream;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.sockets.handler.input.Inputhandler;

public abstract class BasicSocketsInputhandler extends Thread implements Inputhandler{
	protected BasicSocketsConnection connection;
	protected InputStream inputStream;
	protected boolean streamActive;
	protected String messageType;
	protected String commandReceived; 

	public BasicSocketsInputhandler(BasicSocketsConnection connection) {
		this.connection = connection;
	    this.setName("IN: "+connection.getSocket().getInetAddress().getHostAddress()+" - "+this.getName());
	}

	@Override
	public void start() {
		prepare();
		super.start();
	}
	
	protected void prepare(){
		try {
			this.inputStream = connection.getSocket().getInputStream();
			streamActive = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		Main.logger.info("Inputthread started " + connection.getSocket().getRemoteSocketAddress().toString());
		read();
		//if this point is reached then the stream is at the end
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.logger.info(this.getClass().getSimpleName() + ": Stream closed " + connection.getSocket().getRemoteSocketAddress().toString());
		connection.getConnectionManager().removeConnection(connection);
	}
	
	protected abstract void read();
}
