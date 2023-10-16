//region Imports
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.ObjectInputStream;
import java.io.IOException;
//endregion

class Server
{
	private static final int BROADCAST_PERIOD_IN_SECONDS = 1; /*
		Transmissões frequentes e desnecessárias podem causar congestão na rede, reduzindo a eficiência geral dela e
		potencialmente causando atrasos na entrega de mensagens e pacotes de internet. No entanto, quanto mais
		transmissões, maiores as chances dos pacotes do servidor alcançarem todos os nós da rede, o que significa
		que o resultado geral do sistema será mais preciso. Escolha a frequência de broadcast com sabedoria.
	*/
	public static final int SEND_PORT = 6969;
	public static final int LISTEN_PORT = 6968;

	public static boolean keep_listening = true; // Que tipo de linguagem usa "boolean" em vez de "bool" vsffff

	public static void main(String[] args) throws UnknownHostException
	{
		System.out.println("Server hosted at: " + InetAddress.getLocalHost().getHostAddress());

		PingNetwork   pingNetwork   = new PingNetwork(); // Envia pacotes para toda a rede (UDP);
		ListenNetwork listenNetwork = new ListenNetwork(); // Recebe pacotes dos clientes individualmente (TCP).

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(pingNetwork, 0, BROADCAST_PERIOD_IN_SECONDS, TimeUnit.SECONDS);
		listenNetwork.start();
	}
}

class PingNetwork implements Runnable
{
	@Override
	public void run()
	{
		try (DatagramSocket socket = new DatagramSocket())
		{
			socket.setBroadcast(true);
			String message = "ATTENDANCE_COUNT";
			InetAddress ip_broadcast = InetAddress.getByName("255.255.255.255"); // IP de broadcast UDP.

			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip_broadcast, Server.SEND_PORT);
			socket.send(packet);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

class ListenNetwork extends Thread implements Runnable /*
	Não me entenda errado. Não estou tentando me exibir e mostrar que sei usar multi-threading. O método para escutar a
	rede funciona num loop infinito, enquanto o método para pingar os nós da rede é executada periodicamente. Fazer tudo
	na mesma thread de execução bagunçaria a ordem das coisas e faria com que uma delas nem sequer fosse executada em
	primeiro lugar. A arquitetura do projeto (tanto do servidor quanto dos clientes) me obriga a usar mais de um fluxo
	de instruções.

	Mas não nego que gostei...
*/
{
	@Override
	public void run()
	{
		try(ServerSocket server_socket = new ServerSocket(Server.LISTEN_PORT))
		{
			while(Server.keep_listening)
			{
				Socket client_socket = server_socket.accept();
				ObjectInputStream input = new ObjectInputStream(client_socket.getInputStream());
				String message = (String)input.readObject();

				// Do something with the data message
				System.out.println(message);
			}
		}
		catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}