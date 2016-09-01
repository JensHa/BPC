package de.fhrt.pcca.bpc.nioSockets.handler.input.server;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.fhrt.pcca.bpc.Main;
import de.fhrt.pcca.bpc.nioSockets.NIOConnection;
import de.fhrt.pcca.bpc.nioSockets.handler.input.messageReader.JSONMessageReader;
import de.fhrt.pcca.bpc.sockets.handler.output.server.ServerOutputhandler;
import de.fhrt.pcca.bpc.sockets.server.SocketSearchServer;

public class NIOServerInputhandlerJSON extends NIOServerInputhandler {
	// needed to parse the incoming json string
	JSONParser jsonParser;
	JSONObject jsonObject;
	NIOConnection currentSender;

	public NIOServerInputhandlerJSON(NIOConnection connection) {
		super(connection);
		this.jsonParser = new JSONParser();
		this.messageReader = new JSONMessageReader();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(ByteBuffer buffer) {
		ArrayList<String> list = (ArrayList<String>) messageReader.readByteBuffer(buffer);
		for (String input : list) {
			// try {
			// System.out.println(((JSONObject)
			// jsonParser.parse(input)).toJSONString());
			// } catch (ParseException e) {
			// e.printStackTrace();
			// }
			parseAndCheck(input);
			messageType = (String) jsonObject.get("TYPE");
			switch (messageType) {
			case "COMMAND":
				receiveCommand();
				break;
			case "RESULT":
				receiveResult();
				break;
			case "EFFICIENCY":
				receiveEfficiency();
			default:
				break;
			}
		}
		messageReader.clearUnreadMessages();
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

	@Override
	public void receiveResult() {
		((SocketSearchServer) connection.getConnectionManager().getInstance().getWorker())
				.addToResult((Long) jsonObject.get("RESULT"));
	}

	@Override
	public void receiveCommand() {
		commandReceived = (String) jsonObject.get("COMMAND");
		switch (commandReceived) {
		case "REQUEST":
			for (String oneClient : worker.getInstances()) {

				if (oneClient.equals(connection.getIP())) {
					((ServerOutputhandler) connection.getOutputhandler()).sendDeniedJoin("IP");
					return;
				}
			}
			if (worker.isRunning()) {
				worker.addStandbyClient(connection.getIP());
				Main.logger.info("There are now " + worker.getInstances().size() + " clients registered (Standby)");
			} else {
				worker.addClient(connection.getIP());
				Main.logger.info("There are now " + worker.getInstances().size() + " clients registered");
			}
			((ServerOutputhandler) connection.getOutputhandler()).sendSuccessfullJoin();
			break;

		case "REQUESTSTANDBY":
			worker.addStandbyClient(connection.getIP());
			Main.logger.info("There are now " + worker.getStandyInstances().size() + " clients registered (Standby)");
			((ServerOutputhandler) connection.getOutputhandler()).sendSuccessfullJoin();
			break;
		default:
			break;
		}
	}

	@Override
	public void receiveEfficiency() {
		worker.getHeuristic().setNewEfficiency((Double) jsonObject.get("EFFICIENCY"), connection.getIP());
	}
}
