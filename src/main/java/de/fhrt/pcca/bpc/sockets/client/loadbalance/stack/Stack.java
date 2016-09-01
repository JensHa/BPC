package de.fhrt.pcca.bpc.sockets.client.loadbalance.stack;

import java.util.List;


public interface Stack<T> {
	
	public abstract void add(long i);

	public abstract long remove();

	public abstract long get(int i);

	public abstract int size();

	public abstract void addAll(long[] o);

	public abstract void addAll(List<T> o);

	public abstract long[] removeFromBottom(int n);

	public abstract int capacity();
}
