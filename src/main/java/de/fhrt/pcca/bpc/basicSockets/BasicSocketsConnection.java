package de.fhrt.pcca.bpc.basicSockets;

import java.io.IOException;
import java.net.Socket;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.handler.input.BasicSocketsInputhandler;
import de.fhrt.pcca.bpc.basicSockets.handler.output.BasicSocketsOutputhandler;
import de.fhrt.pcca.bpc.sockets.Connection;


public class BasicSocketsConnection implements Connection{
	private Socket socket;
	private BasicSocketsInputhandler inputhandler;
	private BasicSocketsOutputhandler<?> outputhandler;
	private volatile boolean active;
	private BasicSocketsConnectionManager myManager;
	private  Class<? extends BasicSocketsInputhandler> templateInputHandler;
	private  Class<? extends BasicSocketsOutputhandler<?>> templateOutputHandler;

	public BasicSocketsConnection(Socket socket, BasicSocketsConnectionManager myManager,  Class<? extends BasicSocketsInputhandler> templateInputhandler,  Class<? extends BasicSocketsOutputhandler<?>> templateOutputhandler) {
		this.myManager = myManager;
		this.templateInputHandler=templateInputhandler;
		this.templateOutputHandler=templateOutputhandler;
		this.socket=socket;
	}
	
	public void activateConnection(){
		try {
			inputhandler=  templateInputHandler.getConstructor(BasicSocketsConnection.class).newInstance(this);
			outputhandler= templateOutputHandler.getConstructor(BasicSocketsConnection.class).newInstance(this);
			inputhandler.start();
			outputhandler.start();
			active = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public void close() {
		Main.logger.info("Closing connection to: "+socket.getRemoteSocketAddress().toString());
		active = false;
		inputhandler.stop();
		outputhandler.stop();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isActive() {
		return active;
	}

	public BasicSocketsOutputhandler<?> getOutputhandler() {
		//only send if the outputhandler (and thus the stream) is started
		while(!outputhandler.started()){}
		return this.outputhandler;
	}

	public BasicSocketsConnectionManager getConnectionManager() {
		return myManager;
	}

	public Socket getSocket() {
		return this.socket;
	}
	
}
