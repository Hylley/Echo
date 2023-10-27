package NetworkHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class Echo extends Thread implements Runnable /*
	Não me entenda errado. Não estou tentando me exibir e mostrar que sei usar multi-threading. O método para escutar a
	rede funciona num loop infinito, enquanto o método para pingar os nós da rede é executada periodicamente. Fazer tudo
	na mesma thread de execução bagunçaria a ordem das coisas e faria com que uma delas nem sequer fosse executada em
	primeiro lugar. A arquitetura do projeto (tanto do servidor quanto dos clientes) me obriga a usar mais de um fluxo
	de instruções.

	Mas não nego que gostei...
*/
{
	public final Socket socket;
	ObjectInputStream input;

	public final String id;
	static AtomicInteger echo_shutdown_count = new AtomicInteger(0); // Thread-safe API; eu fiz o meu dever de casa ;)

	public Echo(Socket socket, ObjectInputStream input, String username)
	{
		this.socket = socket;
		this.input = input;
		this.id = username;

		if(Server.debug) System.out.println("New client [" + id + "] connected at [" + socket.getInetAddress() + "]");
	}

	@Override
	public void run()
	{
		try
		{
			do
			{
				@SuppressWarnings("unchecked")
				HashMap<String, String> request = (HashMap<String, String>) input.readObject();
				NetworkHandler.Server.handle_request(request, this);
			}
			while(!socket.isClosed());
		}
		catch (SocketException e)
		{
			if(!socket.isClosed()) throw new RuntimeException(e);

			// Eu faria isso tão mais elegante se Java tivesse preprocessor....
			if(Server.debug) System.out.println("[" + echo_shutdown_count.incrementAndGet() + "/" + Server.connections() + "] Closing Echo socket successfully");
		}
		catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }
	}

	public static void send(Echo echo, Object packet)
	{
		try
		{
			ObjectOutputStream output = new ObjectOutputStream(echo.socket.getOutputStream());
			output.writeObject(packet);
		}
		catch (IOException e) { throw new RuntimeException(e); }
	}

	public static void shut(Echo echo)
	{
		HashMap<String, String> packet = new HashMap<>();
		packet.put("request_type", "SERVER_DISCONNECT");
		Echo.send(echo, packet);

		try { echo.socket.close(); }
		catch (IOException e) { throw new RuntimeException(e); }
	}
}
