import NetworkHandler.Server;
import io.nayuki.qrcodegen.QrCode;

import java.awt.image.BufferedImage;

public class Main
{
	public static final boolean debug = true;

	@SuppressWarnings("unused") public static final Dashboard window = new Dashboard();
	static Server server = new Server(debug, window.echoes_list_model); // Tem como ser mais explícito que isso???

	public static void main(String[] args)
	{
		if(debug) System.out.println("Program started");
	}

	public static BufferedImage get_data_format_qr()
	{
		return QrCode.encodeBinary(Server.get_data_register_binaries(), QrCode.Ecc.LOW).toImage(10, 1);
	}

	public static void add_to_ui_list(String name)
	{
		window.append_echoes_list(name);
	}

	public static void shut()
	{
		if(debug) System.out.println("---------------------\nStarting closing procedure");
		server.shut();
	}
}