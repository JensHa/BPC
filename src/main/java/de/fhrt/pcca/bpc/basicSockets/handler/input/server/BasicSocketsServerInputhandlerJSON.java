package de.fhrt.pcca.bpc.basicSockets.handler.input.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;
import de.fhrt.pcca.bpc.basicSockets.handler.output.server.BasicSocketsServerOutputhandler;
import de.fhrt.pcca.bpc.sockets.server.SocketSearchServer;


public class BasicSocketsServerInputhandlerJSON extends BasicSocketsServerInputhandler {
	BufferedReader reader;
	//needed to parse the incoming json string
	JSONParser jsonParser;
	JSONObject jsonObject;

	public BasicSocketsServerInputhandlerJSON(BasicSocketsConnection connection) {
		super(connection);
		this.jsonParser = new JSONParser();
	}

	@Override
	public void prepare() {
		super.prepare();
		reader = new BufferedReader(new InputStreamReader(this.inputStream));
	}

	@Override
	protected void read() {
		String s;
		try {
			while ((s = reader.readLine()) != null) {
				parseAndCheck(s);
				messageType = (String) jsonObject.get("TYPE");
				switch (messageType) {
				case "COMMAND":
					receiveCommand();
					break;
				case "RESULT":
					receiveResult();
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			//TODO: clean final
			e.printStackTrace();
		}				
	}
	
	private void parseAndCheck(String s) {
		try {
			jsonObject = (JSONObject) jsonParser.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (!jsonObject.containsKey("UUID")) {
			Main.logger.error("ERROR: Message has no UUID");
			System.exit(1);
		}		
	}

	public void receiveResult() {
		((SocketSearchServer)connection.getConnectionManager().getInstance().getWorker()).addToResult((Long) jsonObject.get("RESULT"));
	}
	
	public void receiveCommand() {
		commandReceived=(String)jsonObject.get("COMMAND");
		switch (commandReceived){
		case "REQUEST":
			for (String oneClient : worker.getInstances()) {
				if (oneClient.equals(connection.getSocket().getInetAddress().getHostAddress())) {
					((BasicSocketsServerOutputhandler<?>)connection.getOutputhandler()).sendDeniedJoin("IP");
					return;
				}
			}
			worker.addClient(connection.getSocket().getInetAddress().getHostAddress());
			Main.logger.info("There are now " + worker.getInstances().size() + " clients registered");
			((BasicSocketsServerOutputhandler<?>)connection.getOutputhandler()).sendSuccessfullJoin();
			break;
		default:
			break;
		}
	}

	@Override
	public void receiveEfficiency() {
		// TODO Auto-generated method stub
		
	}
}
