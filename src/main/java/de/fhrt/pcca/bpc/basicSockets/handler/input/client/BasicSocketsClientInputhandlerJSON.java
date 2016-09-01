package de.fhrt.pcca.bpc.basicSockets.handler.input.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.basicSockets.handler.output.client.BasicSocketsClientOutputhandler;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.SimpleWorkstealing;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;

public class BasicSocketsClientInputhandlerJSON extends BasicSocketsClientInputhandler {
	private BufferedReader reader;
	//needed to parse the incoming json string
	private JSONParser jsonParser;
	private JSONObject jsonObject;

	
	public BasicSocketsClientInputhandlerJSON(BasicSocketsConnection connection) {
		super(connection);
		jsonParser = new JSONParser();
	}

	@Override
	public void prepare() {
		super.prepare();
		reader = new BufferedReader(new InputStreamReader(this.inputStream));
	}

	@Override
	protected void read() {
		String s;
		try {
			while ((s = reader.readLine()) != null) {
				parseAndCheck(s);
				Main.logger.debug("IN: FROM="+connection.getSocket().getInetAddress().getHostAddress()+"; "+"UUID="+jsonObject.get("UUID")+"; "+"TYPE="+jsonObject.get("TYPE")+"; "+((jsonObject.containsKey("COMMAND"))?("COMMAND="+ jsonObject.get("COMMAND")):""));
				
				messageType = (String) jsonObject.get("TYPE");
				if(messageType==null){
					messageType="";
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
//				case "NULL":
//					((SocketSearchClient)connection.getConnectionManager().getClient()).setStealNode(null);
//					break;
				case "TOKEN":
					receiveTerminationtoken();
				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
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
			synchronized (worker.getLoadbalancer().getLoadbalancingFlag()) {
				worker.sendResult();
				worker.getLoadbalancer().getLoadbalancingFlag().set(false);
				worker.getInstance().setRunning(false);
				worker.getLoadbalancer().getLoadbalancingFlag().notifyAll();
			//	worker.getWorkerthread().interrupt();
			}
			//((ClientOutputhandler<?>)connection.getConnectionManager().getServer().getOutput()).sendResult(((SocketSearchClient)connection.getConnectionManager().getClient()).amountOfWorkDone);
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public void receiveStealrequest() {
		super.receiveStealrequest();
		((BasicSocketsClientOutputhandler<JSONObject>)connection.getOutputhandler()).sendTasks();		
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
		System.out.println(returnArray.size());
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
				for (String ip :  worker.getInstances()) {
					((BasicSocketsClientOutputhandler<?>) connection.getConnectionManager().getConnection(ip).getOutputhandler()).sendCommand("TERMINATE");
				}
				return;
			}
			worker.getTermDet().getToken().notifyAll();
		}
	}

	@Override
	public void receiveEfficiencyRequest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateInstancesList() {
		// TODO Auto-generated method stub
		
	}

}
