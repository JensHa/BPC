package de.fhrt.pcca.bpc.sockets.client.loadbalance.stack;

import java.util.Arrays;
import java.util.List;

public class StaticStack implements Stack<Long>{
	private int pos=-1;
	private long[] stack;
	private int size;
	
	public StaticStack(int size) {
	super();
	this.stack=new long[size];
	this.size=stack.length;
	}
	
	public void add(long i){
//		//is the current size of the stack big enough?
//		if(stack.length-pos==1){
//			int[] tmpStack=new int[stack.length*2];
//			tmpStack=stack=Arrays.copyOf(stack, tmpStack.length);
//			stack=tmpStack;
//		}
//			pos++;
//			stack[pos]=i;
		pos++;
		stack[pos]=i;
	}
	
	public long remove(){
		long i=stack[pos];
		stack[pos]=0;
		pos--;
		return i;
	}
	
	public long get(int i){
	return stack[i];	
	}
	
	public int size(){
		return pos+1;
	}
	
	public void addAll(long[] o){
		pos++;
		int tmppos=pos;

		for(int i=tmppos;i<tmppos+o.length;i++){
			stack[i]=o[i-tmppos];
			pos++;
		}
		pos--;
	}
	
	public void addAll(List<Long> o){
		pos++;

		int tmppos=pos;
		for(int i=tmppos;i<tmppos+o.size();i++){
			stack[i]=o.get(i-tmppos);
			pos++;
		}
		pos--;
	}
	
	public long[] removeFromBottom(int n){
		long[] tmp=Arrays.copyOfRange(stack, 0, n);
		stack=Arrays.copyOfRange(stack, n, stack.length);
		stack=Arrays.copyOf(stack, size);
		pos=pos-n;
		return tmp;
	}

	public int capacity(){
		return this.stack.length;
	}


}
