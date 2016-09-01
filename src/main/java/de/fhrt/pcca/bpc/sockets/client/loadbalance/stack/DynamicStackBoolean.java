package de.fhrt.pcca.bpc.sockets.client.loadbalance.stack;

import java.util.Arrays;
import java.util.List;

public class DynamicStackBoolean{
	private int pos=-1;
	private boolean[] stack;
	
	public DynamicStackBoolean(int initialSize) {
		super();
		this.stack=new boolean[initialSize];
	}
	
	public void add(boolean i){
		
		//is the current size of the stack big enough?
		if(stack.length-pos==1){
			boolean[] tmpStack=new boolean[stack.length*2];
			tmpStack=stack=Arrays.copyOf(stack, tmpStack.length);
			stack=tmpStack;
		}
			pos++;
			stack[pos]=i;
	}
	
	public boolean remove(){
		boolean i=stack[pos];
		pos--;
		return i;
	}
	
	public boolean removeRandom(){
		double chance = Math.random();
		if(chance>0.5){
			return remove();
		}
		return removeFromBottom(1)[0];
	}
	
	public boolean get(int i){
	return stack[i];	
	}
	
	public int size(){
		return pos+1;
	}
	
	public void addAll(boolean[] o){
		pos++;
		int tmppos=pos;

		for(int i=tmppos;i<tmppos+o.length;i++){
			stack[i]=o[i-tmppos];
			pos++;
		}
		pos--;
	}
	
	public void addAll(List<Boolean> o){
		pos++;

		int tmppos=pos;
		for(int i=tmppos;i<tmppos+o.size();i++){
			stack[i]=o.get(i-tmppos);
			pos++;
		}
		pos--;
	}
	
	public boolean[] removeFromBottom(int n){
		boolean[] tmp=Arrays.copyOfRange(stack, 0, n);
		stack=Arrays.copyOfRange(stack, n, stack.length+n);
		//stack=Arrays.copyOf(stack, stack.length+n);
		pos=pos-n;
		return tmp;
	}

	public int capacity(){
		return this.stack.length;
	}

}
