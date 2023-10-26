package NetworkHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class PingNetwork extends Thread implements Runnable
{
	private final int DISCOVERY_BROADCAST_PERIOD;
	private final int SEND_PORT;

	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public PingNetwork(int DISCOVERY_BROADCAST_PERIOD, int SEND_PORT)
	{
		this.DISCOVERY_BROADCAST_PERIOD = DISCOVERY_BROADCAST_PERIOD;
		this.SEND_PORT = SEND_PORT;
	}

	@Override
	public void run()
	{
		executor.scheduleAtFixedRate(() -> this.broadcast("DISCOVERY"), 0, this.DISCOVERY_BROADCAST_PERIOD, TimeUnit.SECONDS);
	}

	public void broadcast(Object message)
	{
		try( DatagramSocket socket = new DatagramSocket() )
		{
			socket.setBroadcast(true);
			InetAddress ip_broadcast = InetAddress.getByName("255.255.255.255"); // IP de broadcast UDP.

			ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();
			ObjectOutputStream output_stream = new ObjectOutputStream(byte_stream);
			output_stream.writeObject(message); output_stream.flush();
			byte[] byte_array = byte_stream.toByteArray();

			DatagramPacket packet = new DatagramPacket(byte_array, byte_array.length, ip_broadcast, SEND_PORT);
			socket.send(packet);
		}
		catch (IOException e) { throw new RuntimeException(e); }

		if(Server.debug) System.out.println("Broadcast: [" + message + "]");
	}

	public void shut()
	{
		executor.shutdown();
		if(Server.debug) System.out.println("Ping network shutdown");
	}
}
