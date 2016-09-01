package de.fhrt.pcca.bpc.sockets.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Stack;

public class BouncingPCSolver{
	private MessageDigest md;
	private byte[] random;
	private int posForTrue;
	private Random rand;
	private int amountOfChildren;
	private Properties properties;

	
	public BouncingPCSolver(ArrayList<Long> initaltasks, int increasefactorforinitaltasks, int children,
			double increasefactorforbigtasks, int myID, ArrayList<String> clients, int maxNodesInLocalQueue,
			double maxTasksToImport, double maxNodesPerTask, double maxTasksToSteal) {
		this.random=new byte[20];
		this.rand=new Random();
		this.amountOfChildren=16;
		this.properties = new Properties(initaltasks, increasefactorforinitaltasks, children,increasefactorforbigtasks, myID, clients, maxNodesInLocalQueue, maxTasksToImport, maxNodesPerTask, maxTasksToSteal);

		try {
			this.md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public long solve(LinkedList<Long> stack, long value){
		if(value==1){
			posForTrue=rand.nextInt(amountOfChildren);
			for(int i=0;i<amountOfChildren;i++){
				if(i==posForTrue){
					if(Math.random()>0.5){
						stack.addLast((long) 1);
						continue;
					}else{
						stack.addFirst((long) 1);
						continue;
					}

				}
				if(Math.random()>0.5){
					stack.addLast((long) 0);
					continue;
				}else{
					stack.addFirst((long) 0);
					continue;
				}
			}
			return 0;
		}
		for(int i=0;i<100000;i++){
			random=md.digest(random);
		}
		return 0;
	}
	
	public Properties getProperties() {
		return this.properties;
	}


}
