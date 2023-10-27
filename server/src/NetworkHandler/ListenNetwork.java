package NetworkHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

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
		do
		{
			try
			{
				Socket new_client_connection = server_socket.accept();
				ObjectInputStream input = new ObjectInputStream(new_client_connection.getInputStream());
				String id = (String) input.readObject();
				if(Server.debug) System.out.println("[" + new_client_connection.getInetAddress() + " ] is trying to connect as [" + id + "]");
				server_instance.connect(new_client_connection, input, id);
			}
			catch(SocketException e)
			{
				if(!server_socket.isClosed()) throw new RuntimeException(e);
				if(Server.debug) System.out.println("Closing ServerSocket successfully");
			}
			catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }
		}
		while(!server_socket.isClosed());
	}

	public void shut()
	{
		try { server_socket.close(); }
		catch (IOException e) { throw new RuntimeException(e); }
		if(Server.debug) System.out.println("Closing ListenNetwork successfully");
	}
}