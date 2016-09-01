package de.fhrt.pcca.bpc.sockets.client;

import java.util.Scanner;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.handler.output.client.BasicSocketsClientOutputhandler;
import de.fhrt.pcca.bpc.sockets.Connection;
import de.fhrt.pcca.bpc.sockets.ConnectionManager;
import de.fhrt.pcca.bpc.sockets.Instance;


public class ClientDialog implements Runnable {
	@SuppressWarnings("rawtypes")
	ConnectionManager manager;
	int state;
	Scanner scanner;
	Connection conToServer;

	public ClientDialog(Instance instance) {
		this.manager = instance.getConnectionManager();
		scanner = new Scanner(System.in);
		state = 0;
	}

	@Override
	public void run() {
		dfa();
	}

	@SuppressWarnings("unchecked")
	private void dfa() {
		String nextLine;
		switch (state) {
		case 0: // first display
			System.out.println("Commands:");
			System.out.println("q - shutdown");
			System.out.println("c - connect to server");
			nextLine = scanner.nextLine();
			if (nextLine.startsWith("c")) {
				state=1;
			}else if(nextLine.startsWith("q")){
				state=2;
			}
			dfa();
			break;
		case 1:
			System.out.println("Please enter the IP");
			nextLine = scanner.nextLine();
			Main.logger.info("Connecting to " + nextLine);
			conToServer = manager.createConnection(nextLine);
			manager.setServer(conToServer);
			if(conToServer!=null){
				((BasicSocketsClientOutputhandler<?>)conToServer.getOutputhandler()).sendJoinrequest();
				Main.logger.info("Connected to server, wait for server to start");
				state = 2;
			}else{
				Main.logger.error("Error, could not connect to server");
				state = 0;
			}
			dfa();
			break;
		case 2: 
			//Process
			break;
			
		default:
			break;
		}
	}

}