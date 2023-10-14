import java.io.DataInputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.IOException;

class Server
{
	public static int port = 6969;

	public static void main(String[] args)
	{
		ListenNetwork listenNetwork = new ListenNetwork(); listenNetwork.start();
	}

	public void pingNetwork() throws IOException
	{
		DatagramSocket ds = new DatagramSocket();
		String str = "Sou lindo";
		InetAddress ip = InetAddress.getByName("255.255.255.255");

		DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, port);
		ds.send(dp);
		ds.close();
	}
}

// Não entenda errado. Não estou tentando me exibir e mostrar que sei usar multi-threading. O método para escutar a
// rede funciona dentro de um loop while, enquanto a função para pingar os nós da rede, periodicamente. Fazer tudo na
// mesma thread de execução bagunçaria a ordem das coisas coisas. O projeto me obriga a usar outra thread.
class ListenNetwork extends Thread
{
	public static boolean keep_listening = true;

	public void run() { try  { listenNetwork(); }  catch (IOException e)  { throw new RuntimeException(e); } }

	void listenNetwork() throws IOException
	{
		ServerSocket server_socket = new ServerSocket(Server.port);

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