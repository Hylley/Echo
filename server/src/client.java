import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

class Client
{
	static boolean keep_listening = true;
	static int listen_port = 6969;
	static int send_port = 6968;

	public static InetAddress server_address;

	public static void main(String[] args) throws IOException {
		try (DatagramSocket socket = new DatagramSocket(listen_port))
		{
			byte[] data_buffer = new byte[1024];

			while (keep_listening)
			{
				DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
				socket.receive(received_packet);

				String message = new String(received_packet.getData(), received_packet.getOffset(), received_packet.getLength());
				server_address = received_packet.getAddress();

				System.out.println(server_address.getHostAddress());

				if(!message.equals("ATTENDANCE_COUNT") || server_address == null) continue;

				pingServer(); 
			}
		}
	}

	static void pingServer() throws IOException
	{
		if(server_address == null) return;

		DatagramSocket socket = new DatagramSocket();
		String message = "girlhood2nt94";

		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), server_address, send_port);
		socket.send(packet);
		socket.close();
	}
}