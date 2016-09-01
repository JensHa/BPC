package de.fhrt.pcca.bpc.sockets.client;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class ClientHeuristic {
	private  ThreadMXBean bean;
	private long workerThreadID;
	private long lastWCT;
	long tmpLastWCT;
	private long lastCPUTime;
	long tmpLastCPU;
	
	public ClientHeuristic(long workerThreadID) {
		this.workerThreadID=workerThreadID;
		bean = ManagementFactory.getThreadMXBean( );
		lastWCT=System.nanoTime();
		lastCPUTime=bean.getThreadCpuTime(workerThreadID);
	}

	public double getCPUTime(){
		tmpLastWCT=System.nanoTime();
		tmpLastCPU=bean.getThreadCpuTime(workerThreadID);
		
		long nominal=System.nanoTime()-lastWCT;
		long actual=bean.getThreadCpuTime(workerThreadID)-lastCPUTime;
		
		lastWCT=tmpLastWCT;
		lastCPUTime=tmpLastCPU;
		
		return (double)actual/nominal;
	}

}
