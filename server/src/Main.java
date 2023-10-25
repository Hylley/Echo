import java.util.List;

import java.awt.image.BufferedImage;
import io.nayuki.qrcodegen.QrCode;

public final class Main /*
	A classe Main serve tanto como ponto de partida como também ponto intermediário do programa. Nem o servidor precisa
	saber o que acontece na janela; nem a janela precisa saber o que acontece no servidor.
*/
{
	@SuppressWarnings("unused")
	public static final Server server = new Server();
	public static final Dashboard window = new Dashboard();
	public static String[] form_data = new String[]{ "matrícula", "nome" };

	public static final boolean debug = true;

	public static void main(String[] args) { }

	@SuppressWarnings("unused")
	public static List<Eco> get_all_registered_users() { return null; }

	public static void set_attendance(String echo_id)
	{
		window.update_echoes_list(new String[]{ echo_id });
		if(debug) System.out.println("E: " + echo_id);
	}

	public static BufferedImage get_data_format_qr()
	{
		return QrCode.encodeBinary(Server.get_data_register_binaries(), QrCode.Ecc.LOW).toImage(10, 1);
	}
}