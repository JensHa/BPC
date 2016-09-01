package de.fhrt.pcca.bpc.sockets.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import de.fhrt.pcca.bpc.sockets.Instance;
import de.fhrt.pcca.bpc.sockets.Worker;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.Loadbalancer;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.TaskCreationPolicy;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Stack;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;
import de.fhrt.pcca.bpc.sockets.terminationDetection.DijkstraTerminiationDetection;
import de.fhrt.pcca.bpc.sockets.util.HashTreeSolver;
import de.fhrt.pcca.bpc.sockets.util.StatPrinter;

public abstract class ClientWorker extends Worker{
	public Thread workerthread;
	/**
	 * @Invoker {@link BasicSocketsClientInputhandler} {@link SocketSearchClient}
	 */
	protected volatile HashTreeSolver solver;
	protected DijkstraTerminiationDetection termDet;
	protected Loadbalancer loadbalancer;
	protected StatPrinter statLogger;
	protected Stack<Long> internal;
	protected ConcurrentLinkedDeque<Task> shared;
	protected volatile AtomicBoolean hasWork;
	protected long amountOfWorkDone;
	protected TaskCreationPolicy taskCreator;
	protected ClientHeuristic heuristic;

	public ClientWorker(Instance instance) {
		super(instance);
		hasWork=new AtomicBoolean(true);
	}
	
	public abstract long search();

	public Thread getWorkerthread() {
		return workerthread;
	}
	
	public void setWorkerthread(Thread workerthread) {
		this.workerthread = workerthread;
	}


	public DijkstraTerminiationDetection getTermDet() {
		return termDet;
	}
	
	public Loadbalancer getLoadbalancer() {
		return loadbalancer;
	}

	public void createSolver(ArrayList<Long> initialtasks, int increasefactorforinitaltasks, int children, int increasefactorforbigtasks, int id, ArrayList<String> allInstances, int maxSizeOfLocalQueue, double maxTasksToImport, double maxnodespertask, double maxTasksToSteal){
	this.instances=allInstances;	
	this.workerID=id;
	this.solver=new HashTreeSolver(initialtasks, increasefactorforbigtasks, children, increasefactorforbigtasks, id,allInstances, maxSizeOfLocalQueue, maxTasksToImport, maxnodespertask, maxTasksToSteal);
	}
	
	@Override
	public void setRunning(boolean running) {
		super.setRunning(running);
		loadbalancer.setIsRunning(running);
		termDet.setIsRunning(running);
		statLogger.setIsRunning(running);
	}

	public HashTreeSolver getSolver() {
		return solver;
	}

	public void setSolver(HashTreeSolver solver) {
		this.solver=solver;
	}
	
	public ConcurrentLinkedDeque<Task> getSharedQueue() {
		return shared;
	}
	
	
	public AtomicBoolean hasWork() {
		return hasWork;
	}
	
	public long getAmountOfWorkDone() {
		return amountOfWorkDone;
	}

	public void sendResult() {
		instance.reportWorkDone(amountOfWorkDone);		
	}
	
	public Stack<Long> getInternal() {
		return internal;
	}
	
	public ClientHeuristic getHeuristic() {
		return heuristic;
	}


}
