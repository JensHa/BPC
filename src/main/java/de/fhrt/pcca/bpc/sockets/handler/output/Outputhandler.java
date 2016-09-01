package de.fhrt.pcca.bpc.sockets.handler.output;

public interface Outputhandler<T>{

	void write(T element);
	
	//void write();
}
