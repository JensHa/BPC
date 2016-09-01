package de.fhrt.pcca.bpc.basicSockets.handler.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;

public class BasicSocketsDefaultInputhandler extends BasicSocketsInputhandler {
	BufferedReader reader;

	public BasicSocketsDefaultInputhandler(BasicSocketsConnection connection){
		super(connection);
	}

	public void prepare() {
		reader = new BufferedReader(new InputStreamReader(this.inputStream));
	}

	protected void read() {
		String s;
		try {
			while ((s = reader.readLine()) != null) {
				Main.logger.info("DefaultInputHandler: Received: " + s + " From :" + connection.getSocket().getRemoteSocketAddress().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
