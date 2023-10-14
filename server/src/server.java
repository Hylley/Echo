import java.net.*;
import java.util.Enumeration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;

class Server
{
	private static final int PING_PERIOD_IN_SECONDS = 5;

	public static int send_port = 6969;
	public static int listen_port = 6968;

	public static void main(String[] args) throws UnknownHostException, SocketException {

		System.out.println("Server on: " + InetAddress.getLocalHost().getHostAddress());

		PingNetwork ping_network = new PingNetwork();
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(ping_network, 0, PING_PERIOD_IN_SECONDS, TimeUnit.SECONDS);

		ListenNetwork listenNetwork = new ListenNetwork();
		listenNetwork.start();
	}
}

class PingNetwork implements Runnable
{
	public void run() { try { pingNetwork(); } catch (IOException e) { throw new RuntimeException(e); } }

	public void pingNetwork() throws IOException
	{
		System.out.println("Ping nodes: ");
		DatagramSocket socket = new DatagramSocket();
		String message = "ATTENDANCE_COUNT";
		InetAddress ip_broadcast = InetAddress.getByName("255.255.255.255"); // IP de broadcast UDP.

		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip_broadcast, Server.send_port);
		socket.send(packet);
		socket.close();
	}
}

// Não entenda errado. Não estou tentando me exibir e mostrar que sei usar multi-threading. O método para escutar a
// rede funciona num loop infinito, enquanto o método para pingar os nós da rede é executada periodicamente. Fazer tudo
// na mesma thread de execução bagunçaria a ordem das coisas e faria com que uma delas nem sequer fosse executada em
// primeiro lugar. A arquitetura do projeto (pelo menos a do servidor) me obriga a usar mais de um fluxo de instruções.
// Mas não nego que gostei disso.
class ListenNetwork extends Thread
{
	public static boolean keep_listening = true;

	public void run() { try  { listenNetwork(); }  catch (Exception e)  { throw new RuntimeException(e); } }

	void listenNetwork() throws Exception
	{
		try(DatagramSocket server_socket = new DatagramSocket(Server.listen_port))
		{
			server_socket.setBroadcast(true);
			byte[] data_buffer = new byte[1024];

			while (keep_listening) {
				DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
				server_socket.receive(received_packet);

				String message = new String(received_packet.getData(), received_packet.getOffset(), received_packet.getLength());

				// Do something with the data message
				System.out.println(message);
			}
		}
	}
}