import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.IOException;

class Server
{
	public static int send_port = 6969;
	public static int listen_port = 6968;

	public static void main(String[] args)
	{
		PingNetwork ping_network = new PingNetwork();

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(ping_network, 0, 3, TimeUnit.SECONDS);

		ListenNetwork listenNetwork = new ListenNetwork(); listenNetwork.start();
	}
}

class PingNetwork implements Runnable
{
	public void run() { try { pingNetwork(); } catch (IOException e) { throw new RuntimeException(e); } }

	public void pingNetwork() throws IOException
	{
		DatagramSocket socket = new DatagramSocket();
		String str = "Sou lindo";
		InetAddress ip_broadcast = InetAddress.getByName("255.255.255.255");

		DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip_broadcast, Server.send_port);
		socket.send(dp);
		socket.close();
	}
}

// Não entenda errado. Não estou tentando me exibir e mostrar que sei usar multi-threading. O método para escutar a
// rede funciona num loop infinito, enquanto a função para pingar os nós da rede é executada periodicamente. Fazer tudo
// na mesma thread de execução bagunçaria a ordem das coisas e faria com que uma delas nem sequer fosse executada em
// primeiro lugar. A arquitetura do projeto (pelo menos a do servidor) me obriga a usar mais de um fluxo de instruções.
class ListenNetwork extends Thread
{
	public static boolean keep_listening = true;

	public void run() { try  { listenNetwork(); }  catch (Exception e)  { throw new RuntimeException(e); } }

	void listenNetwork() throws Exception
	{
		ServerSocket server_socket = new ServerSocket(Server.listen_port);

		while(keep_listening)
		{
			Socket socket = server_socket.accept();
			DataInputStream data_input = new DataInputStream(socket.getInputStream());

			String data = data_input.readUTF();
			System.out.println(data);
			socket.close();
		}
	}
}