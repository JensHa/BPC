package de.fhrt.pcca.bpc.sockets.client.loadbalance;

import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;

public class StaticTaskCreator extends TaskCreationPolicy{
	private int maxInternal;
	private double percentToTransfer;

	public StaticTaskCreator(ClientWorker worker, double percent) {
		super(worker);
		this.properties=this.worker.getSolver().getProperties();
		this.maxInternal=properties.getMaxNodesInLocalQueue();
		this.percentToTransfer=percent;
	}

	@Override
	public boolean createTasksIfNeeded() {
		if(worker.getInternal().size()>=maxInternal){
		createTasks();
		return true;
		}
		return false;
	}
	
	@Override
	protected void createTasks() {
		int tasksToCreate=(int) ((worker.getInternal().size()/properties.getMaxNodesPerTask())*percentToTransfer);
		for(int i=0;i<tasksToCreate;i++){
			worker.getSharedQueue().add(new Task(worker.getInternal().removeFromBottom((int)properties.getMaxNodesPerTask())));
		}
	}

}
