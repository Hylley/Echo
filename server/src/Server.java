import javax.swing.*;
import java.io.*;
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;


public final class Server
{
	private static final int BROADCAST_PERIOD_IN_SECONDS = 1; /*
		Transmissões frequentes e desnecessárias podem causar congestão na rede, reduzindo a eficiência geral dela e
		potencialmente causando atrasos na entrega de mensagens e pacotes de internet. No entanto, quanto mais
		transmissões, maiores as chances dos pacotes do servidor alcançarem todos os nós da rede, o que significa
		que o resultado geral do sistema será mais preciso. Escolha a frequência de broadcast com sabedoria.
	*/
	public static final int SEND_PORT = 6969;
	public static final int LISTEN_PORT = 6968;

	public static ServerSocket server_socket;
	public static List<InetAddress> tcp_clients = new CopyOnWriteArrayList<>(); // Thread-safe API. Eu fiz o dever de casa ;)

	PingNetwork pingNetwork = new PingNetwork(); // Envia pacotes para toda a rede (UDP) —> thread principal;
	ListenNetwork listenNetwork = new ListenNetwork(); // Recebe pacotes dos clientes individualmente (TCP) —> thread paralela.
	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public Server()
	{
		try
		{
			Server.server_socket = new ServerSocket(Server.LISTEN_PORT);

			if(Main.debug) // Como diabos eu uso guard-clauses dentro de um try-catch??
			{
				System.out.println("Hosting at:  	" + InetAddress.getLocalHost().getHostAddress() + " " + SEND_PORT);
				System.out.println("Listening at:	" + InetAddress.getLocalHost().getHostAddress() + " " + LISTEN_PORT);
			}
		}
		catch (IOException e) { throw new RuntimeException(e); }

		executor.scheduleAtFixedRate(pingNetwork, 0, BROADCAST_PERIOD_IN_SECONDS, TimeUnit.SECONDS);
		listenNetwork.start();
	}

	public void send_package(InetAddress address, String data)
	{

	}

	public static byte[] get_data_register_binaries() /*
		Esse método geralmente só é utilizado pela classe Main, que transforma os bytes em um código QR e exibe numa
		janela nova. O cliente, por sua vez, escaneia o código através do aplicativo e obtém todos os dados necessários
		para identificar o servidor na rede e determinar o conteúdo de corpo necessário para enviar um pacote de
		registro. O usuário insere os dados, o pacote é enviado e, se aceito, passa a constar no painel de presença.
	*/
	{
		try
		{
			HashMap<String, String> config = new HashMap<>(); /*
				Não precisa me julgar. Eu já faço isso por conta própria.

				Serialização pode ser feita com literalmente qualquer objeto. Escolhi HashMaps por ser o mais próximo de
				um JSON comum, portanto, é mais legível e padronizado com a web, embora ambos sejam métodos ineficientes
				de armazenamento e manipulação de dados. Isso torna o QR code desnecessariamente grande no longo prazo.

				Alternativas mais viáveis para produção seriam (1) uma classe contêiner especialmente feita para segurar
				esses dados ou (2) um vetor simples com os itens cuidadosamente posicionados. Um problema com a primeira
				é ter de sincronizar as definições das classes em ambos os programas (além de desistir de
				retrocompatibilidade com qualquer mínima alteração); e a segunda só falta de legibilidade mesmo.
			*/
			config.put("host", InetAddress.getLocalHost().getHostAddress());
			config.put("port", String.valueOf(LISTEN_PORT));
			config.put("form", String.join(",", Main.form_data));

			ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();
			ObjectOutputStream output_stream = new ObjectOutputStream(byte_stream);
			output_stream.writeObject(config); output_stream.flush();

			return byte_stream.toByteArray();
		}
		catch (IOException e) { throw new RuntimeException(e); }
	}

	@SuppressWarnings("unused")
	public void end_process()
	{
		listenNetwork.keep_listening = false;
		executor.shutdown();

		if(Main.debug) System.out.println("End server process");
	}
}

class PingNetwork implements Runnable
{
	@Override
	public void run()
	{
		if(Main.debug) System.out.println("Sending discovery package");
		try( DatagramSocket datagram = new DatagramSocket() )
		{
			datagram.setBroadcast(true);
			String message = "DISCOVERY_S";
			InetAddress ip_broadcast = InetAddress.getByName("255.255.255.255"); // IP de broadcast UDP. 224.0.2.60 também pode funcionar.

			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip_broadcast, Server.SEND_PORT);
			datagram.send(packet);
		}
		catch (IOException e) { throw new RuntimeException(e); }

		if(Main.debug) System.out.println("Pinging intnet");
		for(InetAddress client : Server.tcp_clients)
		{
			try( Socket socket = new Socket(client, Server.SEND_PORT) )
			{
				if(Main.debug) System.out.println("Pinging: " + client);
				OutputStream output = socket.getOutputStream();
				output.write("OIIIIIIIIii".getBytes());
			}
			catch (IOException e) { throw new RuntimeException(e); }
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
	public boolean keep_listening = true; // Que tipo de linguagem usa "boolean" em vez de "bool" vsffff

	@Override
	public void run()
	{
		try
		{
			do
			{
				Socket client_socket = Server.server_socket.accept(); if(Main.debug) System.out.println("Received package: " + client_socket.getInetAddress());
				ObjectInputStream input = new ObjectInputStream(client_socket.getInputStream());
				@SuppressWarnings("unchecked") HashMap<String, String> body = (HashMap<String, String>) input.readObject();

				switch (body.get("request_type"))
				{
					case "DISCOVERY_C":
						Server.tcp_clients.add(client_socket.getInetAddress());
						if(Main.debug) System.out.println("Connected to: " + client_socket.getInetAddress() + " (" + Server.tcp_clients + ")");
						break;
					case "ATTENDANCE_COUNT":
						Main.set_attendance(body.get("echo_id"));
						break;
					case "GLOBAL_CHAT_GET":

					case "GLOBAL_CHAT_PUT":
					case "REGISTER_NEW_USER_I":
					default: if(Main.debug) System.out.println("Err.: Invalid request type: " + body); break;
				}
			}
			while (keep_listening);
		}
		catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }
	}
}