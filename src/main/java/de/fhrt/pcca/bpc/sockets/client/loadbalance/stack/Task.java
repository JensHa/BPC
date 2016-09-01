package de.fhrt.pcca.bpc.sockets.client.loadbalance.stack;

public class Task {
	
	private long[] nodes;
	
	public Task(long[] nodes) {
	this.nodes= nodes;
	}

	public long[] getNodes() {
		return nodes;
	}

	public void setNodes(long[] nodes) {
		this.nodes = nodes;
	}
}

