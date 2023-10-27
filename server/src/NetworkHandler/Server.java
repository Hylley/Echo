package NetworkHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Server
{
	//region Constants
	public static final int DISCOVERY_BROADCAST_PERIOD = 1;
	public static final int SEND_PORT = 6969;
	public static final int LISTEN_PORT = 6968;
	public static boolean debug;
	//endregion

	//region Composite classes
	public PingNetwork ping_net;
	public ListenNetwork listen_net;
	//endregion

	public Server(boolean debug)
	{
		try
		{
			this.ping_net   = new PingNetwork(DISCOVERY_BROADCAST_PERIOD, SEND_PORT);
			this.listen_net = new ListenNetwork(new ServerSocket(LISTEN_PORT), this);
		} catch (IOException e) { throw new RuntimeException(e); }

		this.ping_net.start();
		this.listen_net.start();

		Server.debug = debug;
		if(debug) System.out.println("Server instance started");
	}

	private static final List<Echo> connections = new CopyOnWriteArrayList<>(); // Thread-safe API; eu fiz o meu dever de casa ;)

	public void connect(Socket new_socket, String id) /*
		Quando o ListenNetwork escutar uma nova conexão, esse método estático vai ser chamado.
	*/
	{
		Echo new_echo = new Echo(new_socket, id);
		new_echo.start();
		Server.connections.add(new_echo);
	}

	public static int connections() { return connections.size(); }

	public static void handle_request(HashMap<String, String> body, Echo origin) /*
		Quando uma thread cliente receber um novo pacote, esse método estático vai ser chamado.
	*/
	{
		switch (body.get("request_type"))
		{
			case "GLOBAL_TEXT_MESSAGE":
				HashMap<String, String> message = new HashMap<>();
				message.put("name", origin.id);
				message.put("text", body.get("text"));

				for(Echo echo : connections) Echo.send(echo, message);
				break;
			case "ATTENDANCE_COUNT":
			case "REGISTER_NEW_USER":
			default: System.out.println("Err: Invalid request type;"); break;
		}

		if(debug) System.out.println("New request [" + body.get("request_type") + "] from " + origin.socket.getInetAddress());
	}

	public static byte[] get_data_register_binaries()/*
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
			config.put("port", String.valueOf(Server.LISTEN_PORT));

			ByteArrayOutputStream byte_stream = new ByteArrayOutputStream();
			ObjectOutputStream output_stream = new ObjectOutputStream(byte_stream);
			output_stream.writeObject(config); output_stream.flush();

			return byte_stream.toByteArray();
		}
		catch (IOException e) { throw new RuntimeException(e); }
	}

	public void shut()
	{
		ping_net.shut();
		listen_net.shut();
		for(Echo echo : connections) Echo.shut(echo);
		if(debug) System.out.println("Closing Server successfully");
	}
}