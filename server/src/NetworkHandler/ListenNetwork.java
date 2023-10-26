package NetworkHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public final class ListenNetwork extends Thread implements Runnable
{
	private final ServerSocket server_socket;
	private final Server server_instance;

	public ListenNetwork(ServerSocket server_socket, Server server_instance)
	{
		this.server_socket = server_socket;
		this.server_instance = server_instance;
	}

	@Override
	public void run()
	{
		while(!server_socket.isClosed())
		{
			try(Socket new_client_connection = server_socket.accept())
			{
				BufferedReader input = new BufferedReader(new InputStreamReader(new_client_connection.getInputStream()));
				String username = input.readLine();
				server_instance.connect(new_client_connection, username);
			}
			catch (IOException e) { throw new RuntimeException(e); }
		}
	}

	public void shut()
	{
		try { server_socket.close(); }
		catch (IOException e) { throw new RuntimeException(e); }
		if(Server.debug) System.out.println("Listen network shutdown");
	}
}