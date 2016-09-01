package de.fhrt.pcca.bpc.sockets;

import de.fhrt.pcca.bpc.sockets.handler.output.Outputhandler;

public interface Connection {

	Outputhandler<?> getOutputhandler();

}
