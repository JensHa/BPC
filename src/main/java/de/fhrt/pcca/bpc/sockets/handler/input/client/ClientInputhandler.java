package de.fhrt.pcca.bpc.sockets.handler.input.client;

public interface ClientInputhandler {
	//Note: All methods in this interface should be declared as "protected". 
	//This will available  in JDK 9 (see: http://mail.openjdk.java.net/pipermail/jdk9-dev/2015-March/001981.html
	void receiveStealrequest();
	void receiveSolver();
	void receiveTasks();
	void receiveTerminationtoken();
	void receiveCommand();
	void receiveEfficiencyRequest();
	void updateInstancesList();
}
