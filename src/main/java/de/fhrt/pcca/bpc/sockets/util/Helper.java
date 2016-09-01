package de.fhrt.pcca.bpc.sockets.util;

public class Helper {

	public static void main(String[] args) {
		System.out.println(calcAvgQueueLength(100000000, 5, 0.9));
	}
	
	public static int calcMaxQueueLength(int node, int children, double percent){
		int count=0;
		while(node>=children){
			count--;
			node=(int) ((node-children)*percent);
			count+=children;
		}
		return count;
	}
	
	public static int calcAvgQueueLength(int node, int children, double percent){
		int count=0;
		while(node>=children){
			count--;
			node=(int) ((node-children)*percent);
			count+=children/2;
		}
		return count;
	}

}
