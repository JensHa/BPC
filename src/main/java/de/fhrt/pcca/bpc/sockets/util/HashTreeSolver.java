package de.fhrt.pcca.bpc.sockets.util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Stack;


public class HashTreeSolver {
	private byte[] hashCache;
	private MessageDigest md;
	private int percentOfChunk;
	private ArrayList<Integer> listOfPercents;
	private int splitted;
	private int amountOfWorkDone;
	private int assinged;
	private int newValue;
	private Properties properties;
	private byte[] buffer;

	public HashTreeSolver(ArrayList<Long> initaltasks, int increasefactorforinitaltasks, int children,
			double increasefactorforbigtasks, int myID, ArrayList<String> clients, int maxNodesInLocalQueue,
			double maxTasksToImport, double maxNodesPerTask, double maxTasksToSteal) {
		this.hashCache = new byte[20];
		this.listOfPercents = new ArrayList<Integer>();
		try {
			this.md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		this.properties = new Properties(initaltasks, increasefactorforinitaltasks, children,increasefactorforbigtasks, myID, clients, maxNodesInLocalQueue, maxTasksToImport, maxNodesPerTask, maxTasksToSteal);
		this.buffer=new byte[20];
	}
	
	/**
	 * Constructor to clone objects
	 * @param toClone
	 */
	public HashTreeSolver(HashTreeSolver toClone) {
		this.properties = new Properties(toClone.getProperties());
		this.hashCache = toClone.hashCache;
		this.md = toClone.md;
	}

//	/**
//	 * Splits the given node in an random amount of children which each has a random amount of work
//	 * @param queue
//	 * @param value
//	 * @return
//	 */
//	public int splittAndSolveRandom(StaticStack queue, int value) {
//		if (value < 0) {
//			System.out.println("ERROR");
//		}
//		if (value == 0) {
//			return 0;
//		}
//		amountOfWorkDone = 0;
//		splitted = 0;
//		while (splitted != 100) {
//			hashCache = md.digest(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putDouble(value).array());
//			amountOfWorkDone++;
//			value--;
//			percentOfChunk = (int) (((hashCache[19] & 0xFF) / 255.0) * 100);
//			if (percentOfChunk + splitted < 100) {
//				listOfPercents.add(percentOfChunk);
//				splitted += percentOfChunk;
//			} else {
//				listOfPercents.add(100 - splitted);
//				splitted = 100;
//			}
//			if (value == 0) {
//				listOfPercents.clear();
//				return amountOfWorkDone;
//			}
//		}
//
//		assinged = 0;
//		while (!listOfPercents.isEmpty()) {
//			if (listOfPercents.size() == 1) {
//				listOfPercents.remove(0);
//				// DEBUG
//				if (value - assinged < 0) {
//					System.out.println("ERROR");
//				}
//				queue.add(value - assinged);
//			} else {
//				newValue = (int) Math.round(value * (listOfPercents.remove(listOfPercents.size() - 1) / 100.0));
//				if (newValue < 0) {
//					System.out.println("ERROR");
//				}
//				assinged += newValue;
//				if (assinged > value) {
//					newValue = newValue - (assinged - value);
//					assinged -= (assinged - value);
//				}
//				queue.add(newValue);
//			}
//		}
//		return amountOfWorkDone;
//	}
//
	/**
	 * Splits the given node in n children (n is taken from the config-file) with one big node (m% of the orginal value(m is taken from the config-file)) and n-1 small nodes
	 * @param stack
	 * @param value
	 * @return
	 */
	public long solve(Stack stack, long value) {
		int children = properties.getAmountOfChildren();
		if (value < 0) {
			try {
				throw new ThisShouldNeverHappenException("Something terrible happened here :(!");
			} catch (ThisShouldNeverHappenException e) {
				e.printStackTrace();
			}
		}
		// work work work work work
		long count = value > children ? children : value;
		for (long i = 0; i < count; i++) {
			buffer=md.digest(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putDouble(--value).array());
		}
		if (value == 0) {
			for(long i=0;i<count;i++){
				stack.add(0);
			}
			return count;
		}
		addAndMix(children, stack, value);
		return count;
	}
	
//	public int splittAndSolve(Queue queue, int value) {
//		int children = properties.getAmountOfChildren();
//		if (value < 0) {
//			try {
//				throw new ThisShouldNeverHappenException("Something terrible happened here :(!");
//			} catch (ThisShouldNeverHappenException e) {
//				e.printStackTrace();
//			}
//		}
//		// work work work work work
//		int count = value > children ? children : value;
//		for (int i = 0; i < count; i++) {
//			buffer=md.digest(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putDouble(--value).array());
//		}
//		if (value == 0) {
//			for(int i=0;i<count;i++){
//				queue.addNode(0);
//			}
//			return count;
//		}
//		addAndMix(children, queue, value);
//		return count;
//	}
//
//	private void addAndMix(int children, Queue queue, int value){
//		int pos=(int) (((double)((buffer[19]& 0xFF)+1)/255)*(children-1));
//		int bigNode = (int) (value * (properties.getSizeOfBigTasks() / 100));
//		value = value - bigNode;
//		int smallNode = value / (children - 1);
//		int oddNode = value % (children - 1);
//		for(int i=0;i<pos;i++){
//			queue.addNode(smallNode);
//		}
//		queue.addNode(bigNode+oddNode);
//		for(int i=0;i<children-pos-1;i++){
//			queue.addNode(smallNode);
//		}
//	}
	
	private void addAndMix(int children, Stack stack, long value){
		int pos=(int) (((double)((buffer[19]& 0xFF)+1)/255)*(children-1));
		long bigNode = (int) (value * (properties.getSizeOfBigTasks() / 100));
		value = value - bigNode;
		long smallNode = value / (children - 1);
		long oddNode = value % (children - 1);
		for(int i=0;i<pos;i++){
			stack.add(smallNode);
		}
		stack.add(bigNode+oddNode);
		for(int i=0;i<children-pos-1;i++){
			stack.add(smallNode);
		}
	}
	
	public long solve(long value) {
		long count = 0;
		for (long i = 0; i < value; i++) {
			md.digest(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putDouble(i).array());
			count++;
		}
		return count;
	}

	public Properties getProperties() {
		return this.properties;
	}
}
