package de.fhrt.pcca.bpc.sockets.server;

import java.util.Scanner;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.sockets.Instance;


public class ServerDialog implements Runnable{
	int state;
	Scanner scanner;
	BasicSocketsConnection conToServer;
	Instance instance;

	public ServerDialog(Instance instance) {
		scanner = new Scanner(System.in);
		state = 0;
		this.instance = instance;
	}

	@Override
	public void run() {
		dfa();
	}

	private void dfa() {
		String nextLine;
		switch (state) {
		case 0: // first display
			System.out.println("Commands:");
			System.out.println("q - shutdown");
			System.out.println("s - start with calculation");
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			state=1;
//			nextLine = scanner.nextLine();
//			if (nextLine.startsWith("s")) {
//				state=1;
//			}else if(nextLine.startsWith("q")){
//				state=2;
//			}
			dfa();
			break;
		case 1:
			Main.startTime=System.currentTimeMillis();
			((SocketSearchServer)instance.getWorker()).sendSolverToChilds();
			break;
		case 2: 
			break;
			
		default:
			break;
		}
	}


}
