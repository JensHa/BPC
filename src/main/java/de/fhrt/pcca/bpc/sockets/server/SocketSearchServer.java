package de.fhrt.pcca.bpc.sockets.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.sockets.Instance;
import de.fhrt.pcca.bpc.sockets.handler.output.server.ServerOutputhandler;
import de.fhrt.pcca.bpc.sockets.util.HashTreeSolver;


public class SocketSearchServer extends ServerWorker{
	private volatile HashTreeSolver solver;
	private ArrayList<String> instances;
	private long result;
	private int finishedClients;
	private ServerHeuristic heuristic;
	private  ArrayList<String> standbyInstances;
	
	public SocketSearchServer(Instance instance) {
		super(instance);
		this.instances=new ArrayList<>();
		this.standbyInstances=new ArrayList<String>();
		this.heuristic=new ServerHeuristic(this, Integer.valueOf(Main.properties.getProperty("efficiencyRefreshInterval")));
	}
	
	public long search() {
		this.solver=createSolverOutOfProperties(Main.properties);
		//heuristic.start();
		//start of server heuristic
		while(instance.getConnectionManager().isActive()){}
		return result;
	}
	
	public void addClient(String client){
		synchronized (this) {
			this.instances.add(client);
		}
	}
	
	public List<String> getInstances() {
		return Collections.unmodifiableList(instances);
	}
	
	public void sendSolverToChilds(){
		solver.getProperties().setAllClients(instances);
		
		for(int i=0;i<instances.size();i++){
			System.out.println(instances.get(i));
		}

		int nodesForEachInstance=solver.getProperties().getInitalNodes().size()/instances.size();
		int oddNodes=solver.getProperties().getInitalNodes().size()%instances.size();
		for (int i=0;i<instances.size();i++){
			HashTreeSolver tmp= new HashTreeSolver(solver);
			tmp.getProperties().setMyID(i);
			if(i==instances.size()-1){
				tmp.getProperties().setInitalNodes(new ArrayList<>(solver.getProperties().getInitalNodes().subList(i*nodesForEachInstance, i*nodesForEachInstance+nodesForEachInstance+oddNodes)));
			}else{
				tmp.getProperties().setInitalNodes(new ArrayList<>(solver.getProperties().getInitalNodes().subList(i*nodesForEachInstance, i*nodesForEachInstance+nodesForEachInstance)));
			}
			((ServerOutputhandler)instance.getConnectionManager().getConnection(instances.get(i)).getOutputhandler()).sendSolver(tmp);
		}
		//running here means "the calculation has started"
		this.running=true;
	}
	
	private static HashTreeSolver createSolverOutOfProperties(Properties properties) {
		
		ArrayList<Long> initialtasks=new ArrayList<Long>();

//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 1600000000);
//		initialtasks.add((long) 800000000);
		
		
//		initialtasks.add((long) 800000000);
//		initialtasks.add((long) 800000000);

//		initialtasks.add((long) 400000000);
//		initialtasks.add((long) 400000000);
//		initialtasks.add((long) 400000000);
//		initialtasks.add((long) 400000000);
//
//		initialtasks.add((long) 200000000);
//		initialtasks.add((long) 200000000);
//		initialtasks.add((long) 200000000);
//		initialtasks.add((long) 200000000);
//		initialtasks.add((long) 200000000);
//		initialtasks.add((long) 200000000);
//		initialtasks.add((long) 200000000);
//		initialtasks.add((long) 200000000);

		initialtasks.add((long) 1600000000);
//
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		initialtasks.add((long) 100000000);
//		
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
//		initialtasks.add((long) 50000000);
		
		
//		initialtasks.add((long) 1552000000);
//		initialtasks.add((long) 48000000);
		
//		initialtasks.add((long) 1552000000);
//		initialtasks.add((long) 16000000);
//		initialtasks.add((long) 16000000);
//		initialtasks.add((long) 16000000);

		
//		initialtasks.add((long) 1552000000);
//		initialtasks.add((long) 6857142);
//		initialtasks.add((long) 6857142);
//		initialtasks.add((long) 6857142);
//		initialtasks.add((long) 6857142);
//		initialtasks.add((long) 6857142);
//		initialtasks.add((long) 6857142);
//		initialtasks.add((long) 6857148);
	
		
//		initialtasks.add((long) 1552000000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);
//		initialtasks.add((long) 3200000);

//		initialtasks.add((long) 1552000000);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548387);
//		initialtasks.add((long) 1548390);

		//TODO: Fill
		//int initialtasks=Integer.valueOf(properties.getProperty("initialtasks"));
		int increasefactorforinitaltasks=Integer.valueOf(properties.getProperty("increasefactorforinitaltasks"));
		double sizeofbigtasks=Double.valueOf(properties.getProperty("sizeofbigtasks"));
		int children=Integer.valueOf(properties.getProperty("childs"));
		//int amountofbigtasks=Integer.valueOf(properties.getProperty("amountofbigtasks"));
		//int increasefactorforbigtasks=Integer.valueOf(properties.getProperty("increasefactorforbigtasks"));
		int maxNodesInLocalQueue=Integer.valueOf(properties.getProperty("maxnodesininternalqueue"));
		double maxTasksToImport=Double.valueOf(properties.getProperty("maxtaskstoimport"));
		double maxNodesPerTask=Double.valueOf(properties.getProperty("maxnodespertask"));
		double maxTasksToSteal=Double.valueOf(properties.getProperty("maxtaskstosteal"));
		return new HashTreeSolver(initialtasks,increasefactorforinitaltasks,children,sizeofbigtasks,0,null,maxNodesInLocalQueue,maxTasksToImport,maxNodesPerTask,maxTasksToSteal);
}

	public synchronized void addToResult(Long long1) {
		this.result+=long1;
		this.finishedClients++;
		if(this.finishedClients==instances.size()){
			Main.logger.info("Result is: "+ result);
			instance.getConnectionManager().shutdown();
		}
	}
	
	public void getClientEfficiency(){
		for(int i=0;i<instances.size();i++){
			((ServerOutputhandler)instance.getConnectionManager().getConnection(instances.get(i)).getOutputhandler()).requestClientEfficiency();
		}
		
	}
	
	public ServerHeuristic getHeuristic() {
		return heuristic;
	}

	public void addStandbyClient(String ip) {
		this.standbyInstances.add(ip);
	}
	
	public ArrayList<String> getStandyInstances(){
		return this.standbyInstances;
	}

	public void sendSolverToChild(String ip){
		solver.getProperties().setAllClients(instances);
		HashTreeSolver tmp= new HashTreeSolver(solver);
		tmp.getProperties().setMyID(instances.size()-1);
		tmp.getProperties().setInitalNodes(new ArrayList<>());
		((ServerOutputhandler)instance.getConnectionManager().getConnection(ip).getOutputhandler()).sendSolver(tmp);
	}
}
