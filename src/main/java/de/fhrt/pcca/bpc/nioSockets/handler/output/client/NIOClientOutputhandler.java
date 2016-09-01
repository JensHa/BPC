package de.fhrt.pcca.bpc.nioSockets.handler.output.client;

import java.util.LinkedList;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.nioSockets.handler.output.NIOOutputhandler;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;
import de.fhrt.pcca.bpc.sockets.handler.output.client.ClientOutputhandler;

public abstract class NIOClientOutputhandler<T> extends NIOOutputhandler<T> implements ClientOutputhandler {
	protected ClientWorker worker;

	public NIOClientOutputhandler(NIOConnection connection) {
		super(connection);
		worker = (ClientWorker) connection.getConnectionManager().getInstance().getWorker();
	}

	@Override
	public void sendTerminationtoken(boolean colour) {
//		/*
//		 * [DOC 1001] avoid racecondition. token passes task. Send only the token if no task request is open.
//		 */
//		synchronized(worker.getLoadbalancer().stealFinished()){
//			while(!worker.getLoadbalancer().stealFinished().get()){
//				try {
//					worker.getLoadbalancer().stealFinished().wait();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		/*
//		 * [/DOC 1001]
//		 */
		Main.logger.debug("Sent token (" + colour + ") to node " + connection.getIP());
	}

	@Override
	public LinkedList<Task> removeTasksFromClient() {
		LinkedList<Task> tasks = new LinkedList<Task>();
		// Avoid race-condition. token leaves instance before task-package is complete
		synchronized (worker.getTermDet().getToken()) {
			//Total or %?
			int tasksToSteal;
			if(worker.getSolver().getProperties().getMaxTasksToSteal()<1){
				//%
				tasksToSteal=(int) Math.ceil(worker.getSolver().getProperties().getMaxTasksToSteal()*worker.getSharedQueue().size());
			}else{
				//total
				tasksToSteal=(int) worker.getSolver().getProperties().getMaxTasksToSteal();
			}
				
			for (int i = 0; i < tasksToSteal; i++) {
				try {
					tasks.add(worker.getSharedQueue().remove());
				} catch (Exception e) {
					break;
				}
			}
			
			if (!tasks.isEmpty()&&connection.getId()<worker.getId()) {
				worker.getTermDet().setColour(false);
				Main.logger.debug("State was set to black");
			}
		}
		long sumsent=0;
		for(Task task : tasks){
			for(long a : task.getNodes()){
				sumsent+=a;
			}
		}
		//Main.logger.info(sumsent);
		return tasks;
	}
}
