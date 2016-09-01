package de.fhrt.pcca.bpc.sockets.client.loadbalance;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Loadbalancer extends Thread{
	protected volatile boolean running;
	protected AtomicBoolean loadbalancingFlag;
	//TODO:Wrong place
	protected volatile AtomicBoolean stealFinished;

	public Loadbalancer() {
		this.stealFinished=new AtomicBoolean(true);
		this.loadbalancingFlag=new AtomicBoolean(false);
	}
	
	public final AtomicBoolean stealFinished() {
		return stealFinished;
	}
	
	public AtomicBoolean getLoadbalancingFlag() {
		return loadbalancingFlag;
	}
	public void setIsRunning(boolean value){
		this.running=value;
	}
	public abstract void triggerLoadbalancing();

}
