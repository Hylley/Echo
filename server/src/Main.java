import NetworkHandler.Server;
import io.nayuki.qrcodegen.QrCode;

import java.awt.image.BufferedImage;

public class Main
{
	public static final boolean debug = true;

	static Server server = new Server(debug); // Tem como ser mais explícito que isso???
	@SuppressWarnings("unused") public static final Dashboard window = new Dashboard();

	public static void main(String[] args)
	{
		if(debug) System.out.println("Program started");
	}

	public static BufferedImage get_data_format_qr()
	{
		return QrCode.encodeBinary(Server.get_data_register_binaries(), QrCode.Ecc.LOW).toImage(10, 1);
	}

	public static void shut()
	{
		if(debug) System.out.println("---------------------\nStarting closing procedure");
		server.shut();
	}
}