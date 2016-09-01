package de.fhrt.pcca.bpc.sockets.client.loadbalance;

import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.util.Properties;

public abstract class TaskCreationPolicy {
	protected ClientWorker worker;
	protected Properties properties;

	public TaskCreationPolicy(ClientWorker worker) {
		this.worker=worker;
		this.properties=worker.getSolver().getProperties();
	}
	public abstract boolean createTasksIfNeeded();
	protected abstract void createTasks();
}
