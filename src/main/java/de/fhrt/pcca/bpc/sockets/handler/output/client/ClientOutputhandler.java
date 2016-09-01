package de.fhrt.pcca.bpc.sockets.handler.output.client;

import java.util.LinkedList;

import de.fhrt.pcca.bpc.sockets.client.loadbalance.stack.Task;

public interface ClientOutputhandler {
	void sendJoinrequest();
	void sendResult(long result);
	void sendTerminationtoken(boolean colour);
	void sendCommand(String string);
	void sendStealrequest();
	void sendTasks();
	LinkedList<Task> removeTasksFromClient();
	void sendEfficiency();
	void sendStandbyJoinrequest();
}
