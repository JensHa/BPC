package de.fhrt.pcca.bpc.sockets.handler.input.server;

public interface ServerInputhandler {
	//Note: All methods in this interface should be declared as "protected". 
	//This will available  in JDK 9 (see: http://mail.openjdk.java.net/pipermail/jdk9-dev/2015-March/001981.html
	void receiveCommand();
	void receiveResult();
	void receiveEfficiency();
}
