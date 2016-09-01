package de.fhrt.pcca.bpc.sockets.util;

import org.apache.logging.log4j.Level;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.sockets.client.ClientWorker;
import de.fhrt.pcca.bpc.sockets.client.SocketSearchClient;

public class StatPrinter extends Thread {
	int interval;
	ClientWorker statHolder;
	private volatile boolean running;

	public StatPrinter(int interval, ClientWorker statHolder) {
		this.interval = interval;
		this.statHolder = statHolder;
		this.running=true;
	}
	
	public void setIsRunning(boolean value){
		this.running=value;
	}

	@Override
	public void run() {
		while(!statHolder.isRunning()){}
		while (running) {
			int internalqueuesize = statHolder.getInternal().size();
			int sharedqueuesize = statHolder.getSharedQueue().size();
			long workdone=((SocketSearchClient)statHolder).getAmountOfWorkDone();
			Main.statsLogger.log(Level.getLevel("STATS"), internalqueuesize + ";" + sharedqueuesize + ";"+workdone+";"
					+ ((ClientWorker) statHolder.getInstance().getWorker()).getTermDet().getColour() + ";" + ((ClientWorker) statHolder.getInstance().getWorker()).getTermDet().getToken());
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
