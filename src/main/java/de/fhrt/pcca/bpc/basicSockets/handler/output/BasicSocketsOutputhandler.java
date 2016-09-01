package de.fhrt.pcca.bpc.basicSockets.handler.output;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.sockets.handler.output.Outputhandler;

public abstract class BasicSocketsOutputhandler<T> extends Thread implements Outputhandler<T>{
	protected OutputStream outputStream;
	volatile protected boolean started;
	protected volatile LinkedList<T> outputBuffer;
	protected BasicSocketsConnection connection;
	
	public BasicSocketsOutputhandler(BasicSocketsConnection connection) {
		this.connection=connection;
		this.outputBuffer=new LinkedList<>();
		this.setName("OUT: " + connection.getSocket().getInetAddress().getHostAddress() + " - " + this.getName());
	}
	
	@Override
	public synchronized void start() {
		prepare();
		super.start();
	}

	/**
	 * Prepares the thread for communication, e.g. open the stream, create writers etc.
	 */
	protected void prepare(){
		try {
			this.outputStream=connection.getSocket().getOutputStream();
			this.started=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean started(){
		return this.started;
	}
	
	public void close(){
		try {
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.logger.info( this.getClass().getSimpleName()+": Stream closed " + connection.getSocket().getRemoteSocketAddress().toString());
	}
	
	protected void addToOutputbuffer(T o) {
		synchronized (outputBuffer) {
			this.outputBuffer.add(o);
			outputBuffer.notify();
		}
	}
	
	@Override
	public void run() {
		Main.logger.info("Outputthread started " + connection.getSocket().getRemoteSocketAddress().toString());
		while (connection.isActive()) {
			synchronized (outputBuffer) {
				while (outputBuffer.isEmpty()) {
					try {
						outputBuffer.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				write(outputBuffer.remove());
			}
		}
	}

}
