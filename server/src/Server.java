import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;


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

	PingNetwork pingNetwork = new PingNetwork(); // Envia pacotes para toda a rede (UDP) —> thread principal;
	ListenNetwork listenNetwork = new ListenNetwork(); // Recebe pacotes dos clientes individualmente (TCP) —> thread paralela.
	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	public Server()
	{
		try
		{
			System.out.println("Hosting at:  	" + InetAddress.getLocalHost().getHostAddress() + " " + SEND_PORT);
			System.out.println("Listening at:	" + InetAddress.getLocalHost().getHostAddress() + " " + LISTEN_PORT);
		}
		catch (UnknownHostException e) { throw new RuntimeException(e); }

		executor.scheduleAtFixedRate(pingNetwork, 0, BROADCAST_PERIOD_IN_SECONDS, TimeUnit.SECONDS);
		listenNetwork.start();
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
				retrocompatibilidade com qualquer mínima alteração); e a segunda só falta legibilidade mesmo.
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
	}
}

class PingNetwork implements Runnable
{
	@Override
	public void run()
	{
		try( DatagramSocket socket = new DatagramSocket() )
		{
			socket.setBroadcast(true);
			String message = "ATTENDANCE_COUNT";
			InetAddress ip_broadcast = InetAddress.getByName("255.255.255.255"); // IP de broadcast UDP.

			DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), ip_broadcast, Server.SEND_PORT);
			socket.send(packet);
		}
		catch (IOException e) { throw new RuntimeException(e); }
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
		try( ServerSocket server_socket = new ServerSocket(Server.LISTEN_PORT) )
		{
			do
			{
				Socket client_socket = server_socket.accept();
				ObjectInputStream input = new ObjectInputStream(client_socket.getInputStream());
				@SuppressWarnings("unchecked") HashMap<String, String> body = (HashMap<String, String>) input.readObject();

				switch (body.get("request_type"))
				{
					case "ATTENDANCE_COUNT":
						Main.set_attendance(body.get("echo_id"));
						break;
					case "REGISTER_NEW_USER_I":
					case "GLOBAL_TEXT_MESSAGE":
					case "GLOBAL_FILE_MESSAGE":
					default: System.out.println("Err: Invalid request type;"); break;
				}
			}
			while (keep_listening);
		}
		catch (IOException | ClassNotFoundException e) { throw new RuntimeException(e); }
	}
}