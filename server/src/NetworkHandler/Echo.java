package NetworkHandler;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

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
	public final String id;
	static int echo_count = 0;

	public Echo(Socket socket, String username)
	{
		this.socket = socket;
		this.id = username;

		if(Server.debug) System.out.println("New client [" + id + "] connected at [" + socket.getInetAddress() + "]");
	}

	@Override
	public void run()
	{
		try(ObjectInputStream input = new ObjectInputStream(socket.getInputStream()))
		{
			while(!socket.isClosed())
			{
				@SuppressWarnings("unchecked")
				HashMap<String, String> request = (HashMap<String, String>) input.readObject();
				NetworkHandler.Server.handle_request(request, this);
			}
		}
		catch (SocketException e)
		{
			echo_count++;
			if(!socket.isClosed()) throw new RuntimeException(e);
			if(Server.debug) System.out.println("[" + echo_count + "/" + Server.connections() + "] Closing Echo socket successfully");
		}
		catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }
	}

	public static void send(Echo echo, Object packet)
	{
		try(ObjectOutputStream output = new ObjectOutputStream(echo.socket.getOutputStream()))
		{
			output.writeObject(packet);
		}
		catch (IOException e) { throw new RuntimeException(e); }
	}

	public static void shut(Echo echo)
	{
		try { echo.socket.close(); }
		catch (IOException e) { throw new RuntimeException(e); }
	}
}
