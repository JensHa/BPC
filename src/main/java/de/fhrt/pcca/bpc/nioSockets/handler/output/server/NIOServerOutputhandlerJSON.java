package de.fhrt.pcca.bpc.nioSockets.handler.output.server;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.sockets.util.HashTreeSolver;

public class NIOServerOutputhandlerJSON extends NIOServerOutputhandler<String> {
	//needed to make a json string out of an java object
	JSONObject jsonObject;
	JSONArray jsonArray;
	String commandToSend;
	
	public NIOServerOutputhandlerJSON(NIOConnection connection) {
		super(connection);
		this.jsonObject=new JSONObject();
		this.jsonArray= new JSONArray();
	}


	@SuppressWarnings("unchecked")
	private void prepareMessage() {
		jsonObject.clear();
		jsonArray.clear();
		jsonObject.put("UUID", UUID.randomUUID().toString());		
	}
	
	@Override
	public synchronized void write(String element) {
		buffer.clear();
		buffer.put(element.getBytes());
		buffer.flip();
		while(buffer.hasRemaining()) {
		    try {
				connection.getSocketChannel().write(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		// only write if we have a writer
//		while (writer == null) {}
//		writer.println(element);
//		//writer.println(jsonObject.toJSONString());
//		//Main.logger.debug("OUT: TO=" + connection.getSocket().getInetAddress().getHostAddress() + "; "+ jsonObject.toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendSolver(HashTreeSolver solver) {
		prepareMessage();
		jsonObject.put("TYPE", "SOLVER");
		jsonObject.put("INITIALTASKS", solver.getProperties().getInitalNodes());;
		jsonObject.put("INCREASEFACTORFORINITIALTASKS", solver.getProperties().getIncreasefactorForInitaltasks());
		jsonObject.put("AMOUNTOFCHILDREN", solver.getProperties().getAmountOfChildren());
		jsonObject.put("SIZEOFBIGTASKS",solver.getProperties().getSizeOfBigTasks());
		jsonObject.put("MYID", solver.getProperties().getMyID());
		for(String IP:solver.getProperties().getAllClients()){
		jsonArray.add(IP);	
		}
		jsonObject.put("ALLCLIENTS",jsonArray);
		jsonObject.put("MAXNODESINLOCALQUEUE", solver.getProperties().getMaxNodesInLocalQueue());
		jsonObject.put("MAXTASKSTOIMPORT", solver.getProperties().getMaxTasksToImport());
		jsonObject.put("MAXNODESPERTASK", solver.getProperties().getMaxNodesPerTask());
		jsonObject.put("MAXTASKSTOSTEAL",  solver.getProperties().getMaxTasksToSteal());
		write(jsonObject.toJSONString());
//		addToOutputbuffer(jsonObject.toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendSuccessfullJoin() {
		prepareMessage();
		jsonObject.put("TYPE","COMMAND");
		jsonObject.put("COMMAND", "YOUAREIN");
		write(jsonObject.toJSONString());
//		addToOutputbuffer(jsonObject.toJSONString());	
	}

	@Override
	public void sendDeniedJoin(String reason) {
//		prepareMessage();
//		switch (reason) {
//		case "IP":
//			jsonObject.put("TYPE","COMMAND");
//			jsonObject.put("COMMAND", "IPALREADYINUSE");
//			addToOutputbuffer(jsonObject.toJSONString());	
//			break;
//		default:
//			break;
//		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void requestClientEfficiency() {
		prepareMessage();
		jsonObject.put("TYPE", "COMMAND");
		jsonObject.put("COMMAND", "EFFICIENCYREQUEST");
		write(jsonObject.toJSONString());
	}


	@SuppressWarnings("unchecked")
	@Override
	public void updateInstancesList(List<String> instances) {
		prepareMessage();
		jsonObject.put("TYPE", "INSTANCES");
		for(String IP:instances){
		jsonArray.add(IP);	
		}
		jsonObject.put("INSTANCES",jsonArray);	
		write(jsonObject.toJSONString());
	}
	

}
