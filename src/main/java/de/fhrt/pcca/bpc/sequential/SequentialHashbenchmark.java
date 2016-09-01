package de.fhrt.pcca.bpc.sequential;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.fhrt.pcca.bpc.sockets.util.HashTreeSolver;

public class SequentialHashbenchmark {
	//DynamicDequeueLong queue= new DynamicDequeueLong(1000);
	LinkedList<Long> queue=new LinkedList<Long>();
	private HashTreeSolver solver;
	public ConcurrentLinkedQueue<Long> sharedQueue;
	public LinkedList<Integer> internalQueue;
	long amountOfWorkDone = 0;
	
	public SequentialHashbenchmark(HashTreeSolver solver) {
		this.solver=solver;
		this.sharedQueue = new ConcurrentLinkedQueue<Long>();
		this.internalQueue=new  LinkedList<>();
	}

	public long search() {
	queue.addAll(solver.getProperties().getInitalNodes());
	 while (!queue.isEmpty()) {
	//	 amountOfWorkDone+=solver.splittAndSolve(queue, queue.remove(),50,99.99);
		}
		return amountOfWorkDone;
	}
}
