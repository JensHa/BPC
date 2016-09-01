package de.fhrt.pcca.bpc.sockets.terminationDetection;

import java.util.concurrent.atomic.AtomicInteger;

import de.fhrt.pcca.bpc.sockets.Instance;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.handler.output.client.ClientOutputhandler;

public class DijkstraTerminiationDetection extends Thread {
	private ClientWorker worker;
	private Instance instance;
	private volatile boolean colour; // false=black; true=white
	private volatile AtomicInteger token; // 0=no token; -1=black; 1=white
	private volatile boolean running;

	public DijkstraTerminiationDetection(ClientWorker clientWorker) {
		this.worker = clientWorker;
		this.colour=true;
		this.token=new AtomicInteger();

		if (clientWorker.getId() == 0) {
			token.set(1);
		}else{
			this.token.set(0);
		}
		this.instance = worker.getInstance();
		this.setName("Termination detection");
		this.running=true;
	}

	public void terminationDetection() {
		if(!worker.getLoadbalancer().stealFinished().get()){
			Thread.yield();
		}
		synchronized (token) {
//			synchronized (worker.getLoadbalancer().stealFinished()) {
//				if(!worker.getLoadbalancer().stealFinished().get()){
////					System.out.println("###"+ worker.getLoadbalancer().stealFinished().get());
////					try {
////						Thread.sleep(1000);
////					} catch (InterruptedException e) {
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
//					return;
//				}
				//DEADLOCK!
//				while (!worker.getLoadbalancer().stealFinished().get()) {
//					try {
//						worker.getLoadbalancer().stealFinished().wait();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
			if(!worker.getLoadbalancer().stealFinished().get()){
				return;
			}
				int id = worker.getId();
				if (id == 0 && token.get() != 0) {
					String ipOfNextNode=worker.getInstances().size() == 1? worker.getInstances().get(0): worker.getInstances().get(1);
					((ClientOutputhandler) instance.getConnectionManager().getConnection(ipOfNextNode).getOutputhandler()).sendTerminationtoken(colour);
					colour = true;
					token.set(0);

				} else {
					if (token.get() != 0) {
						if (!colour) {
							token.set(-1);
						}
						String ipOfNextNode = worker.getInstances().get(id == worker.getInstances().size() - 1 ? 0 : id + 1);
						((ClientOutputhandler) instance.getConnectionManager().getConnection(ipOfNextNode).getOutputhandler()).sendTerminationtoken(token.get() == -1 ? false : true);
						token.set(0);
						colour = true;
					}
				}
		//	}
			
			token.notifyAll();
		}
	}

	public boolean getColour() {
		return colour;
	}

	public void setColour(boolean colour) {
		this.colour = colour;
	}

	public AtomicInteger getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token.set(token);
	}
	
	public void setIsRunning(boolean value){
		this.running=value;
	}

	@Override
	public void run() {
		while (running) {
			synchronized(token){
				while (token.get()==0) {
					try {
						token.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			synchronized (worker.hasWork()) {
				while(worker.hasWork().get()){
					try {
						worker.hasWork().wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			terminationDetection();
		}
	}
	
	public void triggerTermination(){
		synchronized (worker.hasWork()) {
			worker.hasWork().set(false);
			worker.hasWork().notifyAll();
		}
	}
}
