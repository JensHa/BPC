package de.fhrt.pcca.bpc.sockets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Worker {
	protected Instance instance;
	protected int workerID;
	protected volatile boolean running;
	protected ArrayList<String> instances;


	
	public Worker(Instance instance) {
	this.instance=instance;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}
	
	public int getId() {
		return workerID;
	}

	public void setId(int workerID) {
		this.workerID = workerID;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public List<String> getInstances() {
		if(instances==null){
			return null;
		}
		return Collections.unmodifiableList(instances);
	}
	
	
	public void setInstances(ArrayList<String> newInstances){
		this.instances=newInstances;
	}
}
