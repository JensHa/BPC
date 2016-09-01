package de.fhrt.pcca.bpc.sockets.util;

import java.util.ArrayList;


public class Properties {
	private int myID;
	private int increasefactorforinitaltasks;
	private int children;
	private double sizeofbigtasks;
	private ArrayList<String> allClients;
	private int maxNodesInLocalQueue;
	private double maxNodesPerTask;
	private double maxTasksToImport;
	private double maxTasksToSteal;
	private ArrayList<Long> initialNodes;

	public Properties(ArrayList<Long> initialtasks,int increasefactorforinitaltasks,int children,double sizeofbigtasks, int myID, ArrayList<String> clients, int maxNodesInLocalQueue, double maxTasksToImport, double maxNodesPerTask, double maxTasksToSteal) {
		this.myID=myID;
		this.increasefactorforinitaltasks=increasefactorforinitaltasks;
		this.children=children;
		this.sizeofbigtasks=sizeofbigtasks;
		this.initialNodes=initialtasks;
		this.allClients=clients;
		this.maxNodesInLocalQueue=maxNodesInLocalQueue;
		this.setMaxTasksToImport(maxTasksToImport);
		this.setMaxNodesPerTask(maxNodesPerTask);
		this.setMaxTasksToSteal(maxTasksToSteal);
	}
	
	/**
	 * Constructor to clone
	 * @param toClone
	 */
	public Properties(Properties toClone){
		this.allClients=toClone.allClients;
		this.initialNodes=toClone.initialNodes;
		this.increasefactorforinitaltasks=toClone.increasefactorforinitaltasks;
		this.children=toClone.children;
		this.sizeofbigtasks=toClone.sizeofbigtasks;
		this.maxNodesInLocalQueue=toClone.maxNodesInLocalQueue;
		this.maxNodesPerTask=toClone.maxNodesPerTask;
		this.maxTasksToImport=toClone.maxTasksToImport;
		this.maxTasksToSteal=toClone.maxTasksToSteal;
		
	}



	public void setAllClients(ArrayList<String> other) {
		this.allClients = new ArrayList<>(other);
	}
	
	public ArrayList<String> getAllClients() {
		return this.allClients;
	}
	
//	public FirstWork getFirstWork(){
//		return new FirstWork();
//	}
//	
	public int getMyID() {
		return myID;
	}

	public void setMyID(int myID) {
		this.myID = myID;
	}
	
	public int getMaxNodesInLocalQueue() {
		return maxNodesInLocalQueue;
	}

	public double getMaxNodesPerTask() {
		return maxNodesPerTask;
	}

	public void setMaxNodesPerTask(double maxNodesPerTask) {
		this.maxNodesPerTask = maxNodesPerTask;
	}

	public double getMaxTasksToSteal() {
		return maxTasksToSteal;
	}

	public void setMaxTasksToSteal(double maxTasksToSteal) {
		this.maxTasksToSteal = maxTasksToSteal;
	}

	public double getMaxTasksToImport() {
		return maxTasksToImport;
	}

	public void setMaxTasksToImport(double maxTasksToImport) {
		this.maxTasksToImport = maxTasksToImport;
	}
	
	public ArrayList<Long> getInitalNodes() {
		return initialNodes;
	}

	public void setInitalNodes(ArrayList<Long> initialNodes) {
		this.initialNodes = initialNodes;
	}
	
	
	
	public int getIncreasefactorForInitaltasks() {
		return increasefactorforinitaltasks;
	}

	public void setIncreasefactorForInitaltasks(int increasefactorforinitaltasks) {
		this.increasefactorforinitaltasks = increasefactorforinitaltasks;
	}

	public int getAmountOfChildren() {
		return children;
	}

	public void setAmountOfChildren(int amountofbigtasks) {
		this.children = amountofbigtasks;
	}

	public double getSizeOfBigTasks() {
		return sizeofbigtasks;
	}

	public void setIncreasefactorForBigTasks(int increasefactorforbigtasks) {
		this.sizeofbigtasks = increasefactorforbigtasks;
	}



//	public class FirstWork {
//		int oddNodes;
//		int nodesForEachClient;
//
//		public FirstWork() {
//			oddNodes = initialtasks % allClients.size();
//			nodesForEachClient = (initialtasks - oddNodes) / allClients.size();
//		}
//		
//		public int getNodesForEeachClient(){
//			return this.nodesForEachClient;
//		}
//		
//		public int getOddNodes(){
//			return this.oddNodes;
//		}
//	}

}
