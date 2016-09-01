package de.fhrt.pcca.bpc.sockets.client.loadbalance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.sockets.Connection;
import de.fhrt.pcca.bpc.sockets.Instance;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.SocketSearchClient;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;
import de.fhrt.pcca.bpc.sockets.handler.output.client.ClientOutputhandler;

public class SimpleWorkstealing extends Loadbalancer{
	private ClientWorker worker;
	private Instance instance;
	private int amountOfActiveStealsTry;
	private int stealCount;
	private long starttimeOfSteal;
	private long sumOfStealTime;
	

	public SimpleWorkstealing(ClientWorker clientWorker){
			super();
			this.worker = clientWorker;
			this.instance = worker.getInstance();
			this.setName("simple Workstealing");

			this.running=true;
	}
	
	@Override
	public void run() {
		while(running){
			synchronized(loadbalancingFlag){
				while(!loadbalancingFlag.get()){
					try {
						loadbalancingFlag.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			startWorkstealing();
		}
	}

	private void startWorkstealing() {

		if(!worker.hasWork().get()&&worker.getTermDet().getToken().get()!=0&&worker.getLoadbalancer().stealFinished().get()){
			Thread.yield();
		}
		
		
		
		ArrayList<String> openClients = new ArrayList<>(worker.getInstances());
		// Remove own node from the list
		try {
			openClients.remove(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		Random random = new Random();
		while (openClients.size() != 0 && loadbalancingFlag.get()) {
		
			amountOfActiveStealsTry++;
			stealNodeFromAnotherInstance(openClients.remove(random.nextInt(openClients.size())));
			if(loadbalancingFlag.get()){
			}
			
		}
//		if(openClients.isEmpty()){
//			// if the node enters this line then it has done a stealrequest to all nodes without success. Thus it must initate the termination detection
//			Main.logger.info("Out of work and no WS. Time: " + System.currentTimeMillis());
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// e.printStackTrace();
//			}
//		}
	}
	
	/**
	 * If the current instance has no more work then it steals work from another
	 * instance
	 * 
	 * @param ipOfOtherNode
	 * @return the node if the steal was successful otherwise NULL
	 */
	public void stealNodeFromAnotherInstance(String ipOfOtherNode) {
		starttimeOfSteal=System.currentTimeMillis();
		Connection connection=null;
		connection=instance.getConnectionManager().getConnection(ipOfOtherNode);
		if(connection!=null){
			try {
				synchronized (stealFinished) {
					((ClientOutputhandler)connection.getOutputhandler()).sendStealrequest();
					stealFinished.set(false);
					while(!stealFinished.get()){
						stealFinished.wait();	
					}
				}
				sumOfStealTime+=System.currentTimeMillis()-starttimeOfSteal;
				stealCount++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets the value from a previous steal.
	 * 
	 * @Invoker {@link BasicSocketsClientInputhandler} When the inputhandler receives a
	 *          response to a steal.
	 * @param stealNode
	 */
	public void setStealNode(LinkedList<Task> stolenArray) {
		synchronized (loadbalancingFlag) {
			if(!stolenArray.isEmpty()){
				loadbalancingFlag.set(false);
				loadbalancingFlag.notifyAll();
				worker.hasWork().set(true);
			}
			((SocketSearchClient) worker).getSharedQueue().addAll(stolenArray);
		}
		synchronized (stealFinished) {
			stealFinished.set(true);
			stealFinished.notifyAll();
		}
	}

	@Override
	public void triggerLoadbalancing() {
		synchronized (loadbalancingFlag) {
			loadbalancingFlag.set(true);
			loadbalancingFlag.notifyAll();
			while (loadbalancingFlag.get() && worker.isRunning()) {
				try {
					loadbalancingFlag.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

//			if (running == false) {
//				break;
//			}
		}		
	}


}
