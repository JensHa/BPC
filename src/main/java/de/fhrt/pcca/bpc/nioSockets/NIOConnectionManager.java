package de.fhrt.pcca.bpc.nioSockets;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.nioSockets.handler.input.NIODefaultInputhandler;
import de.fhrt.pcca.bpc.nioSockets.handler.input.NIOInputhandler;
import de.fhrt.pcca.bpc.nioSockets.handler.output.NIODefaultOutputhandler;
import de.fhrt.pcca.bpc.nioSockets.handler.output.NIOOutputhandler;
import de.fhrt.pcca.bpc.sockets.ConnectionManager;
import de.fhrt.pcca.bpc.sockets.Instance;

public class NIOConnectionManager implements ConnectionManager<NIOConnection, NIOInputhandler, NIOOutputhandler<?>>{
	private Instance instance;
	private ServerSocketChannel serverSocketChannel;
	private ArrayList<NIOConnection> connections = new ArrayList<>();
	private volatile boolean run;
	private int defaultPort = 3142;
	private NIOConnection server;
	private volatile Selector inputSelector;
	//avoids possible deadlock for the selector
	private final ReentrantLock selectorLock = new ReentrantLock();
	private Class<? extends NIOInputhandler> inputhandler;
	private Class<? extends NIOOutputhandler<?>> outputhandler;

	public NIOConnectionManager(Instance instance, int portToListenOn, Class<? extends NIOInputhandler> inputHandlerClass, Class<? extends NIOOutputhandler<?>> outputHandlerClass) {
		this.instance = instance;
		try{
			this.inputhandler = inputHandlerClass;
			this.outputhandler = outputHandlerClass;

		}catch(Exception e){
			e.printStackTrace();
		}
		if (portToListenOn < 0 || portToListenOn > 65535) {
			portToListenOn = defaultPort;
		}
		defaultPort = portToListenOn;
		try {
			this.inputSelector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
		run = true;
	}

	public NIOConnectionManager(Instance instance, Class<? extends NIOInputhandler> inputHandler, Class<? extends NIOOutputhandler<?>> outputHandler) {
		this(instance, -1, inputHandler, outputHandler);
	}

	public NIOConnectionManager(Instance instance) {
		this(instance, NIODefaultInputhandler.class, NIODefaultOutputhandler.class);
	}


	public void changeHandlersForConnections(Class<NIOInputhandler> inputHandlerClass, Class<NIOOutputhandler<?>> outputHandlerClass) {
		try{
			this.inputhandler = inputHandlerClass;
			this.outputhandler = outputHandlerClass;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public synchronized void shutdown() {
		run = false;
		for (NIOConnection con : connections) {
			con.close();
		}
		try {
			serverSocketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized NIOConnection createConnection(String ip, int port) {
		NIOConnection con = null;
		try {
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.connect(new InetSocketAddress(ip, port));
			con = new NIOConnection(socketChannel, this, inputhandler, outputhandler);
			connections.add(con);
			con.activateConnection();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			// Ugly but needed for the following race condition:
			// Two clients connect simultaneous to each other. One request will
			// be granted, the other gets a timeout.
			// If you receive a timeout you need to recheck (which normally
			// leads to an active connection)
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			return getConnection(ip);
		} catch (ConnectException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}

	public synchronized NIOConnection createConnection(String ip) {
		return createConnection(ip, defaultPort);
	}

	public synchronized NIOConnection createConnection(SocketChannel s) {
		NIOConnection con = new NIOConnection(s, this, inputhandler, outputhandler);
		connections.add(con);
		con.activateConnection();
		return con;
	}

	public synchronized void removeConnection(NIOConnection con) {
		this.connections.remove(con);
		con.close();
		Main.logger.info("Connection closed: " + con.getIP());
	}

	/**
	 * Search for an active connection to the specified client. If the
	 * connection already exists it will return it otherwise it will try to
	 * create one.
	 * 
	 * @param ip
	 *            to connect to
	 * @return The active connection or NULL if an exception occurred
	 */
	public synchronized NIOConnection getConnection(String ip) {
		for (NIOConnection connection : connections) {
			if (connection.getIP().equals(ip)) {
				return connection;
			}
		}
		// No active connection to that partner? No problem, just create one!
		return createConnection(ip);
	}

	public boolean isActive() {
		return this.run;
	}

	public Instance getInstance() {
		return this.instance;
	}

	public NIOConnection getServer() {
		return this.server;
	}

	public void startListening() {
		new NIOInputThread(this, defaultPort).start();
	}
	
	public Selector getInputSelector(){
		return this.inputSelector;
	}
	
	public ReentrantLock getSelectorLock() {
		return selectorLock;
	}

	public NIOConnection getConnection(SocketChannel client) {
		for(NIOConnection connection : connections){
			if(connection.getSocketChannel().equals(client)){
				return connection;
			}
		}
		return null;
	}

	public Class<? extends NIOOutputhandler<?>> getOutputhandler() {
		return outputhandler;
	}

	@Override
	public  void setServer(NIOConnection conToServer) {
		this.server = conToServer;
	}

	public Class<? extends NIOInputhandler> getInputhandler() {
		return inputhandler;
	}
}
