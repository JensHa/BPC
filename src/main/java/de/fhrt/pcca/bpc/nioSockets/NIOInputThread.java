package de.fhrt.pcca.bpc.nioSockets;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOInputThread extends Thread{
	private ServerSocketChannel serverSocketChannel;
	private NIOConnectionManager myManager;
	private int port;
	private ByteBuffer buffer;
	public NIOInputThread(NIOConnectionManager parent, int port) {
		this.myManager=parent;
		this.port=port;
	}
	
	@Override
	public void run() {
		currentThread().setName("Input");
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.register(myManager.getInputSelector(), serverSocketChannel.validOps(), null);
			buffer = ByteBuffer.allocate(2048);
			while (true) {
				//Avoids deadlock
				myManager.getSelectorLock().lock();
				myManager.getSelectorLock().unlock();
				int noOfKeys = myManager.getInputSelector().select();
				if(noOfKeys == 0) continue; // in case of a wakeup
				Set<SelectionKey> selectedKeys = myManager.getInputSelector().selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();
				while (iter.hasNext()) {
					SelectionKey ky = iter.next();
					if (ky.isAcceptable()) {
						// Accept the new client connection
						SocketChannel client = serverSocketChannel.accept();
						myManager.createConnection(client);
						//System.out.println("Accepted new connection from client: " + client);
					} else if (ky.isReadable()) {
						// Read the data from client
						SocketChannel client = (SocketChannel) ky.channel();
						buffer.clear();
						int read=client.read(buffer);
						if (read==-1) {
							client.close();
							//System.out.println("Client messages are complete; close.");
							System.exit(0);
						}else{
							myManager.getConnection(client).getInputhandler().read(buffer);
						}
					}
					iter.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
}
