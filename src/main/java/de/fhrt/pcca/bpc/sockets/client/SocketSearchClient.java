package de.fhrt.pcca.bpc.sockets.client;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.sockets.Instance;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.DynamicTaskCreator;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.SimpleWorkstealing;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.DynamicStackLong;
import de.fhrt.pcca.bpc.sockets.terminationDetection.DijkstraTerminiationDetection;
import de.fhrt.pcca.bpc.sockets.util.StatPrinter;

public class SocketSearchClient extends ClientWorker {
	public SocketSearchClient(Instance instance) {
		super(instance);
		this.workerthread = Thread.currentThread();
		this.heuristic=new ClientHeuristic(Thread.currentThread().getId());
	}

	@Override
	public long search() {
		while (solver == null) {}
		Main.logger.info("Starting with calculation");
		for(String ip : solver.getProperties().getAllClients()){
			instance.getConnectionManager().createConnection(ip);
		}
		Main.startTime = System.currentTimeMillis();
		internal = new DynamicStackLong(100);
		shared = new ConcurrentLinkedDeque<>();
		internal.addAll(getSolver().getProperties().getInitalNodes());
		termDet = new DijkstraTerminiationDetection(this);
		termDet.start();
		loadbalancer = new SimpleWorkstealing(this);
		loadbalancer.start();
		statLogger = new StatPrinter(Integer.valueOf(Main.properties.getProperty("statinterval")), this);
		statLogger.start();
		taskCreator=new DynamicTaskCreator(this,Double.valueOf(Main.properties.getProperty("maxTasksToExport")));
		running = true;
		
		while (running) {
			if (internal.size() == 0) {
				if (!getTasks()) {
					termDet.triggerTermination();
					loadbalancer.triggerLoadbalancing();
				}
			}else{
				taskCreator.createTasksIfNeeded();
				try {
					long tempNode = internal.remove();
					amountOfWorkDone += solver.solve(internal, tempNode);
				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}finally {
				}
			}
		}
		return amountOfWorkDone;
	}

 	private boolean getTasks() {
  		boolean wasSuccessful=false;
  		int tasksToImport = (int) (solver.getProperties().getMaxTasksToImport()<1?shared.size()*solver.getProperties().getMaxTasksToImport():solver.getProperties().getMaxTasksToImport());
  		//special case...if shared==1 and %
  		tasksToImport=shared.size()==1?1:tasksToImport;
  		try{
 			for(int i=0; i<tasksToImport;i++){
 				internal.addAll(shared.removeFirst().getNodes());
  				wasSuccessful=true;
  			}
  		}catch(NoSuchElementException e){
 		}
 		return wasSuccessful;
 	}
}
