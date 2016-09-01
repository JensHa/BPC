package de.fhrt.pcca.bpc.nioSockets.handler.input.messageReader;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class JSONMessageReader extends MessageReader<String>{
	int bracketCount = 0;

	@Override
	protected String createMessage() {
		return new String(readCache).trim();
	}

	@Override
	public ArrayList<String> readByteBuffer(ByteBuffer buffer) {
		buffer.flip();
		while (buffer.hasRemaining()) {

			boolean messageFull = false;
			while (true) {
				try {
					lastByte = buffer.get();
					readCache[i++] = lastByte;
					if (lastByte == 123) {
						bracketCount++;
					} else if (lastByte == 125) {
						bracketCount--;
					}
					if (bracketCount == 0) {
						messageFull = true;
						break;
					}
				} catch (BufferUnderflowException e) {
					break;
				}
			}
			if (messageFull) {
				unprocessedMessages.add(createMessage());
				Arrays.fill(readCache, (byte) 0);
				i = 0;

			}
		}
		buffer.clear();
		return unprocessedMessages;
	}
}
