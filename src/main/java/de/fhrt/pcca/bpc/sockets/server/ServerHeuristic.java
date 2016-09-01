package de.fhrt.pcca.bpc.sockets.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.sockets.Instance;
import de.fhrt.pcca.bpc.sockets.handler.output.server.ServerOutputhandler;

public class ServerHeuristic extends Thread {
	HashMap<String, ArrayList<Double>> efficiencys;
	int sleeptime;
	double efficiencyLimit;
	SocketSearchServer worker;
	private long lastTimeNodeStarted;

	public ServerHeuristic(SocketSearchServer worker, int sleeptime) {
		this.worker = worker;
		this.efficiencys = new HashMap<String, ArrayList<Double>>();
		this.sleeptime = sleeptime;
		this.efficiencyLimit=Double.valueOf(Main.properties.getProperty("efficiencyLimitInc"));
	}

	public void setNewEfficiency(Double efficiency, String ip) {
		if (efficiencys.containsKey(ip)) {
			efficiencys.get(ip).add(efficiency);
		} else {
			efficiencys.put(ip, new ArrayList<Double>());
		}
	}

	private void checkForClusterChange() {		boolean enoughStats = true;
		double avgEff = 0;
		Iterator<Entry<String, ArrayList<Double>>> it = efficiencys.entrySet().iterator();
		while (it.hasNext()) {
			ArrayList<Double> currentList = it.next().getValue();
			if (currentList.size() < Integer.valueOf(Main.properties.getProperty("decisionMultiplicator"))) {
				enoughStats = false;
				break;
			}
			for (int i = 0; i < Integer.valueOf(Main.properties.getProperty("decisionMultiplicator")); i++) {
				avgEff += currentList.get(currentList.size() - 1 - i);
			}
		}
		avgEff = avgEff / (Integer.valueOf(Main.properties.getProperty("decisionMultiplicator")) * efficiencys.size());
		if(enoughStats){
			Main.logger.debug("Current system efficiency is: " + avgEff);

		}
		if (enoughStats && avgEff >=efficiencyLimit) {
			startNewInstance();
		}
	}

	public void startNewInstance() {
		if (worker.getStandyInstances().size() == 0) {
			return;
		}
		Main.logger.debug("Starting new instance");
		String client = worker.getStandyInstances().remove(0);
		worker.addClient(client);
		// TODO: IMPORTANT!!! possible race condition
		// server sent new instances list to all old instances -> one message
		// might wait in the buffer at the rec.
		// -> new instance starts and sends a stealreq to the "buffer node" ->
		// this node is idle and sends the token in the OLD ring
		for (String IP : worker.getInstances()) {
			((ServerOutputhandler) worker.getInstance().getConnectionManager().getConnection(IP).getOutputhandler()).updateInstancesList(worker.getInstances());
		}
		efficiencys.put(client, new ArrayList<Double>());
		worker.sendSolverToChild(client);
		
		lastTimeNodeStarted = System.currentTimeMillis();
		// 1) start new instance
		// 2) deploy jar
		// 3) start jar
		// 4) jar connects to server
		// 5) distribute information about new client
	}

	@Override
	public void run() {
		while (worker.getInstance().getConnectionManager().isActive()) {
		
			worker.getClientEfficiency();
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// if(System.currentTimeMillis()>(lastTimeNodeStarted+(Integer.valueOf(Main.properties.getProperty("waitTimeBeforeNewNode"))*1000))){
			checkForClusterChange();
			// }
		}
	}

}
