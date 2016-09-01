package de.fhrt.pcca.bpc.basicSockets;

import java.io.IOException;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.handler.input.BasicSocketsDefaultInputhandler;
import de.fhrt.pcca.bpc.basicSockets.handler.input.BasicSocketsInputhandler;
import de.fhrt.pcca.bpc.basicSockets.handler.output.BasicSocketsDefaultOutputhandler;
import de.fhrt.pcca.bpc.basicSockets.handler.output.BasicSocketsOutputhandler;
import de.fhrt.pcca.bpc.sockets.ConnectionManager;
import de.fhrt.pcca.bpc.sockets.Instance;

public class BasicSocketsConnectionManager implements ConnectionManager<BasicSocketsConnection, BasicSocketsInputhandler, BasicSocketsOutputhandler<?>> {
	private Instance instance;
	private ServerSocket serverSocket;
	private ArrayList<BasicSocketsConnection> connections = new ArrayList<>();
	private volatile boolean run;
	private Class<? extends BasicSocketsInputhandler> inputHandlerForAllConnections;
	private Class<? extends BasicSocketsOutputhandler<?>> outputHandlerForAllConnections;
	private int defaultPort = 3142;
	private BasicSocketsConnection server;

	public BasicSocketsConnectionManager(Instance instance, int portToListenOn,
			Class<? extends BasicSocketsInputhandler> inputHandler,
			Class<? extends BasicSocketsOutputhandler<?>> outputHandler) {
		this.instance = instance;
		this.inputHandlerForAllConnections = inputHandler;
		this.outputHandlerForAllConnections = outputHandler;
		if (portToListenOn < 0 || portToListenOn > 65535) {
			portToListenOn = defaultPort;
		}
		defaultPort = portToListenOn;
		run = true;
	}

	public BasicSocketsConnectionManager(Instance instance, Class<? extends BasicSocketsInputhandler> inputHandler,	Class<? extends BasicSocketsOutputhandler<?>> outputHandler) {
		this(instance, -1, inputHandler, outputHandler);
	}

	public BasicSocketsConnectionManager(Instance instance) {
		this(instance, BasicSocketsDefaultInputhandler.class, BasicSocketsDefaultOutputhandler.class);
	}

	public synchronized void shutdown() {
		run = false;
		for (BasicSocketsConnection con : connections) {
			con.close();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized BasicSocketsConnection createConnection(String ip, int port) {
		BasicSocketsConnection con = null;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), 2000);
			con = new BasicSocketsConnection(socket, this, inputHandlerForAllConnections,
					outputHandlerForAllConnections);
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
		}

		return con;
	}

	public synchronized BasicSocketsConnection createConnection(String ip) {
		return createConnection(ip, defaultPort);
	}

	public synchronized BasicSocketsConnection createConnection(Socket s) {
		BasicSocketsConnection con = new BasicSocketsConnection(s, this, inputHandlerForAllConnections,
				outputHandlerForAllConnections);
		connections.add(con);
		con.activateConnection();
		return con;
	}

	public synchronized void removeConnection(BasicSocketsConnection con) {
		this.connections.remove(con);
		con.close();
		Main.logger.info("Connection closed: " + con.getSocket().getRemoteSocketAddress().toString());
	}

	public void startListening() {
		try {
			serverSocket = new ServerSocket(defaultPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			Socket socket = null;

			public void run() {
				while (run) {
					try {
						socket = serverSocket.accept();
						Main.logger.info("New incomming connection: " + socket.getRemoteSocketAddress().toString());
						createConnection(socket);
					} catch (IOException e) {
						if (run) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
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
	public synchronized BasicSocketsConnection getConnection(String ip) {
		for (BasicSocketsConnection connection : connections) {
			if (connection.getSocket().getInetAddress().getHostAddress().equals(ip)) {
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

	public BasicSocketsConnection getServer() {
		return this.server;
	}

	@Override
	public void changeHandlersForConnections(Class<BasicSocketsInputhandler> inputHandler,Class<BasicSocketsOutputhandler<?>> outputHandler) {
		this.inputHandlerForAllConnections =  inputHandler;
		this.outputHandlerForAllConnections =  outputHandler;
	}

	@Override
	public void setServer(BasicSocketsConnection conToServer) {
		this.server=conToServer;
	}
}
