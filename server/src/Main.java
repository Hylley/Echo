import javax.swing.*;
import java.net.UnknownHostException;

public final class Main {
	public static void main(String[] args) throws UnknownHostException
	{
		Server server = new Server();
		JFrame window = new Dashboard();
	}
}