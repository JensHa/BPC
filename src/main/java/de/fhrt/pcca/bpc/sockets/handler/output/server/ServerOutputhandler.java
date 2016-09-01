package de.fhrt.pcca.bpc.sockets.handler.output.server;

import java.util.ArrayList;
import java.util.List;

import de.fhrt.pcca.bpc.sockets.util.HashTreeSolver;

public interface ServerOutputhandler{
	public void sendSolver(HashTreeSolver solver);
	public void sendSuccessfullJoin();
	public void sendDeniedJoin(String reason);
	public void requestClientEfficiency();
	public void updateInstancesList(List<String> list);
}
