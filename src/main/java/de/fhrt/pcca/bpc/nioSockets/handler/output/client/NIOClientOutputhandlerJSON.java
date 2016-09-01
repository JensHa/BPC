package de.fhrt.pcca.bpc.nioSockets.handler.output.client;

import java.io.PrintWriter;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;

public class NIOClientOutputhandlerJSON extends NIOClientOutputhandler<String> {
	ThreadLocal<JSONObject> jsonObject;
	ThreadLocal<JSONArray> jsonOuterArray;
	ThreadLocal<JSONArray> jsonInnerArray;
	ThreadLocal<JSONParser> jsonParser;

	public NIOClientOutputhandlerJSON(NIOConnection connection) {
		super(connection);
		// create an initial JSONObject for each thread
		jsonObject = new ThreadLocal<JSONObject>() {
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

		jsonParser = new ThreadLocal<JSONParser>() {
			@Override
			protected JSONParser initialValue() {
				return new JSONParser();
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void prepareMessage() {
		jsonObject.get().clear();
		jsonOuterArray.get().clear();
		jsonInnerArray.get().clear();
		jsonObject.get().put("UUID", UUID.randomUUID().toString());
	}

	// WS-Thread and one input thread can access this method => sync
	@Override
	public synchronized void write(String element) {
		Main.logger.debug("OUT: TO=" + connection.getIP() + "; " + element);
		buffer.clear();
		buffer.put(element.getBytes());
		buffer.flip();
		while (buffer.hasRemaining()) {
			try {
				connection.getSocketChannel().write(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public void sendResult(long o) {
		prepareMessage();
		jsonObject.get().put("TYPE", "RESULT");
		jsonObject.get().put("RESULT", o);
		write(jsonObject.get().toJSONString());
	}

	@Override
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
		write(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendStealrequest() {
		prepareMessage();
		jsonObject.get().put("TYPE", "COMMAND");
		jsonObject.get().put("COMMAND", "STEALREQUEST");
		write(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendJoinrequest() {
		prepareMessage();
		jsonObject.get().put("TYPE", "COMMAND");
		jsonObject.get().put("COMMAND", "REQUEST");
		write(jsonObject.get().toJSONString());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void sendStandbyJoinrequest() {
		prepareMessage();
		jsonObject.get().put("TYPE", "COMMAND");
		jsonObject.get().put("COMMAND", "REQUESTSTANDBY");
		write(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendTerminationtoken(boolean colour) {
		super.sendTerminationtoken(colour);
		prepareMessage();
		jsonObject.get().put("TYPE", "TOKEN");
		jsonObject.get().put("COLOUR", colour);
		write(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendCommand(String command) {
		prepareMessage();
		jsonObject.get().put("TYPE", "COMMAND");
		jsonObject.get().put("COMMAND", command);
		write(jsonObject.get().toJSONString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendEfficiency() {
		prepareMessage();
		jsonObject.get().put("TYPE", "EFFICIENCY");
		jsonObject.get().put("EFFICIENCY", worker.getHeuristic().getCPUTime());
		write(jsonObject.get().toJSONString());
	}
}
