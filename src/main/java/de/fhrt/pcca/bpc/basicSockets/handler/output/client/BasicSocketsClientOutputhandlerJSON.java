package de.fhrt.pcca.bpc.basicSockets.handler.output.client;

import java.io.PrintWriter;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;

public class BasicSocketsClientOutputhandlerJSON extends BasicSocketsClientOutputhandler<String> {
	ThreadLocal<JSONObject> jsonObject;
	ThreadLocal<JSONArray> jsonOuterArray;
	ThreadLocal<JSONArray> jsonInnerArray;
	ThreadLocal<JSONParser> jsonParser;
	volatile PrintWriter writer;

	public BasicSocketsClientOutputhandlerJSON(BasicSocketsConnection connection) {
		super(connection);
		//create an initial JSONObject for each thread
		jsonObject =  new ThreadLocal<JSONObject>() {
		    @Override 
		    protected JSONObject initialValue() {
		        return new JSONObject();
		    }
		};    
		jsonOuterArray = new ThreadLocal<JSONArray>() {
		    @Override 
		    protected JSONArray initialValue() {
		        return new JSONArray();
		    }
		}; 
		jsonInnerArray = new ThreadLocal<JSONArray>() {
		    @Override 
		    protected JSONArray initialValue() {
		        return new JSONArray();
		    }
		}; 
		
		jsonParser = new ThreadLocal<JSONParser>(){
			@Override
			protected JSONParser initialValue() {
				return new JSONParser();
			}
		};
	}

	@Override
	protected void prepare() {
		super.prepare();
		this.writer = new PrintWriter(this.outputStream, true);
	}
	
	@SuppressWarnings("unchecked")
	private void prepareMessage() {
		jsonObject.get().clear();
		jsonOuterArray.get().clear();
		jsonInnerArray.get().clear();
		jsonObject.get().put("UUID", UUID.randomUUID().toString());		
	}

	@Override
	public void write(String element) {
		// only write if we have a writer
		while (writer == null) {}
		JSONObject removed = null;
		try {
			removed = (JSONObject) jsonParser.get().parse(element);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		writer.println(removed);
		Main.logger.debug("OUT: TO=" + connection.getSocket().getInetAddress().getHostAddress() + "; "+ removed.get("TYPE"));
	}

	@SuppressWarnings("unchecked")
	public void sendResult(long o) {
		prepareMessage();
		jsonObject.get().put("TYPE", "RESULT");
		jsonObject.get().put("RESULT", o);
		addToOutputbuffer(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	public void sendTasks() {
		prepareMessage();
		jsonObject.get().put("TYPE", "TASKS");
		for (Task task : removeTasksFromClient()) {
			jsonInnerArray.set(new JSONArray());
			for (int i = 0; i < task.getNodes().length; i++) {
				jsonInnerArray.get().add(task.getNodes()[i]);
			}
			jsonOuterArray.get().add(jsonInnerArray.get());
		}
		jsonObject.get().put("TASKS", jsonOuterArray.get());
		addToOutputbuffer(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendStealrequest() {
		prepareMessage();
		jsonObject.get().put("TYPE", "COMMAND");
		jsonObject.get().put("COMMAND", "STEALREQUEST");
		addToOutputbuffer(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendJoinrequest() {
		prepareMessage();
		jsonObject.get().put("TYPE", "COMMAND");
		jsonObject.get().put("COMMAND", "REQUEST");
		addToOutputbuffer(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendTerminationtoken(boolean colour) {
		super.sendTerminationtoken(colour);
		prepareMessage();
		jsonObject.get().put("TYPE", "TOKEN");
		jsonObject.get().put("COLOUR", colour);
		addToOutputbuffer(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendCommand(String command) {
		prepareMessage();
		jsonObject.get().put("TYPE","COMMAND");
		jsonObject.get().put("COMMAND", command);
		addToOutputbuffer(jsonObject.get().toJSONString());
	}

	@Override
	public void sendEfficiency() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendStandbyJoinrequest() {
		// TODO Auto-generated method stub
		
	}
}
