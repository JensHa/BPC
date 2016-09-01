package de.fhrt.pcca.bpc.nioSockets.handler.input;

import java.nio.ByteBuffer;

import de.fhrt.pcca.bpc.nioSockets.NIOConnection;

public class NIODefaultInputhandler extends NIOInputhandler {

	public NIODefaultInputhandler(NIOConnection connection){
		super(connection);
	}


	@Override
	public void read(ByteBuffer buffer) {
		//TODO: fill
	}
}
