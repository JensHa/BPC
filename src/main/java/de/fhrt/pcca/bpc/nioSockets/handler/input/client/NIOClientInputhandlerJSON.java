package de.fhrt.pcca.bpc.nioSockets.handler.input.client;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.nioSockets.handler.input.messageReader.JSONMessageReader;
import de.fhrt.pcca.bpc.nioSockets.handler.output.client.NIOClientOutputhandlerJSON;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.SimpleWorkstealing;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;
import de.fhrt.pcca.bpc.sockets.handler.output.client.ClientOutputhandler;

public class NIOClientInputhandlerJSON extends NIOClientInputhandler {
	//needed to parse the incoming json string
	JSONParser jsonParser;
	JSONObject jsonObject;
	
	public NIOClientInputhandlerJSON(NIOConnection connection) {
		super(connection);
		this.worker =  (ClientWorker)connection.getConnectionManager().getInstance().getWorker();
		this.jsonParser = new JSONParser();
		this.messageReader=new JSONMessageReader();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(ByteBuffer buffer) {
		/*
		 * [DOC 1000]
		 * Used to prefer the token.
		 */
//		if(!worker.hasWork().get()&&worker.getTermDet().getToken().get()!=0&&worker.getLoadbalancer().stealFinished().get()){
//			synchronized (worker.getTermDet().getToken()) {
//				while(!worker.hasWork().get()&&worker.getTermDet().getToken().get()!=0&&worker.getLoadbalancer().stealFinished().get()){
//					try {
//						worker.getTermDet().getToken().wait();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
		if(!worker.hasWork().get()&&worker.getTermDet().getToken().get()!=0&&worker.getLoadbalancer().stealFinished().get()){
			Thread.yield();
		}
		/*
		 * [/DOC 1000]
		 */
		ArrayList<String> list = (ArrayList<String>) messageReader.readByteBuffer(buffer);
		for(String input : list){
			parseAndCheck(input);
			Main.logger.debug("IN: FROM="+connection.getIP()+"; "+"UUID="+jsonObject.get("UUID")+"; "+"TYPE="+jsonObject.get("TYPE")+"; "+((jsonObject.containsKey("COMMAND"))?("COMMAND="+ jsonObject.get("COMMAND")):""));
					
			messageType = (String) jsonObject.get("TYPE");
			if (messageType == null) {
				messageType = "";
			}
			switch (messageType) {
			case "COMMAND":
				receiveCommand();
				break;
			case "SOLVER":
				receiveSolver();
				break;
			case "TASKS":
				receiveTasks();
				break;
			case "TOKEN":
				receiveTerminationtoken();
				break;
			case "INSTANCES":
				updateInstancesList();
				break;
			default:
				break;
			}
		}
		messageReader.clearUnreadMessages();
	}
	
	
	private void parseAndCheck(String s) {
		jsonObject = null;
		try {
			jsonObject = (JSONObject) jsonParser.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (!jsonObject.containsKey("UUID")) {
			Main.logger.error("ERROR: Message has no UUID");
			Main.logger.error(jsonObject.toJSONString());
			System.exit(1);
		}		
	}

	public void receiveCommand() {
		commandReceived = (String) jsonObject.get("COMMAND");
		switch (commandReceived) {
		case "YOUAREIN":
			break;
		case "STEALREQUEST":
			receiveStealrequest();
			break;
		case "TERMINATE":
//			synchronized (worker.getLoadbalancer().getLoadbalancingFlag()) {
				worker.sendResult();
				worker.getInstance().setRunning(false);
//				worker.getLoadbalancer().getLoadbalancingFlag().notifyAll();
				break;
//			}
		case "EFFICIENCYREQUEST":
			receiveEfficiencyRequest();
			break;
		default:
			break;
		}
	}

	@Override
	public void receiveStealrequest() {
		while(!worker.isRunning()){}
		super.receiveStealrequest();
		((NIOClientOutputhandlerJSON)connection.getOutputhandler()).sendTasks();		
	}

	@SuppressWarnings("unchecked")
	public void receiveSolver() {
		//this looks terrible but is necessary cause the parser will give you always a long value 
		ArrayList<Long> initialtasks = new ArrayList<Long>();
		JSONArray jArray = (JSONArray) jsonObject.get("INITIALTASKS");
		if (jArray != null) {
			for (int i = 0; i < jArray.size(); i++) {
				initialtasks.add((Long)jArray.get(i));
			}
		}
		worker.createSolver(
				initialtasks, 
				((Long) jsonObject.get("INCREASEFACTORFORINITIALTASKS")).intValue(),
				((Long) jsonObject.get("AMOUNTOFCHILDREN")).intValue(), 
				((Double) jsonObject.get("SIZEOFBIGTASKS")).intValue(),
				((Long) jsonObject.get("MYID")).intValue(),
				((ArrayList<String>)jsonObject.get("ALLCLIENTS")),
				((Long) jsonObject.get("MAXNODESINLOCALQUEUE")).intValue(),
				((Double) jsonObject.get("MAXTASKSTOIMPORT")),
				((Double) jsonObject.get("MAXNODESPERTASK")),
				((Double) jsonObject.get("MAXTASKSTOSTEAL")).doubleValue());
	}

	public void receiveTasks() {
		//two dimensions
		JSONArray stealArray = (JSONArray)jsonObject.get("TASKS");
		LinkedList<Task> returnArray=new LinkedList<Task>();
		for(int i=0;i<stealArray.size();i++){
			//TODO: maxnodes works only >=1. 
			long[] tmpTask=new long[((JSONArray) stealArray.get(i)).size()];
			for(int i1=0;i1<((JSONArray)stealArray.get(i)).size();i1++){
				tmpTask[i1]=(long) ((Long)((JSONArray)stealArray.get(i)).get(i1)).intValue();
			}
			Task task = new Task(tmpTask);
			returnArray.add(task);
		}
		((SimpleWorkstealing)worker.getLoadbalancer()).setStealNode(returnArray);			
	}

	public void receiveTerminationtoken() {
		while(!worker.isRunning()){}
		int myID = connection.getConnectionManager().getInstance().getWorker().getId();
		int localtoken=(int) (((boolean) jsonObject.get("COLOUR")) ? 1 : -1);
		Main.logger.debug("Received token: " + localtoken);
		//TODO: smaller sync?
		synchronized (worker.getTermDet().getToken()) {
			worker.getTermDet().setToken(localtoken);
			if (myID == 0 && localtoken == 1) {
				for (String ip : worker.getInstances()) {
					((ClientOutputhandler) connection.getConnectionManager().getConnection(ip).getOutputhandler()).sendCommand("TERMINATE");
				}
				/*
				 * [DOC 1000]
				 * Drop token or else the input-thread stucks.
				 */
				worker.getTermDet().setToken(0);
				/*
				 * [/DOC 1000]
				 */
			}
			worker.getTermDet().getToken().notifyAll();
		}
	}

	@Override
	public void receiveEfficiencyRequest() {
		if(worker.isRunning()){
			((ClientOutputhandler)connection.getOutputhandler()).sendEfficiency();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateInstancesList() {
		JSONArray newInstances = (JSONArray)jsonObject.get("INSTANCES");
		worker.setInstances(newInstances);
	}
}
