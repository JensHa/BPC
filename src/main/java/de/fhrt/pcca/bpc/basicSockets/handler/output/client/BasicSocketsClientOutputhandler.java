package de.fhrt.pcca.bpc.basicSockets.handler.output.client;

import java.util.ArrayList;
import java.util.LinkedList;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.basicSockets.handler.output.BasicSocketsOutputhandler;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;
import de.fhrt.pcca.bpc.sockets.handler.output.client.ClientOutputhandler;

public abstract class BasicSocketsClientOutputhandler<T> extends BasicSocketsOutputhandler<T> implements ClientOutputhandler {
	protected ClientWorker worker;

	public BasicSocketsClientOutputhandler(BasicSocketsConnection connection) {
		super(connection);
		worker = (ClientWorker) connection.getConnectionManager().getInstance().getWorker();
	}

	@Override
	public void sendTerminationtoken(boolean colour) {
		Main.logger.debug("Sent token (" + colour + ") to node " + connection.getSocket().getInetAddress().getHostAddress());
	}
	
	@Override
	public LinkedList<Task> removeTasksFromClient() {
		LinkedList<Task> tasks = new LinkedList<Task>();
		// Avoid race-condition. token passes task
		synchronized (worker.getTermDet().getToken()) {
			int tasksToSteal= (int) (worker.getSolver().getProperties().getMaxTasksToSteal()<1?worker.getSolver().getProperties().getMaxTasksToSteal()*worker.getSharedQueue().size():worker.getSolver().getProperties().getMaxTasksToSteal());
			for (int i = 0; i < tasksToSteal; i++) {
				try {
					tasks.add(worker.getSharedQueue().remove());
				} catch (Exception e) {
					continue;
				}
			}
			if (!tasks.isEmpty()) {
				worker.getTermDet().setColour(false);
				Main.logger.debug("State was set to black");
			}
		}
		return tasks;
	}
}
