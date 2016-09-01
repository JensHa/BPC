package de.fhrt.pcca.bpc.basicSockets.handler.output;

import java.io.PrintWriter;

import de.fhrt.pcca.bpc.basicSockets.BasicSocketsConnection;



public class BasicSocketsDefaultOutputhandler extends BasicSocketsOutputhandler<Object> {
	PrintWriter writer;

	public BasicSocketsDefaultOutputhandler(BasicSocketsConnection connection) {
		super(connection);
	}

	@Override
	public void prepare() {
		// TODO Auto-generated method stub
		super.prepare();
		this.writer = new PrintWriter(this.outputStream, true);
	}
	
	@Override
	public void write(Object o) {
		//String s = outputBuffer.remove().toString();
		synchronized (this) {
			while(writer==null){}
			writer.println(o);
		}
	}
}
