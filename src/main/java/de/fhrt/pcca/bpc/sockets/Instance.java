package de.fhrt.pcca.bpc.sockets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.nioSockets.NIOConnectionManager;
import de.fhrt.pcca.bpc.nioSockets.handler.input.client.NIOClientInputhandlerJSON;
import de.fhrt.pcca.bpc.nioSockets.handler.input.server.NIOServerInputhandlerJSON;
import de.fhrt.pcca.bpc.nioSockets.handler.output.client.NIOClientOutputhandlerJSON;
import de.fhrt.pcca.bpc.nioSockets.handler.output.server.NIOServerOutputhandlerJSON;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.SocketSearchClient;
import de.fhrt.pcca.bpc.sockets.handler.output.client.ClientOutputhandler;
import de.fhrt.pcca.bpc.sockets.server.ServerDialog;
import de.fhrt.pcca.bpc.sockets.server.SocketSearchServer;

public class Instance {
	private volatile int instanceID;
	private Worker worker;
	//TODO:private Loadbalancer balancer;
	@SuppressWarnings("rawtypes")
	private volatile ConnectionManager manager;
	private String serverIP;
	private long result;
	private boolean server;
	private volatile boolean running;

	public Instance(String serverIP) {
		this.serverIP=serverIP;
		server=false;
	}
	
	public Instance(){
		server=true;
	}
	@SuppressWarnings("unchecked")
	public long start(){
		this.running=true;
		if(server){
			worker = new SocketSearchServer(this);
			try {
				Main.logger.info("Starting managment-server. IP " +InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			manager = new NIOConnectionManager(this, NIOServerInputhandlerJSON.class, NIOServerOutputhandlerJSON.class);
			//manager = new BasicSocketsConnectionManager(this, BasicSocketsServerInputhandlerJSON.class, BasicSocketsServerOutputhandlerJSON.class);
			manager.startListening();
			new Thread(new ServerDialog(this)).start();
			return ((SocketSearchServer)worker).search();
		}else{
			worker=new SocketSearchClient(this);
			try {
				Main.logger.info("Starting client IP " + InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			manager = new NIOConnectionManager(this, NIOClientInputhandlerJSON.class, NIOClientOutputhandlerJSON.class);
			manager.startListening();
			//new Thread(new ClientDialog(this)).start();
			manager.setServer(manager.createConnection(serverIP));
			((ClientOutputhandler) manager.getServer().getOutputhandler()).sendJoinrequest();
			
			return ((ClientWorker)worker).search();
		}
	}
	
	@SuppressWarnings("unchecked")
	public long standbyStart(){
		worker=new SocketSearchClient(this);
		try {
			Main.logger.info("Starting standby-client IP " + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		manager = new NIOConnectionManager(this, NIOClientInputhandlerJSON.class, NIOClientOutputhandlerJSON.class);
		manager.startListening();
		//new Thread(new ClientDialog(this)).start();
		manager.setServer(manager.createConnection(serverIP));
		((ClientOutputhandler) manager.getServer().getOutputhandler()).sendStandbyJoinrequest();
		
		return ((ClientWorker)worker).search();
	}
	
	public void setRunning(boolean value){
		if(value==false){
			this.running=false;
			worker.setRunning(false);
		}else{
			this.running=true;
		}

	}
	
	public Worker getWorker() {
		return worker;
	}
	
	public void setWorker(ClientWorker worker) {
		this.worker = worker;
	}
	
	public int getId() {
		return instanceID;
	}

	public void setId(int id) {
		this.instanceID = id;
	}

	@SuppressWarnings("rawtypes")
	public ConnectionManager getConnectionManager() {
		return manager;
	}

	public void setConnectionManager(ConnectionManager<Connection, ?, ?> manager) {
		this.manager = manager;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	
	public void reportWorkDone(long work){
		result+=work;
		((ClientOutputhandler)manager.getServer().getOutputhandler()).sendResult(result);
		
	}

}
