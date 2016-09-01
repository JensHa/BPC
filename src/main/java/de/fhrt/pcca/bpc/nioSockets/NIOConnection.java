package de.fhrt.pcca.bpc.nioSockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.nioSockets.handler.input.NIOInputhandler;
import de.fhrt.pcca.bpc.nioSockets.handler.output.NIOOutputhandler;
import de.fhrt.pcca.bpc.sockets.Connection;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.handler.output.Outputhandler;


public class NIOConnection implements Connection{
	private SocketChannel socketChannel;
	private volatile boolean active;
	private NIOConnectionManager myManager;
	private NIOInputhandler inputhandler;
	private NIOOutputhandler<?> outputhandler;
	private String ip;
	private int id;

	public NIOConnection(SocketChannel socketChannel, NIOConnectionManager myManager, Class<? extends NIOInputhandler> inputhandler, Class<? extends NIOOutputhandler<?>> outputhandler) {
		this.myManager = myManager;
		this.socketChannel=socketChannel;
		try{
			this.inputhandler=inputhandler.getConstructor(NIOConnection.class).newInstance(this);;
			this.outputhandler= outputhandler.getConstructor(NIOConnection.class).newInstance(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			this.ip=((InetSocketAddress)socketChannel.getRemoteAddress()).getAddress().getHostAddress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Set id of current client
		 */
		int i=0;
		List<String> instances = myManager.getInstance().getWorker().getInstances();
		if(instances!=null){
			for(String ipFromList : instances){
				if(ip.equals(ipFromList)){
					break;
				}
				i++;
			}
			id=i;
		}else{
			id=-1;
		}
		/*
		 *[/Set id of current client]
		 */
		
	}
	
	public void activateConnection(){
		try {
			socketChannel.configureBlocking(false);
			myManager.getSelectorLock().lock();
			try {
			    myManager.getInputSelector().wakeup();

			    socketChannel.register(myManager.getInputSelector(),SelectionKey.OP_READ);
			} finally {
				myManager.getSelectorLock().unlock();
			}
			active = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			Main.logger.info("Closing connection to: "+socketChannel.getRemoteAddress().toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		active = false;
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isActive() {
		return active;
	}
	public NIOConnectionManager getConnectionManager() {
		return myManager;
	}

	public SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	@Override
	public Outputhandler<?> getOutputhandler() {
		//while(!outputhandler.started()){}
		return outputhandler;		
	}

	public NIOInputhandler getInputhandler() {
		return inputhandler;
	}

	public String getIP() {
		return ip;
	}
	
	public int getId(){
		return id;
	}
}
