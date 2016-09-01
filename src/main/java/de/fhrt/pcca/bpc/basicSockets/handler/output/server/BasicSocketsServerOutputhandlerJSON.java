package de.fhrt.pcca.bpc.basicSockets.handler.output.server;

import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.sockets.util.HashTreeSolver;

public class BasicSocketsServerOutputhandlerJSON extends BasicSocketsServerOutputhandler<String> {
	volatile PrintWriter writer;
	//needed to make a json string out of an java object
	JSONObject jsonObject;
	JSONArray jsonArray;
	String commandToSend;
	
	public BasicSocketsServerOutputhandlerJSON(BasicSocketsConnection connection) {
		super(connection);
		this.jsonObject=new JSONObject();
		this.jsonArray= new JSONArray();
	}

	@Override
	public void prepare() {
		super.prepare();
		this.writer = new PrintWriter(this.outputStream, true);
	}
	
	@SuppressWarnings("unchecked")
	private void prepareMessage() {
		jsonObject.clear();
		jsonArray.clear();
		jsonObject.put("UUID", UUID.randomUUID().toString());		
	}
	
	@Override
	public void write(String element) {
		// only write if we have a writer
		while (writer == null) {}
		writer.println(element);
		//writer.println(jsonObject.toJSONString());
		Main.logger.debug("OUT: TO=" + connection.getSocket().getInetAddress().getHostAddress() + "; "+ jsonObject.toJSONString());
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
		addToOutputbuffer(jsonObject.toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendSuccessfullJoin() {
		prepareMessage();
		jsonObject.put("TYPE","COMMAND");
		jsonObject.put("COMMAND", "YOUAREIN");
		addToOutputbuffer(jsonObject.toJSONString());	
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendDeniedJoin(String reason) {
		prepareMessage();
		switch (reason) {
		case "IP":
			jsonObject.put("TYPE","COMMAND");
			jsonObject.put("COMMAND", "IPALREADYINUSE");
			addToOutputbuffer(jsonObject.toJSONString());	
			break;
		default:
			break;
		}
	}

	@Override
	public void requestClientEfficiency() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateInstancesList(List<String> list) {
		// TODO Auto-generated method stub
		
	}
}
