import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.IOException;

class Client
{
	public static void main(String[] args) throws IOException
	{
		DatagramSocket ds = new DatagramSocket();
		String str = "Sou lindo";
		InetAddress ip = InetAddress.getByName("255.255.255.255");

		DatagramPacket dp = new DatagramPacket(str.getBytes(), str.length(), ip, 6969);
		ds.send(dp);
		ds.close();
	}
}