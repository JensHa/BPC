package de.fhrt.pcca.bpc.sockets.server;

import de.fhrt.pcca.bpc.sockets.Instance;
import de.fhrt.pcca.bpc.sockets.Worker;

public abstract class ServerWorker extends Worker{

	public ServerWorker(Instance instance) {
		super(instance);
	}

}
