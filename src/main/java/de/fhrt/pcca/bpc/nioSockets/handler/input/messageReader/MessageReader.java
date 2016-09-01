package de.fhrt.pcca.bpc.nioSockets.handler.input.messageReader;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class MessageReader<T> {
	ArrayList<T> unprocessedMessages;
	protected byte[] readCache;
	int i = 0;
	byte lastByte = 0;

	public MessageReader() {
		this.unprocessedMessages = new ArrayList<T>();
		this.readCache = new byte[2048];
	}

	public abstract ArrayList<T> readByteBuffer(ByteBuffer buffer);
	
	protected abstract T createMessage();

	public void clearUnreadMessages() {
		unprocessedMessages.clear();
	}
}
