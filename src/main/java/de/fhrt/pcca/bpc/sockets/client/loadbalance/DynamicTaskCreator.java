package de.fhrt.pcca.bpc.sockets.client.loadbalance;

import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;

public class DynamicTaskCreator extends TaskCreationPolicy {
	private double percent;

	public DynamicTaskCreator(ClientWorker worker, double percent) {
		super(worker);
		if (!worker.getInternal().getClass().getName()
				.equals("de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.DynamicStackLong")) {
			throw new UnsupportedClassVersionError("Use only with DynamicStack!");
		}
		this.percent = percent;
	}

	@Override
	public boolean createTasksIfNeeded() {
		// internal > external?
		// if(worker.getSharedQueue().size()*properties.getMaxNodesPerTask()<worker.getInternal().size()){
		// createTasks();
		// return true;
		// }
		if (worker.getSharedQueue().isEmpty()) {
			createTasks();
			return true;
		}
		return false;
	}

	@Override
	protected void createTasks() {
		int nodesPerTask=(int)((double)worker.getInternal().size()*percent/2);
		if(nodesPerTask!=0){
			worker.getSharedQueue().add(new Task(worker.getInternal().removeFromBottom(nodesPerTask)));
			worker.getSharedQueue().add(new Task(worker.getInternal().removeFromBottom(nodesPerTask)));
		}
		

//		int tasksToCreate = (int) (worker.getInternal().size() / properties.getMaxNodesPerTask() * percent);
//		for (int i = 0; i < tasksToCreate; i++) {
//			worker.getSharedQueue().add(new Task(worker.getInternal().removeFromBottom((int) properties.getMaxNodesPerTask())));
//		}
	}
}
