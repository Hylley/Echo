import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

class Client
{
	static boolean keep_listening = true;
	static int listen_port = 6969;
	// static int send_port = 6968;

	static InetAddress server_address;

	public static void main(String[] args) throws IOException {
		try(DatagramSocket socket = new DatagramSocket(listen_port))
		{
			byte[] data_buffer = new byte[1024];

			while(keep_listening)
			{
				DatagramPacket received_packet = new DatagramPacket(data_buffer, data_buffer.length);
				socket.receive(received_packet);

				String message = new String(received_packet.getData(), 0, received_packet.getLength());
				Client.server_address = received_packet.getAddress();

				System.out.println("Received message from server: " + message);
			}
		}
	}
}